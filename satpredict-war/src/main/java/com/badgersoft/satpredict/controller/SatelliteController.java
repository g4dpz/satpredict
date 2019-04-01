package com.badgersoft.satpredict.controller;

import com.badgersoft.satpredict.client.dto.PassesDTO;
import com.badgersoft.satpredict.client.dto.SatelliteCharacter;
import com.badgersoft.satpredict.client.dto.SatelliteCharacteristics;
import com.badgersoft.satpredict.dto.SatPosDTO;
import com.badgersoft.satpredict.service.SatelliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uk.me.g4dpz.satellite.SatPos;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by davidjohnson on 30/08/2016.
 */
@RestController
@RequestMapping("/satellite")
public class SatelliteController {

    @Autowired
    SatelliteService satelliteService;

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SatelliteCharacteristics getCharacterstics(HttpServletRequest request,
                                                      HttpServletResponse response) {

        SatelliteCharacteristics characteristics = satelliteService.getSatelliteCharacteristics();

        response.setStatus(HttpServletResponse.SC_OK);

        return characteristics;

    }

    @RequestMapping(value = "/info/{catnum}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SatelliteCharacter getCharacterstics(
            @PathVariable(value = "catnum") Long catnum,
            HttpServletRequest request,
            HttpServletResponse response) {

        SatelliteCharacter satelliteCharacter = satelliteService.getSatelliteCharacter(catnum);

        if (satelliteCharacter != null) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }


        return satelliteCharacter;

    }

    @RequestMapping(value = "/position/{catnum}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SatPosDTO getPosition(
            @PathVariable(value = "catnum") Long catnum,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "altitude") Double altitude,
            HttpServletRequest request,
            HttpServletResponse response) {

        SatPos satPos = satelliteService.getPosition(catnum, latitude, longitude, altitude);

        if (satPos == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

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

    @RequestMapping(value = "/predict/{catnum}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

        try {
            PassesDTO passesDTO = satelliteService.getPassesDTO(catnum, latitude, longitude, altitude, hours);

            if (passesDTO == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            else {
                response.setStatus(HttpServletResponse.SC_OK);
                return passesDTO;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }
}
