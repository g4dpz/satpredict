/*
	This file is part of the FUNcube Data Warehouse
	
	Copyright 2013,2014 (c) David A.Johnson, G4DPZ, AMSAT-UK

    The FUNcube Data Warehouse is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    The FUNcube Data Warehouse is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with The FUNcube Data Warehouse.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.badgersoft.satpredict.config;

import com.badgersoft.satpredict.service.SatelliteService;
import com.badgersoft.satpredict.service.TleUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    public static final String FC1_NORAD_ID = "39444";
    public static final String NAYIF1_NRAD_ID = "42017";
    public static final String JY1SAT_NORAD_ID = "43803";

    private TleUpdateService tleUpdateService;
    private SatelliteService satelliteService;

    @Autowired
    public ScheduleConfig(TleUpdateService tleUpdateService, SatelliteService satelliteService) {
        this.tleUpdateService = tleUpdateService;
        this.satelliteService = satelliteService;
    }

    @Scheduled(initialDelay = 30000, fixedRate = 86400000)
    public void tleProcessorTask() {
        tleUpdateService.doUpdate();
//        try {
//            satelliteService.reserveStellarStationSlots(FC1_NORAD_ID, NAYIF1_NRAD_ID, JY1SAT_NORAD_ID);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
