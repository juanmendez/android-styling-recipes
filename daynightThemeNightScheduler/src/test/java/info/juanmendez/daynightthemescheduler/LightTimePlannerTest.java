package info.juanmendez.daynightthemescheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.services.ApiProxy;
import info.juanmendez.daynightthemescheduler.services.ApiRetro;
import info.juanmendez.daynightthemescheduler.services.LightTimePlanner;
import info.juanmendez.daynightthemescheduler.services.NetworkService;
import info.juanmendez.daynightthemescheduler.utils.LightTimeUtils;
import info.juanmendez.daynightthemescheduler.utils.LocalTimeUtils;

import static info.juanmendez.daynightthemescheduler.services.LightTimePlanner.SUNRISE_SCHEDULE;
import static info.juanmendez.daynightthemescheduler.services.LightTimePlanner.SUNSET_SCHEDULE;
import static info.juanmendez.daynightthemescheduler.services.LightTimePlanner.TOMORROW_SCHEDULE;
import static info.juanmendez.daynightthemescheduler.services.LightTimePlanner.whatSchedule;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LightTimePlannerTest {

    LocalTime sunrise;
    LocalTime sunset;
    ApiRetro apiRetro;
    LightTime appLightTime;
    LightTime proxyTodayLightTime;
    LightTime proxyTomorrowLightTime;
    LightTimePlanner planner;

    boolean isOnline = true;
    NetworkService networkService;

    @Before
    public void before(){
        appLightTime = new LightTime();
        proxyTodayLightTime = new LightTime();
        proxyTomorrowLightTime = new LightTime();

        sunrise = LocalTime.now();
        sunset = LocalTime.now();

        generateProxy();
        generateNetworkService();

        planner = new LightTimePlanner(apiRetro, networkService, appLightTime );
    }

    private void generateNetworkService() {
        networkService = mock( NetworkService.class );

        PowerMockito.doAnswer(invocation -> isOnline).when( networkService ).isOnline();
    }

    private void generateProxy() {
        //chicago.. https://api.sunrise-sunset.org/json?lat=41.8500300&lng=-87.6500500&formatted=0
        apiRetro = mock( ApiRetro.class );

        PowerMockito.doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult(proxyTodayLightTime);
            return null;
        }).when(apiRetro).provideTodaysSchedule(any(Response.class));

        PowerMockito.doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult(proxyTomorrowLightTime);
            return null;
        }).when(apiRetro).provideTomorrowSchedule(any(Response.class));
    }

    @Test
    public void firstTime(){

        //if values in appLightTime are empty, we need to get today's lightTime
        if( appLightTime.getSunRise().isEmpty() || appLightTime.getSunRise().isEmpty() ){

            //we are defining what proxy is going to return next
            proxyTodayLightTime.setSunRise( "2017-10-27T12:07:26+00:00" );
            proxyTodayLightTime.setSunSet( "2017-10-27T23:03:42+00:00" );

            apiRetro.provideTodaysSchedule(result -> {
                appLightTime.setSunRise( result.getSunRise());
                appLightTime.setSunSet( result.getSunSet());
            });
        }

        //do we schedule for sunrise, sunset, or none?
        sunrise = LocalTimeUtils.getLocalTime( appLightTime.getSunRise() );
        sunset = LocalTimeUtils.getLocalTime( appLightTime.getSunSet() );

        assertEquals( whatSchedule(  LocalTime.parse( "00:40:00"), sunrise, sunset), SUNRISE_SCHEDULE  );
        assertEquals( whatSchedule(  LocalTime.parse( "16:40:00"), sunrise, sunset), SUNSET_SCHEDULE  );
        assertEquals( whatSchedule(  LocalTime.parse( "23:44:00"), sunrise, sunset), TOMORROW_SCHEDULE  );

        //what if we need tomorrows lightTime instead?
        proxyTomorrowLightTime.setSunRise( "2017-10-28T12:07:26+00:00" );
        proxyTomorrowLightTime.setSunSet( "2017-10-28T23:03:42+00:00" );
        apiRetro.provideTomorrowSchedule(result -> {
            appLightTime.setSunRise( result.getSunRise());
            appLightTime.setSunSet( result.getSunSet());
        });

        //so proxy is giving back sunrise for tomorrow.
        assertEquals( LocalTimeUtils.getLocalDateTime(appLightTime.getSunRise()).toLocalDate(), LocalDate.parse("2017-10-27").plusDays(1) );
    }

    @Test
    public void firstTimeInPlanner(){
        proxyTodayLightTime.setSunRise( "2017-10-27T12:07:26+00:00" );
        proxyTodayLightTime.setSunSet( "2017-10-27T23:03:42+00:00" );

        proxyTomorrowLightTime.setSunRise( "2017-10-28T12:07:26+00:00" );
        proxyTomorrowLightTime.setSunSet( "2017-10-28T23:03:42+00:00" );

        Whitebox.setInternalState( planner, "now", LocalTime.parse( "00:40:00") );
        planner.provideNextSchedule( result -> {
            assertEquals(proxyTodayLightTime.getSunRise(), result.getNextSchedule());
        });

        Whitebox.setInternalState( planner, "now", LocalTime.parse( "16:40:00") );
        planner.provideNextSchedule( result -> {
            assertEquals(proxyTodayLightTime.getSunSet(), result.getNextSchedule());
        });

        Whitebox.setInternalState( planner, "now", LocalTime.parse( "23:00:00") );
        planner.provideNextSchedule( result -> {
            assertEquals( result.getSunRise(), proxyTomorrowLightTime.getSunRise() );
            assertEquals( result.getSunSet(), proxyTomorrowLightTime.getSunSet() );
            assertEquals( result.getNextSchedule(), proxyTomorrowLightTime.getSunRise() );
        });
    }

    /**
     * this is the action which should be taken whenever there is no network connection.
     * we use what's available from the day before.
     */
    @Test
    public void offlineTest(){
        isOnline = false;

        String yesterdaySunrise = "2017-10-26T12:07:26+00:00";
        String yesterdaySunset = "2017-10-26T23:03:42+00:00";

        appLightTime.setSunRise( yesterdaySunrise );
        appLightTime.setSunSet( yesterdaySunset );

        if( !networkService.isOnline() ){
            //reuse what's on appLightTime from another date into today

            proxyTodayLightTime.setSunRise( LocalTimeUtils.getDayAsString( appLightTime.getSunRise(), 0 ));
            proxyTodayLightTime.setSunSet( LocalTimeUtils.getDayAsString( appLightTime.getSunSet(), 0 ));

            assertEquals(LocalDateTime.parse( proxyTodayLightTime.getSunRise()).toLocalDate(), LocalDate.now() );
            assertEquals(LocalDateTime.parse( proxyTodayLightTime.getSunSet()).toLocalDate(), LocalDate.now() );
        }
    }

    @Test
    public void checkIfItsToday(){
        //we want to get today or tomorrows sunrise and sunset but we might not be online..
        //what do we do?
        String yesterdaySunrise = "2017-10-26T12:07:26+00:00";
        String yesterdaySunset = "2017-10-26T23:03:42+00:00";

        appLightTime.setSunRise( yesterdaySunrise );
        appLightTime.setSunSet( yesterdaySunset );

        String now = "2017-10-26T23:03:42+00:00";
        DateTime nowDateTime = new DateTime( now );

        assertTrue( new DateTime( yesterdaySunrise ).toLocalDate().equals( nowDateTime.toLocalDate() ));
        assertTrue( LocalTimeUtils.isSameDay( yesterdaySunrise, now ));
        assertFalse( LocalTimeUtils.isSameDay( yesterdaySunrise, "" ));

        proxyTodayLightTime.setSunRise( "2017-10-27T12:07:26+00:00" );
        proxyTodayLightTime.setSunSet( "2017-10-27T23:03:42+00:00" );
    }

    /**
     * The app has sunrise and sunset from yesterday.
     * We are requesting today's. Do we need new data?
     */
    @Test
    public void testCheckIfCacheIsNeeded(){
        String yesterdaySunrise = "2017-10-26T12:07:26+00:00";
        String yesterdaySunset = "2017-10-26T23:03:42+00:00";

        appLightTime.setSunRise( yesterdaySunrise );
        appLightTime.setSunSet( yesterdaySunset );

        ApiProxy proxy = new ApiProxy(networkService, apiRetro, appLightTime );
        final LightTime[] proxyResult = new LightTime[1];

        Response<LightTime> response = result -> {
            proxyResult[0] = result;
        };

        proxy.provideTodaysSchedule( response );
        assertEquals( proxyResult[0].getSunRise(), proxyTodayLightTime.getSunRise() );
        verify( apiRetro ).provideTodaysSchedule( any(Response.class));

        reset( apiRetro );

        //app is up to date, so for our next proxy request we should get a cached version.
        appLightTime.setSunRise( LocalTimeUtils.getDayAsString(yesterdaySunrise, 0));
        appLightTime.setSunSet( LocalTimeUtils.getDayAsString(yesterdaySunset, 0));
        proxy.provideTodaysSchedule( response );

        //so our apiRetro shouldn't have been called
        verify( apiRetro, times(0) ).provideTodaysSchedule( any(Response.class));
    }

    /**
     * we want to find sunrise and sunset for today, but we are offline..
     * lets test if we can copy the one from the day before
     */
    @Test
    public void testForNetworkIssues(){
        isOnline = false;

        String yesterdaySunrise = "2017-10-26T12:07:26+00:00";
        String yesterdaySunset = "2017-10-26T23:03:42+00:00";

        appLightTime.setSunRise( yesterdaySunrise );
        appLightTime.setSunSet( yesterdaySunset );

        ApiProxy proxy = new ApiProxy(networkService, apiRetro, appLightTime );
        final LightTime[] proxyResult = new LightTime[1];

        Response<LightTime> response = result -> {
            proxyResult[0] = result;
        };

        proxy.provideTodaysSchedule( response );
        verify( apiRetro, times(0) ).provideTodaysSchedule( any(Response.class));
        assertEquals( proxyResult[0].getSunRise(), LocalTimeUtils.getDayAsString(yesterdaySunrise, 0));
        assertEquals( proxyResult[0].getSunSet(), LocalTimeUtils.getDayAsString(yesterdaySunset, 0));

        proxy.provideTomorrowSchedule( response );
        verify( apiRetro, times(0) ).provideTodaysSchedule( any(Response.class));
        assertEquals( proxyResult[0].getSunRise(), LocalTimeUtils.getDayAsString(yesterdaySunrise, 1));
        assertEquals( proxyResult[0].getSunSet(), LocalTimeUtils.getDayAsString(yesterdaySunset, 1));

        //sweet, we are now aware our proxy is generating an old date into a new date when there is no network.
        //what if there is no data set, meaning it's the first time and we are not in the network
        appLightTime.setSunRise("");
        appLightTime.setSunSet("");
        proxy.provideTodaysSchedule( response );
        assertFalse(LightTimeUtils.isValid( proxyResult[0]));

        proxy.provideTomorrowSchedule( response );
        assertFalse(LightTimeUtils.isValid( proxyResult[0]));

        //knowing that our response has an invalid lightTime means
        //we can use this flag to make a special case.
    }
}