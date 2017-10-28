package info.juanmendez.daynightthemescheduler.services;

import org.joda.time.DateTime;

import info.juanmendez.daynightthemescheduler.utils.LightTimeUtils;
import info.juanmendez.daynightthemescheduler.utils.LocalTimeUtils;
import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;

/**
 * Created by Juan Mendez on 10/27/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class ApiProxy implements ApiRetro {
    NetworkService networkService;
    LightTime appLightTime;
    ApiRetro webService;

    public ApiProxy(NetworkService networkService, ApiRetro webService, LightTime appLightTime) {
        this.networkService = networkService;
        this.webService = webService;
        this.appLightTime = appLightTime;
    }

    /**
     * We attempt to collect data already cached in.
     * Otherwise we try to make a webservice call to get the data.
     * In the most optimistic case having no network we clone data from another date into todays.
     * In the worst case we reply with an empty LightTime, which is considered invalid to our app.
     * @param response
     */
    @Override
    public void provideTodaysSchedule(Response<LightTime> response) {
        //we check if what we have is already cached
        if(LocalTimeUtils.isSameDay( appLightTime.getSunRise(), DateTime.now().toString() )){
            response.onResult( LightTimeUtils.clone(appLightTime) );
        }else if( networkService.isOnline() ){
             webService.provideTodaysSchedule( response );
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
    public void provideTomorrowSchedule(Response<LightTime> response) {
        if( networkService.isOnline() ){
            webService.provideTomorrowSchedule( response );
        }else if( LightTimeUtils.isValid(appLightTime)){
            response.onResult( LightTimeUtils.cloneForAnotherDay(appLightTime, 1 ) );
        }else{
            response.onResult( new LightTime() );
        }
    }
}
