package info.juanmendez.daynightthemescheduler.models;

import android.support.annotation.NonNull;

/**
 * Created by Juan Mendez on 10/27/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class LightTime {
    private String sunrise = "";
    private String sunset = "";
    private String nextSchedule = "";

    public LightTime(@NonNull String sunrise, @NonNull String sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public LightTime() {
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getNextSchedule() {
        return nextSchedule;
    }

    public void setNextSchedule(String nextSchedule) {
        this.nextSchedule = nextSchedule;
    }
}
