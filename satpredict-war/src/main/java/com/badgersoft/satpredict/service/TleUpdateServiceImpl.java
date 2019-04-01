package com.badgersoft.satpredict.service;

import com.badgersoft.satpredict.dao.AliasDao;
import com.badgersoft.satpredict.dao.TleDao;
import com.badgersoft.satpredict.dao.TleSourceDao;
import com.badgersoft.satpredict.domain.AliasEntity;
import com.badgersoft.satpredict.domain.TleEntity;
import com.badgersoft.satpredict.domain.TleSourceEntity;
import com.badgersoft.satpredict.utils.Clock;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;


/**
 * Created by davidjohnson on 26/08/2016.
 */
@Service
public class TleUpdateServiceImpl implements TleUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(TleUpdateServiceImpl.class);

    @Autowired
    private TleDao tleDao;

    @Autowired
    private TleSourceDao tleSourceDao;

    @Autowired
    private AliasDao aliasDao;

    @Autowired
    Clock clock;

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void doUpdate() {

        Iterator<TleSourceEntity> iterator = tleSourceDao.findByEnabled(true).iterator();

        List<String> messages = new ArrayList<>();

        StringBuffer sb
                = new StringBuffer(String.format("TLEs where updated at: %s\n", new Date(System.currentTimeMillis()).toString()));

        while(iterator.hasNext()) {

            long aliasCount = aliasDao.count();
            long tleCount = tleSourceDao.count();
            long tlesAdded = 0;
            long tlesUpdated = 0;
            long aliasesAdded = 0;

            TleSourceEntity tleSource = iterator.next();
            String tleSourceUrl = tleSource.getUrl();
            Long linesToSkip = tleSource.getSkiplines();
            try {
                URL url = new URL(tleSourceUrl);
                final List<String[]> tmpSatElems = importSat(url.openStream(), linesToSkip);
                for (String[] lines : tmpSatElems) {
                    long catnum = getCatnum(lines[1]);
                    List<TleEntity> tleEntityList = tleDao.findByCatnum(catnum);
                    if (tleEntityList != null) {
                        TleEntity tleEntity;
                        Date now = Calendar.getInstance().getTime();
                        if (tleEntityList.isEmpty()) {
                            LOG.debug(String.format("Adding: %s", lines[0].trim()));
                            tleEntity = new TleEntity(lines, now, Collections.EMPTY_LIST);
                            tleEntity.setCreateddate(now);

                            AliasEntity alias = new AliasEntity();
                            alias.setTle(tleEntity);
                            alias.setSource(tleSource);
                            alias.setCreatedDate(now);
                            alias.setName(lines[0].trim());
                            alias.setPriority(10L);
                            tleEntity.setAliases(Collections.singletonList(alias));
                            ++tlesAdded;
                            ++aliasesAdded;
                        }
                        else {
                            tleEntity = tleEntityList.get(0);
                            int year = Integer.parseInt(lines[1].substring(18, 20));
                            double refEpoch = Double.parseDouble(lines[1].substring(20, 32));
                            if (year > tleEntity.getYear() || (year == tleEntity.getYear() && refEpoch > tleEntity.getRefepoch())) {
                                LOG.debug(String.format("Replacing: %s", lines[0].trim()));
                                tleEntity.replaceContent(lines, now);
                                tleEntity.setYear(year);
                                tleEntity.setRefepoch(refEpoch);
                                tlesUpdated++;
                            }
                            List<AliasEntity> aliases = tleEntity.getAliases();
                            Iterator<AliasEntity> aliasIterator = aliases.iterator();
                            boolean matchingAlias = false;
                            while (aliasIterator.hasNext()) {
                                AliasEntity aliasEntity = aliasIterator.next();
                                if (aliasEntity.getName().equals(lines[0].trim())) {
                                    matchingAlias = true;
                                    break;
                                }
                            }
                            if (!matchingAlias) {
                                AliasEntity alias = new AliasEntity();
                                alias.setTle(tleEntity);
                                alias.setSource(tleSource);
                                alias.setCreatedDate(now);
                                alias.setName(lines[0].trim());
                                alias.setPriority(0L);
                                tleEntity.getAliases().add(alias);
                                ++aliasesAdded;
                            }
                        }

                        tleSourceDao.save(tleSource);
                        tleDao.save(tleEntity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            sb.append(String.format(
                    "Source: %s, TLE count before processing: %s, TLE updates: %s, TLE additions: %s, " +
                    "Alias count before processing: %s, Alias additions: %s\n",
                    tleSource.getName(), tleCount, tlesAdded, tlesUpdated, aliasCount, aliasesAdded));
        }

        LOG.info(sb.toString());

    }

    public static List<String[]> importSat(InputStream fileIS, Long skipLines) throws IOException {
        Long numerLinesToSkip = skipLines;
        List importedSats = new ArrayList();
        BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
        int j = 0;
        String[] lines = new String[3];

        String readString;
        while((readString = buf.readLine()) != null) {
            if (skipLines-- > 0) {
                continue;
            }
            switch(j) {
                case 0:
                    lines = new String[3];
                case 1:
                    lines[j] = readString;
                    ++j;
                    break;
                case 2:
                    lines[j] = readString;
                    j = 0;
                    importedSats.add(lines);
            }
        }

        return importedSats;
    }

    private long getCatnum(final String line) {
        return Long.parseLong(StringUtils.strip(line.substring(2, 7)));
    }






    /*

        name = tle[0].trim();
        setnum = Integer.parseInt(StringUtils.strip(tle[1].substring(64, 68)));
        year = Integer.parseInt(StringUtils.strip(tle[1].substring(18, 20)));
        refepoch = Double.parseDouble(tle[1].substring(20, 32));
        incl = Double.parseDouble(tle[2].substring(8, 16));
        raan = Double.parseDouble(tle[2].substring(17, 25));
        eccn = 1.0e-07 * Double.parseDouble(tle[2].substring(26, 33));
        argper = Double.parseDouble(tle[2].substring(34, 42));
        meanan = Double.parseDouble(tle[2].substring(43, 51));
        meanmo = Double.parseDouble(tle[2].substring(52, 63));
        drag = Double.parseDouble(tle[1].substring(33, 43));
     */
}
