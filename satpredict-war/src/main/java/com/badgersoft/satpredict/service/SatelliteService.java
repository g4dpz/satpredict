package com.badgersoft.satpredict.service;

import com.badgersoft.satpredict.client.dto.PassesDTO;
import com.badgersoft.satpredict.client.dto.SatelliteCharacter;
import com.badgersoft.satpredict.client.dto.SatelliteCharacteristics;
import org.springframework.stereotype.Service;
import uk.me.g4dpz.satellite.InvalidTleException;
import uk.me.g4dpz.satellite.SatNotFoundException;
import uk.me.g4dpz.satellite.SatPos;

import java.io.IOException;

@Service
public interface SatelliteService {
    void reserveStellarStationSlots(String... noradeIdList) throws IOException;

    SatelliteCharacteristics getSatelliteCharacteristics();

    SatelliteCharacter getSatelliteCharacter(Long catnum);

    SatPos getPosition(Long catnum, double latitude, double longitude, double altitude);

    PassesDTO getPassesDTO(Long catnum, Double latitude, Double longitude, Double altitude, Integer hours) throws SatNotFoundException, InvalidTleException;
}
