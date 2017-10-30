package info.juanmendez.daynightthemescheduler;

import android.location.Location;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.LightThemeModule;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.services.LightTimeApi;
import info.juanmendez.daynightthemescheduler.services.LightThemePlanner;
import info.juanmendez.daynightthemescheduler.services.LocationService;
import info.juanmendez.daynightthemescheduler.services.NetworkService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created by Juan Mendez on 10/29/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 * LightThemeScheduler based on several conditions figures out if there is a schedule to carry over
 */
public class LightThemeSchedulerTest {
    LocalTime twistSunrise;
    LocalTime twistSunset;

    LightTimeApi apiRetro;
    LightTime appLightTime;
    LightTime twistApiToday;
    LightTime twistApiTomorrow;

    LightThemePlanner planner;
    LocationService locationService;
    int twistObserversCount = 0;

    boolean twistIsOnline = true;
    boolean twistLocationGranted = true;
    NetworkService networkService;
    LightThemeModule m;

    @Before
    public void onBefore(){


        appLightTime = new LightTime();
        twistApiToday = new LightTime();
        twistApiTomorrow = new LightTime();

        twistSunrise = LocalTime.now();
        twistSunset = LocalTime.now();

        generateProxy();
        generateNetworkService();
        generateLocationService();

        m = spy( LightThemeModule.create()
                .applyLighTimeApi( apiRetro )
                .applyLocationService(locationService)
                .applyNetworkService(networkService)
                .applyObserversCount( 0 ) );

        //we can manipulate count by setting twistObserversCount;
        doAnswer(invocation -> twistObserversCount).when( m ).getObserversCount();

        planner = new LightThemePlanner( m, appLightTime );
    }

    private void generateNetworkService() {
        networkService = mock( NetworkService.class );
        doAnswer(invocation -> twistIsOnline).when( networkService ).isOnline();
    }

    private void generateProxy() {
        //chicago.. https://api.sunrise-sunset.org/json?lat=41.8500300&lng=-87.6500500&formatted=0
        apiRetro = mock( LightTimeApi.class );

        doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult(twistApiToday);
            return null;
        }).when(apiRetro).generateTodayTimeLight(any(Response.class));

        doAnswer(invocation -> {
            Response<LightTime> response = invocation.getArgumentAt(0, Response.class);
            response.onResult(twistApiTomorrow);
            return null;
        }).when(apiRetro).generateTomorrowTimeLight(any(Response.class));
    }

    private void generateLocationService(){
        locationService = mock( LocationService.class );
        doAnswer(invocation -> twistLocationGranted).when( locationService ).isGranted();
        doReturn( new Location("NONE")).when( locationService ).getLastKnownLocation();
    }

    @Test
    public void testScheduler(){

    }
}
