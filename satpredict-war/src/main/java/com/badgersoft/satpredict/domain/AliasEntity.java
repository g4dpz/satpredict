package com.badgersoft.satpredict.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by davidjohnson on 28/08/2016.
 */
@Entity
@Table(name = "alias", catalog = "satellite")
@IdClass(AliasEntity.class)
public class AliasEntity implements Serializable {

    @Id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tle_catnum")
    private TleEntity tle;

    @Id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tle_source_id")
    private TleSourceEntity source;

    private Long priority;

    @Column(name = "created_date")
    private Date createdDate;

    private String name;

    public TleEntity getTle() {
        return tle;
    }

    public void setTle(TleEntity tle) {
        this.tle = tle;
    }

    public TleSourceEntity getSource() {
        return source;
    }

    public void setSource(TleSourceEntity source) {
        this.source = source;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AliasEntity)) return false;

        AliasEntity that = (AliasEntity) o;

        if (!tle.equals(that.tle)) return false;
        if (!source.equals(that.source)) return false;
        if (!priority.equals(that.priority)) return false;
        if (!createdDate.equals(that.createdDate)) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = tle.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + priority.hashCode();
        result = 31 * result + createdDate.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
