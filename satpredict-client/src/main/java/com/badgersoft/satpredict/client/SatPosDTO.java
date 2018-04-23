package com.badgersoft.satpredict.client;

import java.io.Serializable;

/**
 * Created by davidjohnson on 05/09/2016.
 */
public class SatPosDTO implements Serializable {

    private String latitude = "0.0";
    private String longitude = "0.0";
    private String altitude = "0.0";
    private String azimuth = "0.0";
    private String elevation = "0.0";
    private String range = "0.0";
    private String rangeRate = "0.0";

    public SatPosDTO() {
        
    }

    public SatPosDTO(
            double latitude,
            double longitude,
            double altitude,
            double azimuth,
            double elevation,
            double range,
            double rangeRate
    ) {
        this.latitude = rad2deg(latitude);
        this.longitude = rad2deg(longitude);
        this.altitude = to2dp(altitude);
        this.azimuth = rad2deg(azimuth);
        this.elevation = rad2deg(elevation);
        this.range = to2dp(range);
        this.rangeRate = to2dp(rangeRate);
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setRangeRate(String rangeRate) {
        this.rangeRate = rangeRate;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public String getElevation() {
        return elevation;
    }

    public String getRange() {
        return range;
    }

    public String getRangeRate() {
        return rangeRate;
    }

    private String rad2deg(double value) {
        return to2dp((value / (2.0 * Math.PI)) * 360.0);
    }

    private String to2dp(double value) {
        return String.format("%.2f", value);
    }
}
