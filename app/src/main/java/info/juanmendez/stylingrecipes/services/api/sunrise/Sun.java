package info.juanmendez.stylingrecipes.services.api.sunrise;

import info.juanmendez.daynightthemescheduler.models.LightTime;

/**
 * Created by Juan Mendez on 10/18/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class Sun
{
    private LightTime results;

    private String status;

    public LightTime getResults ()
    {
        return results;
    }

    public void setResults (LightTime results)
    {
        this.results = results;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Sun{" +
                "results=" + results +
                ", status='" + status + '\'' +
                '}';
    }
}
