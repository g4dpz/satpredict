package com.badgersoft.satpredict.shared;

import uk.me.g4dpz.satellite.SatPassTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidjohnson on 05/09/2016.
 */
public class PassesDTO implements Serializable {

    List<PassDTO> passList = new ArrayList<>(0);

    public PassesDTO() {}

    public PassesDTO(List<SatPassTime> passes) {

        for (SatPassTime satPassTime : passes) {
            passList.add(new PassDTO(
                    satPassTime.getStartTime(),
                    satPassTime.getEndTime(),
                    satPassTime.getAosAzimuth(),
                    satPassTime.getTCA(),
                    satPassTime.getLosAzimuth(),
                    satPassTime.getMaxEl(),
                    satPassTime.getPolePassed()
            )
            );
        }

        return;
    }

    public List<PassDTO> getPassList() {
        return passList;
    }

    public void setPassList(List<PassDTO> passList) {
        this.passList = passList;
    }
}
