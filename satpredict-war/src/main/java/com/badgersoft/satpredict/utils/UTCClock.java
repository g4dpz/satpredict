// FUNcube Data Warehouse
// Copyright 2013 (c) David A.Johnson, G4DPZ, AMSAT-UK
// This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send a letter
// to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.

package com.badgersoft.satpredict.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * Default implementation of the {@link Clock} that uses the UTC clock.
 */
public class UTCClock implements Clock {
	
	private static final SimpleTimeZone TZ = new SimpleTimeZone(0, "UTC");
	
    public UTCClock() {
        super();
    }

    public Date currentDate() {
        return Calendar.getInstance(TZ).getTime();
    }

    public long currentTime() {
        return Calendar.getInstance(TZ).getTime().getTime();
    }
}
