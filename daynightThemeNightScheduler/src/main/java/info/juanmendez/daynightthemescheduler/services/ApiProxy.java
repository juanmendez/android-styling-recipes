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
    LightTime appLighttime;
    ApiRetro webService;

    public ApiProxy(NetworkService networkService, ApiRetro webService, LightTime appLighttime) {
        this.networkService = networkService;
        this.webService = webService;
        this.appLighttime = appLighttime;
    }

    @Override
    public void provideTodaysSchedule(Response<LightTime> response) {
        //we check if what we have is already cached
        if(LocalTimeUtils.isSameDay( appLighttime.getSunRise(), DateTime.now().toString() )){
            response.onResult( LightTimeUtils.clone( appLighttime ) );
        }else if( networkService.isOnline() ){
             webService.provideTodaysSchedule( response );
        }else if( LightTimeUtils.isValid( appLighttime )){
            response.onResult( LightTimeUtils.cloneForAnotherDay( appLighttime, 0 ) );
        }else{
            response.onResult( new LightTime() );
        }
    }

    @Override
    public void provideTomorrowSchedule(Response<LightTime> response) {
        if( networkService.isOnline() ){
            webService.provideTomorrowSchedule( response );
        }else if( LightTimeUtils.isValid( appLighttime )){
            response.onResult( LightTimeUtils.cloneForAnotherDay( appLighttime, 1 ) );
        }else{
            response.onResult( new LightTime() );
        }
    }
}
