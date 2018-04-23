package com.badgersoft.satpredict.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidjohnson on 30/08/2016.
 */
public class SatelliteCharacteristics implements Serializable {

    private List<SatelliteCharacter> characters = new ArrayList<>();

    public SatelliteCharacteristics() {
    }

    public List<SatelliteCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<SatelliteCharacter> aliases) {
        this.characters = aliases;
    }
}
