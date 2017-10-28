package info.juanmendez.daynightthemescheduler;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.services.ApiRetro;
import info.juanmendez.daynightthemescheduler.services.NetworkService;
import info.juanmendez.daynightthemescheduler.utils.LocalTimeUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SchedulerTest {

    LocalTime sunrise;
    LocalTime sunset;
    ApiRetro apiProxy;
    LightTime lightTime;

    boolean isOnline = true;
    NetworkService networkService;

    @Before
    public void before(){
        lightTime = new LightTime("2017-10-18T12:07:26+00:00", "2017-10-18T23:03:42+00:00");

        sunrise = LocalTimeUtils.getLocalTime( lightTime.getSunrise());
        sunset = LocalTimeUtils.getLocalTime( lightTime.getSunset() );

        generateProxy();
        generateNetworkService();
    }

    private void generateNetworkService() {
        networkService = mock( NetworkService.class );
        doReturn( isOnline ).when( networkService ).isOnline();
    }

    private void generateProxy() {
        //chicago.. https://api.sunrise-sunset.org/json?lat=41.8500300&lng=-87.6500500&formatted=0
        apiProxy = mock( ApiRetro.class );

        PowerMockito.doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult( lightTime );
            return null;
        }).when( apiProxy ).generateTodayTimeLight(any(Response.class));
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
        assertFalse( LocalTimeUtils.isDaylightScreen(now, sunrise, sunset ));

        //8:00
        now = now.plusHours( 3 );
        assertTrue( LocalTimeUtils.isDaylightScreen(now, sunrise, sunset ));

        //16:00
        now = now.plusHours( 8 );
        assertTrue( LocalTimeUtils.isDaylightScreen(now, sunrise, sunset ));

        //18:00
        now = now.plusHours( 2 );
        assertTrue( LocalTimeUtils.isDaylightScreen(now, sunrise, sunset ));

        //18:03
        now = now.plusMinutes(3);
        assertTrue( LocalTimeUtils.isDaylightScreen(now, sunrise, sunset ));

        //18:03:43 pm, we are passed by one second!
        now = now.plusSeconds( 43);
        assertFalse( LocalTimeUtils.isDaylightScreen(now, sunrise, sunset ));

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

        Response<LightTime> respose = mock(Response.class);
        apiProxy.generateTodayTimeLight(respose);
        Mockito.verify(  respose ).onResult(any(LightTime.class));
    }

    /**
     * check if the appLightTime is from today.
     */
    @Test
    public void checkIfLightTimeIsToday(){

        LocalDateTime sunriseDateTime = LocalTimeUtils.getLocalDateTime( lightTime.getSunrise() );
        LocalDateTime sunsetDateTime = LocalTimeUtils.getLocalDateTime( lightTime.getSunset() );
        LocalDateTime now = LocalDateTime.now();

        //appLightTime happened not today
        assertFalse( sunriseDateTime.toLocalDate().equals( now.toLocalDate() ) );

        //lets pretend now is on Oct 18
        now = LocalTimeUtils.getLocalDateTime( "2017-10-18T16:00:00+00:00" );

        assertTrue( sunriseDateTime.toLocalDate().equals( now.toLocalDate() ) );
        assertTrue( sunriseDateTime.isBefore(now) && sunsetDateTime.isAfter(now ));
    }

    @Test
    public void testChangingAnotherDateTimeForTodays(){
        LocalDateTime sunriseDateTime = LocalTimeUtils.getLocalDateTime( lightTime.getSunrise() );
        LocalDateTime sunriseToday =  sunriseDateTime.toLocalTime().toDateTimeToday().toLocalDateTime();
        assertTrue( sunriseToday.toLocalDate().equals( sunriseToday.toLocalDate() ));
    }
}