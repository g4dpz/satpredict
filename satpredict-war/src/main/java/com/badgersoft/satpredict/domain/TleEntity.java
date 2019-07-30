package com.badgersoft.satpredict.domain;

import org.apache.commons.lang3.StringUtils;
import uk.me.g4dpz.satellite.TLE;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tle", catalog = "satellite")
public class TleEntity implements Serializable {

    @Id
    private long catnum;
    private Date createddate;
    private Date updateddate;

    private String line1;
    private String line2;
    private String line3;

    private double argper;
    private double bstar;
    private boolean deepspace;
    private double drag;
    private double eccn;
    private double eo  ;
    private double epoch;
    private double incl;
    private double meanan;
    private double meanmo;
    private double xmo  ;
    private String name;
    private double nddot6;
    private double omega;
    private long orbitnum;
    private double raan;
    private double refepoch;
    private double setnum;
    private double xincl;
    private double xndt2o;
    private double xno;
    private double xnodeo;
    private long year;


    @OneToMany(mappedBy = "tle", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AliasEntity> aliases = new ArrayList<>(0);

    @ManyToOne
    @JoinColumn(name = "constellation_id")
    private ConstellationEntity constellation;


    public TleEntity() {

    }

    public TleEntity(final String[] lines, Date updatedDate, List<AliasEntity>aliases) {
        this.setCatnum(getCatnumFromLine(lines[1]));
        replaceContent(lines, updatedDate);
        setAliases(aliases);
    }

    public long getCatnum() {
        return catnum;
    }

    public void setCatnum(long catnum) {
        this.catnum = catnum;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    public Date getUpdateddate() {
        return updateddate;
    }

    public void setUpdateddate(Date updateddate) {
        this.updateddate = updateddate;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public List<AliasEntity> getAliases() {
        return aliases;
    }

    public void setAliases(List<AliasEntity> aliases) {
        this.aliases = aliases;
    }

    public void replaceContent(final String[] lines, Date updateDate) {
        line1 = lines[0];
        line2 = lines[1];
        line3 = lines[2];
        
        TLE tle = new TLE(lines);
        this.argper = tle.getArgper();
        this.bstar = tle.getBstar();
        this.deepspace = tle.isDeepspace();
        this.drag = tle.getDrag();
        this.eccn = tle.getEccn();
        this.eo = tle.getEo();
        this.epoch = tle.getEpoch();
        this.incl = tle.getIncl();
        this.meanan = tle.getMeanan();
        this.meanmo = tle.getMeanmo();
        this.xmo = tle.getXmo();
        this.name = tle.getName();
        this.nddot6 = tle.getNddot6();
        this.omega = tle.getOmegao();
        this.orbitnum = tle.getOrbitnum();
        this.raan = tle.getRaan();
        this.refepoch = tle.getRefepoch();
        this.setnum = tle.getSetnum();
        this.xincl = tle.getXincl();
        this.xndt2o = tle.getXndt2o();
        this.xno = tle.getXno();
        this.xnodeo = tle.getXnodeo();
        this.year = tle.getYear();
        
        this.updateddate = updateDate;
    }

    public double getArgper() {
        return argper;
    }

    public void setArgper(double argper) {
        this.argper = argper;
    }

    public double getBstar() {
        return bstar;
    }

    public void setBstar(double bstar) {
        this.bstar = bstar;
    }

    public boolean isDeepspace() {
        return deepspace;
    }

    public void setDeepspace(boolean deepspace) {
        this.deepspace = deepspace;
    }

    public double getDrag() {
        return drag;
    }

    public void setDrag(double drag) {
        this.drag = drag;
    }

    public double getEccn() {
        return eccn;
    }

    public void setEccn(double eccn) {
        this.eccn = eccn;
    }

    public double getEo() {
        return eo;
    }

    public void setEo(double eo) {
        this.eo = eo;
    }

    public double getEpoch() {
        return epoch;
    }

    public void setEpoch(double epoch) {
        this.epoch = epoch;
    }

    public double getIncl() {
        return incl;
    }

    public void setIncl(double incl) {
        this.incl = incl;
    }

    public double getMeanan() {
        return meanan;
    }

    public void setMeanan(double meanan) {
        this.meanan = meanan;
    }

    public double getMeanmo() {
        return meanmo;
    }

    public void setMeanmo(double meanmo) {
        this.meanmo = meanmo;
    }

    public double getXmo() {
        return xmo;
    }

    public void setXmo(double xmo) {
        this.xmo = xmo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getNddot6() {
        return nddot6;
    }

    public void setNddot6(double nddot6) {
        this.nddot6 = nddot6;
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega(double omega) {
        this.omega = omega;
    }

    public long getOrbitnum() {
        return orbitnum;
    }

    public void setOrbitnum(long orbitnum) {
        this.orbitnum = orbitnum;
    }

    public double getRaan() {
        return raan;
    }

    public void setRaan(double raan) {
        this.raan = raan;
    }

    public double getRefepoch() {
        return refepoch;
    }

    public void setRefepoch(double refepoch) {
        this.refepoch = refepoch;
    }

    public double getSetnum() {
        return setnum;
    }

    public void setSetnum(double setnum) {
        this.setnum = setnum;
    }

    public double getXincl() {
        return xincl;
    }

    public void setXincl(double xincl) {
        this.xincl = xincl;
    }

    public double getXndt2o() {
        return xndt2o;
    }

    public void setXndt2o(double xndt2o) {
        this.xndt2o = xndt2o;
    }

    public double getXno() {
        return xno;
    }

    public void setXno(double xno) {
        this.xno = xno;
    }

    public double getXnodeo() {
        return xnodeo;
    }

    public void setXnodeo(double xnodeo) {
        this.xnodeo = xnodeo;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    @Transient
    private long getCatnumFromLine(final String line) {
        return Long.parseLong(StringUtils.strip(line.substring(2, 7)));
    }

    public ConstellationEntity getConstellation() {
        return constellation;
    }

    public void setConstellation(ConstellationEntity constellation) {
        this.constellation = constellation;
    }
}
