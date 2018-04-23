package com.badgersoft.satpredict.client.dto;

import com.badgersoft.satpredict.domain.AliasEntity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by davidjohnson on 30/08/2016.
 */
public class SatelliteCharacter implements Serializable {

    private Long catnum;

    private List<AliasEntity> aliases = Collections.EMPTY_LIST;

    public SatelliteCharacter() {
    }

    public List<AliasEntity> getAliases() {
        return aliases;
    }

    public void setAliases(List<AliasEntity> aliases) {
        this.aliases = aliases;
    }

    public Long getCatnum() {
        return catnum;
    }

    public void setCatnum(Long catnum) {
        this.catnum = catnum;
    }
}
