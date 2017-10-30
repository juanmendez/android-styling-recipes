package info.juanmendez.daynightthemescheduler.services;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;

/**
 * Created by Juan Mendez on 10/17/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public interface LightTimeApi {
    void generateTodayTimeLight(Response<LightTime> respose);
    void generateTomorrowTimeLight(Response<LightTime> response );
}
