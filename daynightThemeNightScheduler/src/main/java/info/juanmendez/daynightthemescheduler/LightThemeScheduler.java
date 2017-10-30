package info.juanmendez.daynightthemescheduler;

import info.juanmendez.daynightthemescheduler.services.LightThemeClient;

/**
 * Created by Juan Mendez on 10/17/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

class LightThemeScheduler {

    LightThemeClient client;
    LightThemeScheduler scheduler;

    public LightThemeScheduler(LightThemeClient lightTimeOwner) {
        client = lightTimeOwner;
    }
}
