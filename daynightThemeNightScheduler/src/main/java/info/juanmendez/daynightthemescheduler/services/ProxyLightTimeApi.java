package info.juanmendez.daynightthemescheduler.services;

import org.joda.time.DateTime;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.LightTimeModule;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.utils.LightTimeUtils;
import info.juanmendez.daynightthemescheduler.utils.LocalTimeUtils;

/**
 * Created by Juan Mendez on 10/27/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class ProxyLightTimeApi implements LightTimeApi {
    LightTimeModule m;
    LightTime appLightTime;


    public ProxyLightTimeApi( LightTimeModule module, LightTime lightTime  ) {
        m = module;
        this.appLightTime = lightTime;
    }

    /**
     * We attempt to collect data already cached in.
     * Otherwise we try to make a webservice call to get the data.
     * In the most optimistic case having no network we clone data from another date into todays.
     * In the worst case we reply with an empty LightTime, which is considered invalid to our app.
     * @param response
     */
    @Override
    public void generateTodayTimeLight(Response<LightTime> response) {
        //we check if what we have is already cached
        if(LocalTimeUtils.isSameDay( appLightTime.getSunrise(), DateTime.now().toString() )){
            response.onResult( LightTimeUtils.clone(appLightTime) );
        }else if( m.getNetworkService().isOnline() && m.getLocationService().isGranted() ){
             m.getLightTimeApi().generateTodayTimeLight( response );
        }else if( LightTimeUtils.isValid(appLightTime)){
            response.onResult( LightTimeUtils.cloneForAnotherDay(appLightTime, 0 ) );
        }else{
            response.onResult( new LightTime() );
        }
    }

    /**
     * We attempt to collect data from a webservice call to get the tomorrow's data.
     * In the most optimistic case having no network we clone data from another date into tomorrows.
     * In the worst case we reply with an empty LightTime, which is considered invalid to our app.
     * @param response
     */
    @Override
    public void generateTomorrowTimeLight(Response<LightTime> response) {
        if( m.getNetworkService().isOnline() && m.getLocationService().isGranted() ){
            m.getLightTimeApi().generateTomorrowTimeLight( response );
        }else if( LightTimeUtils.isValid(appLightTime)){
            response.onResult( LightTimeUtils.cloneForAnotherDay(appLightTime, 1 ) );
        }else{
            response.onResult( new LightTime() );
        }
    }
}
