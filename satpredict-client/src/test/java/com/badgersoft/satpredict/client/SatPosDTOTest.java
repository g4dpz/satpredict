package com.badgersoft.satpredict.client;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class SatPosDTOTest
{

    public static final String ALTITUDE = "333.33";
    public static final String AZIMUTH = "-255.35";
    public static final String ELEVATION = "31.83";
    public static final String LATITUDE = "63.66";
    public static final String LONGITUDE = "127.32";
    public static final String RANGE = "2345.68";
    public static final String RANGE_RATE = "-2.35";

    @Test
    public void construction()
    {
        double latitude = 1.11111;
        double longitude = 2.22222;
        double altitude = 333.33333;
        double azimuth = -4.45678;
        double elevation = 0.5555;
        double range = 2345.678;
        double rangeRate = -2.345;

        SatPosDTO satPosDTO = new SatPosDTO(latitude, longitude, altitude, azimuth, elevation, range, rangeRate);
        assertEquals(ALTITUDE, satPosDTO.getAltitude());
        assertEquals(AZIMUTH, satPosDTO.getAzimuth());
        assertEquals(ELEVATION, satPosDTO.getElevation());
        assertEquals(LATITUDE, satPosDTO.getLatitude());
        assertEquals(LONGITUDE, satPosDTO.getLongitude());
        assertEquals(RANGE, satPosDTO.getRange());
        assertEquals(RANGE_RATE, satPosDTO.getRangeRate());
    }
}
