package info.juanmendez.daynightthemescheduler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SchedulerTest {

    LocalTime sunrise = getLocalTime("2017-10-18T12:07:26+00:00");
    LocalTime sunset = getLocalTime( "2017-10-18T23:03:42+00:00" );

    @Before
    public void before(){
        sunrise = getLocalTime("2017-10-18T12:07:26+00:00");
        sunset = getLocalTime( "2017-10-18T23:03:42+00:00" );
    }

    /**Spike Joda time **/
    @Test
    public void jodaTimeSpike(){

        System.out.println( sunrise );
        System.out.println( sunset );

        LocalTime now = LocalTime.now();

        if( now.isBefore(sunrise)){
            System.out.println( "it's very early morning screen is in night mode" );
        }
        else
        if( now.isBefore(sunset)){
            System.out.println( "screen is in day mode" );
        }
        else{
            System.out.println( "it's night time, screen is in night mode" );
        }
    }

    @Test
    public void testScreen(){

        //5:00
        LocalTime now = new LocalTime(5, 0, 0 );
        assertFalse( isDaylightScreen(now, sunrise, sunset ));

        //8:00
        now = now.plusHours( 3 );
        assertTrue( isDaylightScreen(now, sunrise, sunset ));

        //16:00
        now = now.plusHours( 8 );
        assertTrue( isDaylightScreen(now, sunrise, sunset ));

        //18:00
        now = now.plusHours( 2 );
        assertTrue( isDaylightScreen(now, sunrise, sunset ));

        //18:03
        now = now.plusMinutes(3);
        assertTrue( isDaylightScreen(now, sunrise, sunset ));

        //18:03:43 pm, we are passed by one second!
        now = now.plusSeconds( 43);
        assertFalse( isDaylightScreen(now, sunrise, sunset ));

        //how do we do a substraction? Example, how much time is needed to get to 19:00 hours?
        LocalTime nineteen = new LocalTime( 19, 0, 0 );

        Period diff = new Period( now, nineteen );

        assertEquals( diff.getMinutes(), 56);
        assertEquals( diff.getSeconds(), 60-43);
        assertEquals(  diff.getHours() * 60 * 60* 1000 + diff.getMinutes()*60*1000 + diff.getSeconds()*1000, (56*60*1000)+((60-43)*1000) );

        //so it's 22:00, and I want to get up at 5:00, how many minutes are total?
        now = new LocalTime(22, 0, 0 );
        sunrise = new LocalTime( 5, 0, 0 );
        diff = new Period( now, sunrise );

        //so I am aware I will sleep 7 hours, is that right?
        //so the test failed, it said difference was -17 hours. Plus 24 would had made a perfect 7!..
        diff = diff.plus( new Period(24, 0, 0, 0)); //so this should do it!
        assertEquals( diff.getHours(), 7 );

        //i'm not conviced.. how about I go to bed at 5 in the morning and would wake up at 6 am tomorrow..
        //cause I didn't sleep for three days.. :(
        //just to remind this is great for periods which are negative
        now = new LocalTime(17,0,0);
        diff = (new Period(now, sunrise)).plus( new Period(24,0,0,0));
        assertEquals( diff.getHours(), 12 );

        //lets sleep between 23:00, and 1:00
        now = new LocalTime(23,0,0);
        sunrise = new LocalTime(1,0,0);
        diff = (new Period(now, sunrise)).plus( new Period(24,0,0,0));
        assertEquals( diff.getHours(), 2 );

    }

    /*
        User just opted for night-auto in the application.
     */
    @Test
    public void testOptionMade(){

        //chicago.. https://api.sunrise-sunset.org/json?lat=41.8500300&lng=-87.6500500&formatted=0

        ApiProxy apiProxy = Mockito.mock( ApiProxy.class );

        PowerMockito.doAnswer( invocation -> {
            QuickResponse<List<Date>> response = invocation.getArgumentAt(0, QuickResponse.class);

            List<Date> scheduler = new ArrayList<>();
            scheduler.add( new Date());
            scheduler.add( new Date());
            response.onResult( scheduler );

            return null;
        }).when( apiProxy ).provideTodaysSchedule(any(QuickResponse.class));

        QuickResponse<List<Date>> respose = Mockito.mock(QuickResponse.class);
        apiProxy.provideTodaysSchedule(respose);
        Mockito.verify(  respose ).onResult(any(List.class));
    }

    private LocalTime getLocalTime(String dateString ){
        DateTime dt = new DateTime(dateString, DateTimeZone.UTC);
        dt = new DateTime( dt, DateTimeZone.forID((TimeZone.getDefault()).getID()));
        return dt.toLocalTime();
    }

    private Boolean isDaylightScreen(LocalTime now, LocalTime sunrise, LocalTime sunset){
        if( now.isBefore( sunrise )){
            return false;
        }else if( now.isBefore(sunset)){
            return true;
        }

        return false;
    }
}