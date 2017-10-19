package info.juanmendez.stylingrecipes.services.api.sunset_sunrise;

/**
 * Created by Juan Mendez on 10/18/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class Results
{
    private String sunset;
    private String sunrise;

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
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