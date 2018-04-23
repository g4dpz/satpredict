package com.badgersoft.satpredict.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by davidjohnson on 05/09/2016.
 */
public class PassDTO implements Serializable {

    private Date startTime;
    private Date endTime;
    private int aosAzimuth;
    private Date tca;
    private int losAzimuth;
    private double maxEl;
    String polePassed;

    public PassDTO() {}

    public PassDTO(Date startTime, Date endTime, int aosAzimuth, Date tca, int losAzimuth, double maxEl, String polePassed) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.aosAzimuth = aosAzimuth;
        this.tca = tca;
        this.losAzimuth = losAzimuth;
        this.maxEl = maxEl;
        this.polePassed = polePassed;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getAosAzimuth() {
        return aosAzimuth;
    }

    public void setAosAzimuth(int aosAzimuth) {
        this.aosAzimuth = aosAzimuth;
    }

    public Date getTca() {
        return tca;
    }

    public void setTca(Date tca) {
        this.tca = tca;
    }

    public int getLosAzimuth() {
        return losAzimuth;
    }

    public void setLosAzimuth(int losAzimuth) {
        this.losAzimuth = losAzimuth;
    }

    public double getMaxEl() {
        return maxEl;
    }

    public void setMaxEl(double maxEl) {
        this.maxEl = maxEl;
    }

    public String getPolePassed() {
        return polePassed;
    }

    public void setPolePassed(String polePassed) {
        this.polePassed = polePassed;
    }
}
