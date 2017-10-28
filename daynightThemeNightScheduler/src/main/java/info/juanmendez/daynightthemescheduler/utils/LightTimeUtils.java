package info.juanmendez.daynightthemescheduler.utils;

import info.juanmendez.daynightthemescheduler.models.LightTime;

/**
 * Created by Juan Mendez on 10/28/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class LightTimeUtils {
    public static LightTime clone(LightTime that ){
        LightTime clone = new LightTime( that.getSunrise(), that.getSunset() );
        clone.setNextSchedule( that.getNextSchedule() );
        return clone;
    }

    public static LightTime cloneForAnotherDay(LightTime appLighttime, int daysFromToday) {
        LightTime clone = new LightTime();
        clone.setSunrise( LocalTimeUtils.getDayAsString( appLighttime.getSunrise(), daysFromToday ));
        clone.setSunset( LocalTimeUtils.getDayAsString( appLighttime.getSunset(), daysFromToday ));
        return clone;
    }

    public static boolean isValid( LightTime that ){
        return !that.getSunrise().isEmpty() && !that.getSunset().isEmpty();
    }
}
