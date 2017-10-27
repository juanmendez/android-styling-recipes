package info.juanmendez.daynightthemescheduler;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.services.ApiWS;
import info.juanmendez.daynightthemescheduler.services.LightTimePlanner;
import info.juanmendez.daynightthemescheduler.services.NetworkService;

import static info.juanmendez.daynightthemescheduler.services.LightTimePlanner.SUNRISE_SCHEDULE;
import static info.juanmendez.daynightthemescheduler.services.LightTimePlanner.SUNSET_SCHEDULE;
import static info.juanmendez.daynightthemescheduler.services.LightTimePlanner.TOMORROW_SCHEDULE;
import static info.juanmendez.daynightthemescheduler.services.LightTimePlanner.whatSchedule;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LightTimePlannerTest {

    LocalTime sunrise;
    LocalTime sunset;
    ApiWS apiProxy;
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

        planner = new LightTimePlanner(apiProxy, networkService, appLightTime );
    }

    private void generateNetworkService() {
        networkService = mock( NetworkService.class );
        doReturn( isOnline ).when( networkService ).isOnline();
    }

    private void generateProxy() {
        //chicago.. https://api.sunrise-sunset.org/json?lat=41.8500300&lng=-87.6500500&formatted=0
        apiProxy = mock( ApiWS.class );

        PowerMockito.doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult(proxyTodayLightTime);
            return null;
        }).when( apiProxy ).provideTodaysSchedule(any(Response.class));

        PowerMockito.doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult(proxyTomorrowLightTime);
            return null;
        }).when( apiProxy ).provideTomorrowSchedule(any(Response.class));
    }

    @Test
    public void firstTime(){

        //if values in appLightTime are empty, we need to get today's lightTime
        if( appLightTime.getSunRise().isEmpty() || appLightTime.getSunRise().isEmpty() ){

            //we are defining what proxy is going to return next
            proxyTodayLightTime.setSunRise( "2017-10-27T12:07:26+00:00" );
            proxyTodayLightTime.setSunSet( "2017-10-27T23:03:42+00:00" );

            apiProxy.provideTodaysSchedule(result -> {
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
        apiProxy.provideTomorrowSchedule( result -> {
            appLightTime.setSunRise( result.getSunRise());
            appLightTime.setSunSet( result.getSunSet());
        });

        //so proxy is giving back sunrise for tomorrow.
        assertEquals( LocalTimeUtils.getLocalDateTime(appLightTime.getSunRise()).toLocalDate(), LocalDate.now().plusDays(1) );
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
}