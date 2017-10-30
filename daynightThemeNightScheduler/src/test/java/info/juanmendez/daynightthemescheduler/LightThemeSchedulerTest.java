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

/**
 * Created by Juan Mendez on 10/29/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 * LightThemeScheduler based on several conditions figures out if there is a schedule to carry over
 */
public class LightThemeSchedulerTest {
    LocalTime sunrise;
    LocalTime sunset;
    LightTimeApi apiRetro;
    LightTime appLightTime;
    LightTime proxyTodayLightTime;
    LightTime proxyTomorrowLightTime;
    LightThemePlanner planner;
    LocationService locationService;

    boolean isOnline = true;
    boolean locationGranted = true;
    NetworkService networkService;

    @Before
    public void onBefore(){


        appLightTime = new LightTime();
        proxyTodayLightTime = new LightTime();
        proxyTomorrowLightTime = new LightTime();

        sunrise = LocalTime.now();
        sunset = LocalTime.now();

        generateProxy();
        generateNetworkService();
        generateLocationService();

        LightThemeModule m = LightThemeModule.create()
                            .applyLighTimeApi( apiRetro )
                            .applyLocationService(locationService)
                            .applyNetworkService(networkService);

        planner = new LightThemePlanner( m, appLightTime );
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
    public void testScheduler(){

    }
}
