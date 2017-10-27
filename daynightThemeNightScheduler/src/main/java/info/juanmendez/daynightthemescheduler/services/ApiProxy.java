package info.juanmendez.daynightthemescheduler.services;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;

/**
 * Created by Juan Mendez on 10/27/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class ApiProxy implements ApiWS{
    NetworkService networkService;
    LightTime appLighttime;
    ApiWS webService;

    public ApiProxy(NetworkService networkService, ApiWS webService, LightTime appLighttime) {
        this.networkService = networkService;
        this.webService = webService;
        this.appLighttime = appLighttime;
    }
    
    @Override
    public void provideTodaysSchedule(Response<LightTime> respose) {
        if( networkService.isOnline() ){
            webService.provideTodaysSchedule( respose );
        }else{

        }
    }

    @Override
    public void provideTomorrowSchedule(Response<LightTime> response) {
        if( networkService.isOnline() ){
            webService.provideTodaysSchedule( response );
        }else{

        }
    }
}
