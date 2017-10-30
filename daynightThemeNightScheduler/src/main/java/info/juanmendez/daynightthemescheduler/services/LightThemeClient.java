package info.juanmendez.daynightthemescheduler.services;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.LightThemeModule;

/**
 * Created by Juan Mendez on 10/29/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public interface LightThemeClient {
    void cancelIfRunning(); //cancel alarmService if it's running
    void scheduleNext(LightTime lightTime ); //provide next schedule
    void setLightMode( int type );

    LightTime getAppLightTime(); //provide last stored data for sunrise/sunset & schedule time
    LightThemeModule getLightTimeModule();
}