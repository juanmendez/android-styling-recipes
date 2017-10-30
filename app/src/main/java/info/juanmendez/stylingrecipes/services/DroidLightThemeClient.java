package info.juanmendez.stylingrecipes.services;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import info.juanmendez.daynightthemescheduler.models.LightThemeModule;
import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.services.LightThemeClient;


/**
 * Created by Juan Mendez on 10/29/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean
public class DroidLightThemeClient implements LightThemeClient {

    @Bean
    DroidLocationService locationService;

    @Bean
    DroidNetworkService networkService;

    @Bean
    DroidLightTimeApi lightTimeApi;

    @Override
    public void cancelIfRunning() {

    }

    @Override
    public void scheduleNext(LightTime lightTime) {

    }

    @Override
    public void setLightMode(int type) {

    }

    @Override
    public LightTime getAppLightTime() {
        return new LightTime();
    }

    @Override
    public LightThemeModule getLightTimeModule() {

        LightThemeModule m = LightThemeModule.create()
                            .applyLighTimeApi(lightTimeApi)
                            .applyLocationService( locationService )
                            .applyNetworkService( networkService )
                            .applyObserversCount( 0 );
        return m;
    }
}