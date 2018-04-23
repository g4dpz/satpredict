package com.badgersoft.satpredict.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidjohnson on 27/08/2016.
 */
@Entity
@Table(name = "tle_source", catalog = "satellite")
public class TleSourceEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String url;
    private String name;
    private Boolean enabled;
    private Long skiplines;

    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AliasEntity> aliases = new ArrayList<>(0);

    public TleSourceEntity() {
    }

    public TleSourceEntity(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getSkiplines() {
        return skiplines;
    }

    public void setSkiplines(Long skipLines) {
        this.skiplines = skipLines;
    }

    public List<AliasEntity> getAliases() {
        return aliases;
    }

    public void setAliases(List<AliasEntity> aliases) {
        this.aliases = aliases;
    }
}
