package info.juanmendez.daynightthemescheduler.services;

import org.joda.time.LocalTime;

import info.juanmendez.daynightthemescheduler.utils.LocalTimeUtils;
import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;

/**
 * Created by Juan Mendez on 10/27/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 * This class takes care of figuring out the light times needed for either today, tomorrow or both
 */
public class LightTimePlanner {

    public static final int SUNRISE_SCHEDULE = 1;
    public static final int SUNSET_SCHEDULE = 2;
    public static final int TOMORROW_SCHEDULE = 3;

    private ApiRetro apiProxy;
    private NetworkService networkService;
    private LocalTime now  = LocalTime.now();

    /**
     * @param apiProxy makes calls to get
     * @param lightTime
     */
    public LightTimePlanner(ApiRetro apiProxy, NetworkService networkService, LightTime lightTime ) {
        this.apiProxy = apiProxy;
        this.networkService = networkService;
    }

    public void provideNextSchedule(Response<LightTime> response){
        provideTodaySchedule(  response );
    }

    private void provideTodaySchedule( Response<LightTime> response ){
        apiProxy.provideTodaysSchedule(result -> {

            LightTime lightTime = new LightTime( result.getSunRise(), result.getSunSet() );

            LocalTime sunrise = LocalTimeUtils.getLocalTime( lightTime.getSunRise() );
            LocalTime sunset = LocalTimeUtils.getLocalTime( lightTime.getSunSet() );

            int when = whatSchedule( now, sunrise, sunset );

            if( when == SUNRISE_SCHEDULE ){
                lightTime.setNextSchedule( result.getSunRise() );
                response.onResult( lightTime );
            }else if(  when == SUNSET_SCHEDULE ){
                lightTime.setNextSchedule( result.getSunSet() );
                response.onResult( lightTime );
            }else if( when == TOMORROW_SCHEDULE ){
                //ok, we need to call and get tomorrows..
                provideTomorrowSchedule( response );
            }
        });
    }

    private void provideTomorrowSchedule( Response<LightTime> response ){
        apiProxy.provideTomorrowSchedule(result -> {
            LightTime lightTime = new LightTime( result.getSunRise(), result.getSunSet() );
            lightTime.setNextSchedule( result.getSunRise() );
            response.onResult( lightTime );
        });
    }

    public static int whatSchedule( LocalTime now, LocalTime sunrise, LocalTime sunset ){
        if( now.isBefore( sunrise )){
            return SUNRISE_SCHEDULE;
        }else if( now.isBefore( sunset) ){
            return SUNSET_SCHEDULE;
        }else{
            return TOMORROW_SCHEDULE;
        }
    }
}