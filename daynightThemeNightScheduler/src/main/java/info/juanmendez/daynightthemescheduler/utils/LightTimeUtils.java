package info.juanmendez.daynightthemescheduler.utils;

import info.juanmendez.daynightthemescheduler.models.LightTime;

/**
 * Created by Juan Mendez on 10/28/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class LightTimeUtils {
    public static LightTime clone(LightTime that ){
        LightTime clone = new LightTime( that.getSunRise(), that.getSunSet() );
        clone.setNextSchedule( that.getNextSchedule() );
        return clone;
    }

    public static LightTime cloneForAnotherDay(LightTime appLighttime, int daysFromToday) {
        LightTime clone = new LightTime();
        clone.setSunRise( LocalTimeUtils.getDayAsString( appLighttime.getSunRise(), daysFromToday ));
        clone.setSunSet( LocalTimeUtils.getDayAsString( appLighttime.getSunSet(), daysFromToday ));
        return clone;
    }

    public static boolean isValid( LightTime that ){
        return !that.getSunRise().isEmpty() && !that.getSunSet().isEmpty();
    }
}
