package com.badgersoft.satpredict.controller;

import com.badgersoft.satpredict.dao.AliasDao;
import com.badgersoft.satpredict.dao.TleDao;
import com.badgersoft.satpredict.dao.TleSourceDao;
import com.badgersoft.satpredict.dto.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by davidjohnson on 16/06/2016.
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    private static final String template = "Version: %s, Satellite TLEs: %d, Tle Sources: %d, Aliases: %d";
    private final AtomicLong counter = new AtomicLong();
    @Value("${version.number}")
    String versionNumber;

    @Autowired
    TleDao tleDao;

    @Autowired
    TleSourceDao tleSourceDao;

    @Autowired
    AliasDao aliasDao;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    Greeting sayHello() {

        Long countTles = tleDao.count();
        Long countSourceTles = tleSourceDao.count();
        Long countAliases = aliasDao.count();

        return new Greeting(counter.incrementAndGet(),
                String.format(template, versionNumber, countTles, countSourceTles, countAliases));
    }
}
