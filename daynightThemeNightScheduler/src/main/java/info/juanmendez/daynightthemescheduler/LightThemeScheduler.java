package info.juanmendez.daynightthemescheduler;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.LightTimeModule;

/**
 * Created by Juan Mendez on 10/17/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

class LightThemeScheduler {

    LightTimeModule lightTimeModule;
    LightTime appLightTime;

    public LightThemeScheduler(LightTimeModule lightTimeModule, LightTime appLightTime ) {
        this.lightTimeModule = lightTimeModule;
        this.appLightTime = appLightTime;
    }
}
