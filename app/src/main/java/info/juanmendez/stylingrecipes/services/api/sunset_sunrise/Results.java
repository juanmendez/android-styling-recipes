package info.juanmendez.stylingrecipes.services.api.sunset_sunrise;

import java.util.Date;

/**
 * Created by Juan Mendez on 10/18/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class Results
{
    private Date sunset;
    private Date sunrise;

    public Date getSunset() {
        return sunset;
    }

    public void setSunset(Date sunset) {
        this.sunset = sunset;
    }

    public Date getSunrise() {
        return sunrise;
    }

    public void setSunrise(Date sunrise) {
        this.sunrise = sunrise;
    }

    @Override
    public String toString() {
        return "Results{" +
                "sunset='" + sunset + '\'' +
                ", sunrise='" + sunrise + '\'' +
                '}';
    }
}