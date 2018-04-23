// FUNcube Data Warehouse
// Copyright 2013 (c) David A.Johnson, G4DPZ, AMSAT-UK
// This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send a letter
// to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.

package com.badgersoft.satpredict.utils;

import java.util.Date;

/**
 * Default implementation of the {@link Clock} that uses the system clock.
 */
public class DefaultClock implements Clock {
    public DefaultClock() {
        super();
    }

    @Override
    public Date currentDate() {
        return new Date();
    }

    @Override
    public long currentTime() {
        return System.currentTimeMillis();
    }
}
