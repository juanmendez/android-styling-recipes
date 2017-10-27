package info.juanmendez.daynightthemescheduler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.TimeZone;

/**
 * Created by Juan Mendez on 10/27/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class LocalTimeUtils {

    public static LocalTime getLocalTime(String dateString ){
        DateTime dt = new DateTime(dateString, DateTimeZone.UTC);
        dt = new DateTime( dt, DateTimeZone.forID((TimeZone.getDefault()).getID()));
        return dt.toLocalTime();
    }

    public static LocalDateTime getLocalDateTime( String dateString ){
        DateTime dt = new DateTime(dateString, DateTimeZone.UTC);
        dt = new DateTime( dt, DateTimeZone.forID((TimeZone.getDefault()).getID()));
        return dt.toLocalDateTime();
    }

    public static Boolean isDaylightScreen(LocalTime now, LocalTime sunrise, LocalTime sunset){
        if( now.isBefore( sunrise )){
            return false;
        }else if( now.isBefore(sunset)){
            return true;
        }

        return false;
    }
}
