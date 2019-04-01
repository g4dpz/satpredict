package com.badgersoft.satpredict.service;

import com.badgersoft.satpredict.client.dto.PassesDTO;
import com.badgersoft.satpredict.client.dto.SatelliteCharacter;
import com.badgersoft.satpredict.client.dto.SatelliteCharacteristics;
import com.badgersoft.satpredict.dao.AliasDao;
import com.badgersoft.satpredict.dao.TleDao;
import com.badgersoft.satpredict.domain.TleEntity;
import com.badgersoft.satpredict.utils.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.me.g4dpz.satellite.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SatelliteServiceImpl implements SatelliteService {

    private static final Logger LOG = LoggerFactory.getLogger(TleUpdateServiceImpl.class);

    private static String STELLARSTATION_API_KEY = null;

    @Autowired
    TleDao tleDao;

    @Autowired
    AliasDao aliasDao;

    @Autowired
    Cache cache;

    static {
        initApiKey();
    }

    @Override
    public void reserveStellarStationSlots(String... noradeIdList) {
        LOG.info(STELLARSTATION_API_KEY);
    }

    @Override
    public SatelliteCharacteristics getSatelliteCharacteristics() {
        Iterator<TleEntity> iterator = tleDao.findAll().iterator();

        ConcurrentMap<Long, SatelliteCharacter> satelliteCharacterMap = new ConcurrentHashMap<>();

        while (iterator.hasNext()) {
            TleEntity tleEntity = iterator.next();
            long catnum = tleEntity.getCatnum();
            SatelliteCharacter satelliteCharacter = new SatelliteCharacter();
            satelliteCharacter.setCatnum(catnum);
            satelliteCharacter.setAliases(tleEntity.getAliases());
            satelliteCharacterMap.put(catnum,satelliteCharacter);
        }

        List<SatelliteCharacter> satelliteCharacterList = new ArrayList<>();

        Iterator<Map.Entry<Long, SatelliteCharacter>> satelliteMapIterator
                = satelliteCharacterMap.entrySet().iterator();

        while (satelliteMapIterator.hasNext()) {
            satelliteCharacterList.add(satelliteMapIterator.next().getValue());
        }

        SatelliteCharacteristics characteristics = new SatelliteCharacteristics();
        characteristics.setCharacters(satelliteCharacterList);
        return characteristics;
    }

    @Override
    public SatelliteCharacter getSatelliteCharacter(Long catnum) {
        SatelliteCharacter satelliteCharacter = new SatelliteCharacter();

        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            }
            else {
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        satelliteCharacter.setAliases(tleEntity.getAliases());
        satelliteCharacter.setCatnum(catnum);
        return satelliteCharacter;
    }

    @Override
    public SatPos getPosition(Long catnum, double latitude, double longitude, double altitude) {
        SatPos satPos;

        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            }
            else {
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        String[] lines = new String[] {tleEntity.getLine1(), tleEntity.getLine2(), tleEntity.getLine3()};

        TLE tle = new TLE(lines);

        Satellite satellite = SatelliteFactory.createSatellite(tle);

        Date now = new Date(System.currentTimeMillis());
        satPos = satellite.getPosition(new GroundStationPosition(latitude, longitude, altitude), now);
        return satPos;
    }

    @Override
    public PassesDTO getPassesDTO(Long catnum, Double latitude, Double longitude, Double altitude, Integer hours) throws SatNotFoundException, InvalidTleException {
        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            }
            else {
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        String[] lines = new String[] {tleEntity.getLine1(), tleEntity.getLine2(), tleEntity.getLine3()};

        TLE tle = new TLE(lines);

        Date now = new Date(System.currentTimeMillis());
        PassPredictor passPredictor;


            passPredictor = new PassPredictor(tle, new GroundStationPosition(latitude, longitude, altitude));
            List<SatPassTime> passes = passPredictor.getPasses(now, hours, true);
            PassesDTO passesDTO = new PassesDTO(passes);
            return passesDTO;

    }

    private static void initApiKey() {
        try {
            BufferedReader txtReader = new BufferedReader(new InputStreamReader(TleUpdateServiceImpl.class.getResourceAsStream("/stellarstation-private-key.json")));
            StringBuffer lines = new StringBuffer();
            String strLine;
            while ((strLine = txtReader.readLine()) != null)   {
                lines.append(strLine).append("\n");
            }
            STELLARSTATION_API_KEY = lines.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
