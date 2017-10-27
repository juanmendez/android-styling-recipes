package info.juanmendez.daynightthemescheduler.models;

import android.support.annotation.NonNull;

/**
 * Created by Juan Mendez on 10/27/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class LightTime {
    private String sunRise = "";
    private String sunSet = "";
    private String nextSchedule = "";

    public LightTime(@NonNull String sunRise, @NonNull String sunSet) {
        this.sunRise = sunRise;
        this.sunSet = sunSet;
    }

    public LightTime() {
    }

    public String getSunRise() {
        return sunRise;
    }

    public void setSunRise(String sunRise) {
        this.sunRise = sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
    }

    public String getNextSchedule() {
        return nextSchedule;
    }

    public void setNextSchedule(String nextSchedule) {
        this.nextSchedule = nextSchedule;
    }
}
