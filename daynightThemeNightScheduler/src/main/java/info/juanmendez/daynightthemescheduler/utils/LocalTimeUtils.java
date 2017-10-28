package info.juanmendez.daynightthemescheduler.utils;

import android.support.annotation.NonNull;

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

    public static LocalTime getLocalTime(@NonNull String dateString ){
        DateTime dt = new DateTime(dateString, DateTimeZone.UTC);
        dt = new DateTime( dt, DateTimeZone.forID((TimeZone.getDefault()).getID()));
        return dt.toLocalTime();
    }

    public static LocalDateTime getLocalDateTime(@NonNull String dateString ){
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

    /**
     * Creates a new LocalDateTime using the time from another date formatted string
     * @param dateString any string date formatted
     * @param daysFromToday 0 is for today, 1 is for tommorrow
     * @return
     */
    public static LocalDateTime getDayAsLocalDateTime(@NonNull String dateString, int daysFromToday ){
        DateTime dt = new DateTime( dateString, DateTimeZone.UTC );
        return dt.toLocalTime().toDateTimeToday().plusDays(daysFromToday).toLocalDateTime();
    }

    /**
     * creates a date formatted string using the time from another given date formatted string
     * @param dateString another date provided as string
     * @param daysFromToday the new date is based on today, so for tomorrow we assign value of 1
     * @return
     */
    public static String getDayAsString(@NonNull String dateString, int daysFromToday ){
        return getDayAsLocalDateTime( dateString, daysFromToday ).toString();
    }

    public static boolean isSameDay(@NonNull String then, @NonNull String now ){
        if( then.isEmpty() || now.isEmpty() )
            return false;

        DateTime thenDateTime = new DateTime( then );
        DateTime nowDateTime =  new DateTime( now);

        return thenDateTime.toLocalDate().equals( nowDateTime.toLocalDate() );
    }
}
