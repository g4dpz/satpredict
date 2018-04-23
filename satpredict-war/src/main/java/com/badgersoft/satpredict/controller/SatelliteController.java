package com.badgersoft.satpredict.controller;

import com.badgersoft.satpredict.dao.AliasDao;
import com.badgersoft.satpredict.dao.TleDao;
import com.badgersoft.satpredict.domain.TleEntity;
import com.badgersoft.satpredict.dto.SatPosDTO;
import com.badgersoft.satpredict.shared.PassesDTO;
import com.badgersoft.satpredict.shared.SatelliteCharacter;
import com.badgersoft.satpredict.shared.SatelliteCharacteristics;
import com.badgersoft.satpredict.utils.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uk.me.g4dpz.satellite.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by davidjohnson on 30/08/2016.
 */
@RestController
@RequestMapping("/satellite")
public class SatelliteController {

    @Autowired
    TleDao tleDao;

    @Autowired
    AliasDao aliasDao;

    @Autowired
    Cache cache;

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SatelliteCharacteristics getCharacterstics(HttpServletRequest request,
                                                      HttpServletResponse response) {

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

        response.setStatus(HttpServletResponse.SC_OK);

        return characteristics;

    }

    @RequestMapping(value = "/info/{catnum}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SatelliteCharacter getCharacterstics(
            @PathVariable(value = "catnum") Long catnum,
            HttpServletRequest request,
            HttpServletResponse response) {

        SatelliteCharacter satelliteCharacter = new SatelliteCharacter();

        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        satelliteCharacter.setAliases(tleEntity.getAliases());
        satelliteCharacter.setCatnum(catnum);
        response.setStatus(HttpServletResponse.SC_OK);

        return satelliteCharacter;

    }

    @RequestMapping(value = "/position/{catnum}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SatPosDTO getPosition(
            @PathVariable(value = "catnum") Long catnum,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "altitude") Double altitude,
            HttpServletRequest request,
            HttpServletResponse response) {

        SatPos satPos;

        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        String[] lines = new String[] {tleEntity.getLine1(), tleEntity.getLine2(), tleEntity.getLine3()};

        TLE tle = new TLE(lines);

        Satellite satellite = SatelliteFactory.createSatellite(tle);

        Date now = new Date(System.currentTimeMillis());
        satPos = satellite.getPosition(new GroundStationPosition(latitude, longitude, altitude), now);

        response.setStatus(HttpServletResponse.SC_OK);

        return new SatPosDTO(
            satPos.getLatitude(),
            satPos.getLongitude(),
            satPos.getAltitude(),
            satPos.getAzimuth(),
            satPos.getElevation(),
            satPos.getRange(),
            satPos.getRangeRate()
        );

    }

    @RequestMapping(value = "/predict/{catnum}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PassesDTO predict(
            @PathVariable(value = "catnum") Long catnum,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "altitude") Double altitude,
            @RequestParam(value = "hours") Integer hours,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        String[] lines = new String[] {tleEntity.getLine1(), tleEntity.getLine2(), tleEntity.getLine3()};

        TLE tle = new TLE(lines);

        Date now = new Date(System.currentTimeMillis());
        PassPredictor passPredictor;

        try {
            passPredictor = new PassPredictor(tle, new GroundStationPosition(latitude, longitude, altitude));
            List<SatPassTime> passes = passPredictor.getPasses(now, hours, true);
            PassesDTO passesDTO = new PassesDTO(passes);
            response.setStatus(HttpServletResponse.SC_OK);
            return passesDTO;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }
}
