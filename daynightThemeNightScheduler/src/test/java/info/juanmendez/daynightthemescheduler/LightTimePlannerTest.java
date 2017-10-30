package info.juanmendez.daynightthemescheduler;

import android.location.Location;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.LightTimeModule;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.services.LightTimeApi;
import info.juanmendez.daynightthemescheduler.services.LightTimePlanner;
import info.juanmendez.daynightthemescheduler.services.LocationService;
import info.juanmendez.daynightthemescheduler.services.NetworkService;
import info.juanmendez.daynightthemescheduler.services.ProxyLightTimeApi;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;

/**
 * These tests were made in order to make the functionality needed in ProxyLightTimeApi
 */
public class LightTimePlannerTest {

    LocalTime sunrise;
    LocalTime sunset;
    LightTimeApi apiRetro;
    LightTime appLightTime;
    LightTime proxyTodayLightTime;
    LightTime proxyTomorrowLightTime;
    LightTimePlanner planner;

    boolean isOnline = true;
    private boolean locationGranted = true;
    NetworkService networkService;
    LocationService locationService;
    LightTimeModule m;

    @Before
    public void before(){
        appLightTime = new LightTime();
        proxyTodayLightTime = new LightTime();
        proxyTomorrowLightTime = new LightTime();

        sunrise = LocalTime.now();
        sunset = LocalTime.now();

        generateProxy();
        generateNetworkService();
        generateLocationService();

        m = LightTimeModule.create()
                .applyLighTimeApi( apiRetro )
                .applyLocationService(locationService)
                .applyNetworkService(networkService);

        planner = new LightTimePlanner( m, appLightTime );
    }

    private void generateNetworkService() {
        networkService = mock( NetworkService.class );
        doAnswer(invocation -> isOnline).when( networkService ).isOnline();
    }

    private void generateProxy() {
        //chicago.. https://api.sunrise-sunset.org/json?lat=41.8500300&lng=-87.6500500&formatted=0
        apiRetro = mock( LightTimeApi.class );

        doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult(proxyTodayLightTime);
            return null;
        }).when(apiRetro).generateTodayTimeLight(any(Response.class));

        doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult(proxyTomorrowLightTime);
            return null;
        }).when(apiRetro).generateTomorrowTimeLight(any(Response.class));
    }

    private void generateLocationService(){
        locationService = mock( LocationService.class );
        doAnswer(invocation -> locationGranted).when( locationService ).isGranted();
        doReturn( new Location("NONE")).when( locationService ).getLastKnownLocation();
    }

    @Test
    public void firstTime(){

        //if values in appLightTime are empty, we need to get today's lightTime
        if( appLightTime.getSunrise().isEmpty() || appLightTime.getSunrise().isEmpty() ){

            //we are defining what proxy is going to return next
            proxyTodayLightTime.setSunrise( "2017-10-27T12:07:26+00:00" );
            proxyTodayLightTime.setSunset( "2017-10-27T23:03:42+00:00" );

            apiRetro.generateTodayTimeLight(result -> {
                appLightTime.setSunrise( result.getSunrise());
                appLightTime.setSunset( result.getSunset());
            });
        }

        //do we schedule for sunrise, sunset, or none?
        sunrise = LocalTimeUtils.getLocalTime( appLightTime.getSunrise() );
        sunset = LocalTimeUtils.getLocalTime( appLightTime.getSunset() );

        assertEquals( whatSchedule(  LocalTime.parse( "00:40:00"), sunrise, sunset), SUNRISE_SCHEDULE  );
        assertEquals( whatSchedule(  LocalTime.parse( "16:40:00"), sunrise, sunset), SUNSET_SCHEDULE  );
        assertEquals( whatSchedule(  LocalTime.parse( "23:44:00"), sunrise, sunset), TOMORROW_SCHEDULE  );

        //what if we need tomorrows lightTime instead?
        proxyTomorrowLightTime.setSunrise( "2017-10-28T12:07:26+00:00" );
        proxyTomorrowLightTime.setSunset( "2017-10-28T23:03:42+00:00" );
        apiRetro.generateTomorrowTimeLight(result -> {
            appLightTime.setSunrise( result.getSunrise());
            appLightTime.setSunset( result.getSunset());
        });

        //so proxy is giving back sunrise for tomorrow.
        assertEquals( LocalTimeUtils.getLocalDateTime(appLightTime.getSunrise()).toLocalDate(), LocalDate.parse("2017-10-27").plusDays(1) );
    }

    @Test
    public void firstTimeInPlanner(){
        proxyTodayLightTime.setSunrise( "2017-10-27T12:07:26+00:00" );
        proxyTodayLightTime.setSunset( "2017-10-27T23:03:42+00:00" );

        proxyTomorrowLightTime.setSunrise( "2017-10-28T12:07:26+00:00" );
        proxyTomorrowLightTime.setSunset( "2017-10-28T23:03:42+00:00" );

        Whitebox.setInternalState( planner, "now", LocalTime.parse( "00:40:00") );
        planner.provideNextTimeLight(result -> {
            assertEquals(proxyTodayLightTime.getSunrise(), result.getNextSchedule());
        });

        Whitebox.setInternalState( planner, "now", LocalTime.parse( "16:40:00") );
        planner.provideNextTimeLight(result -> {
            assertEquals(proxyTodayLightTime.getSunset(), result.getNextSchedule());
        });

        Whitebox.setInternalState( planner, "now", LocalTime.parse( "23:00:00") );
        planner.provideNextTimeLight(result -> {
            assertEquals( result.getSunrise(), proxyTomorrowLightTime.getSunrise() );
            assertEquals( result.getSunset(), proxyTomorrowLightTime.getSunset() );
            assertEquals( result.getNextSchedule(), proxyTomorrowLightTime.getSunrise() );
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

        appLightTime.setSunrise( yesterdaySunrise );
        appLightTime.setSunset( yesterdaySunset );

        if( !networkService.isOnline() ){
            //reuse what's on appLightTime from another date into today

            proxyTodayLightTime.setSunrise( LocalTimeUtils.getDayAsString( appLightTime.getSunrise(), 0 ));
            proxyTodayLightTime.setSunset( LocalTimeUtils.getDayAsString( appLightTime.getSunset(), 0 ));

            assertEquals(LocalDateTime.parse( proxyTodayLightTime.getSunrise()).toLocalDate(), LocalDate.now() );
            assertEquals(LocalDateTime.parse( proxyTodayLightTime.getSunset()).toLocalDate(), LocalDate.now() );
        }
    }

    @Test
    public void checkIfItsToday(){
        //we want to get today or tomorrows sunrise and sunset but we might not be online..
        //what do we do?
        String yesterdaySunrise = "2017-10-26T12:07:26+00:00";
        String yesterdaySunset = "2017-10-26T23:03:42+00:00";

        appLightTime.setSunrise( yesterdaySunrise );
        appLightTime.setSunset( yesterdaySunset );

        String now = "2017-10-26T23:03:42+00:00";
        DateTime nowDateTime = new DateTime( now );

        assertTrue( new DateTime( yesterdaySunrise ).toLocalDate().equals( nowDateTime.toLocalDate() ));
        assertTrue( LocalTimeUtils.isSameDay( yesterdaySunrise, now ));
        assertFalse( LocalTimeUtils.isSameDay( yesterdaySunrise, "" ));

        proxyTodayLightTime.setSunrise( "2017-10-27T12:07:26+00:00" );
        proxyTodayLightTime.setSunset( "2017-10-27T23:03:42+00:00" );
    }

    /**
     * The app has sunrise and sunset from yesterday.
     * We are requesting today's. Do we need new data?
     */
    @Test
    public void testCheckIfCacheIsNeeded(){
        String yesterdaySunrise = "2017-10-26T12:07:26+00:00";
        String yesterdaySunset = "2017-10-26T23:03:42+00:00";

        appLightTime.setSunrise( yesterdaySunrise );
        appLightTime.setSunset( yesterdaySunset );

        ProxyLightTimeApi proxy = new ProxyLightTimeApi( m, appLightTime );
        final LightTime[] proxyResult = new LightTime[1];

        Response<LightTime> response = result -> {
            proxyResult[0] = result;
        };

        proxy.generateTodayTimeLight( response );
        assertEquals( proxyResult[0].getSunrise(), proxyTodayLightTime.getSunrise() );
        verify( apiRetro ).generateTodayTimeLight( any(Response.class));

        reset( apiRetro );

        //app is up to date, so for our next proxy request we should get a cached version.
        appLightTime.setSunrise( LocalTimeUtils.getDayAsString(yesterdaySunrise, 0));
        appLightTime.setSunset( LocalTimeUtils.getDayAsString(yesterdaySunset, 0));
        proxy.generateTodayTimeLight( response );

        //so our apiRetro shouldn't have been called
        verify( apiRetro, times(0) ).generateTodayTimeLight( any(Response.class));
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

        appLightTime.setSunrise( yesterdaySunrise );
        appLightTime.setSunset( yesterdaySunset );

        ProxyLightTimeApi proxy = new ProxyLightTimeApi(m, appLightTime );

        final LightTime[] proxyResult = new LightTime[1];

        Response<LightTime> response = result -> {
            proxyResult[0] = result;
        };

        proxy.generateTodayTimeLight( response );
        verify( apiRetro, times(0) ).generateTodayTimeLight( any(Response.class));
        assertEquals( proxyResult[0].getSunrise(), LocalTimeUtils.getDayAsString(yesterdaySunrise, 0));
        assertEquals( proxyResult[0].getSunset(), LocalTimeUtils.getDayAsString(yesterdaySunset, 0));

        proxy.generateTomorrowTimeLight( response );
        verify( apiRetro, times(0) ).generateTodayTimeLight( any(Response.class));
        assertEquals( proxyResult[0].getSunrise(), LocalTimeUtils.getDayAsString(yesterdaySunrise, 1));
        assertEquals( proxyResult[0].getSunset(), LocalTimeUtils.getDayAsString(yesterdaySunset, 1));

        //sweet, we are now aware our proxy is generating an old date into a new date when there is no network.
        //what if there is no data set, meaning it's the first time and we are not in the network
        appLightTime.setSunrise("");
        appLightTime.setSunset("");
        proxy.generateTodayTimeLight( response );
        assertFalse(LightTimeUtils.isValid( proxyResult[0]));

        proxy.generateTomorrowTimeLight( response );
        assertFalse(LightTimeUtils.isValid( proxyResult[0]));

        //knowing that our response has an invalid lightTime means
        //we can use this flag to make a special case.
    }

    @Test
    public void testPlannerWithNetworkIssues(){
        isOnline = false;
        appLightTime.setSunrise("");
        appLightTime.setSunset("");

        final LightTime[] proxyResult = new LightTime[1];

        Response<LightTime> response = result -> {
            proxyResult[0] = result;
        };

        planner.provideNextTimeLight( response );
        assertFalse(LightTimeUtils.isValid( proxyResult[0]));

        //if we have a network, but we don't have location permissions
        isOnline = true;
        locationGranted = false;

        planner.provideNextTimeLight( response );
        assertFalse(LightTimeUtils.isValid( proxyResult[0]));

    }
}