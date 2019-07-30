package com.badgersoft.satpredict.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "constellation", catalog = "satellite")
public class ConstellationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "constellation", fetch = FetchType.EAGER)
    private List<TleEntity> satellites = new ArrayList<>(0);


    public ConstellationEntity(String name, List<TleEntity> satellites) {
        this.name = name;
        this.satellites = satellites;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TleEntity> getSatellites() {
        return satellites;
    }

    public void setSatellites(List<TleEntity> satellites) {
        this.satellites = satellites;
    }
}
