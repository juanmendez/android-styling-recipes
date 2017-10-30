package info.juanmendez.daynightthemescheduler.models;

import info.juanmendez.daynightthemescheduler.services.LightTimeApi;
import info.juanmendez.daynightthemescheduler.services.LocationService;
import info.juanmendez.daynightthemescheduler.services.NetworkService;

/**
 * Created by Juan Mendez on 10/29/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class LightThemeModule {

    NetworkService networkService;
    LocationService locationService;
    LightTimeApi lightTimeApi;
    int observersCount;

    public static LightThemeModule create(){
        return new LightThemeModule();
    }

    public LightThemeModule applyNetworkService(NetworkService networkService) {
        this.networkService = networkService;
        return this;
    }

    public LightThemeModule applyLocationService(LocationService locationService) {
        this.locationService = locationService;
        return this;
    }

    public LightThemeModule applyLighTimeApi(LightTimeApi lightTimeApi) {
        this.lightTimeApi = lightTimeApi;
        return this;
    }

    public LightThemeModule applyObserversCount(int count) {
        this.observersCount = count;
        return this;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    public LocationService getLocationService() {
        return locationService;
    }

    public LightTimeApi getLightTimeApi() {
        return lightTimeApi;
    }

    public int getObserversCount() {
        return observersCount;
    }
}
