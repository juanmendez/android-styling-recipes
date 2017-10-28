package info.juanmendez.stylingrecipes.services.api.sunrise;

import info.juanmendez.daynightthemescheduler.models.LightTime;

/**
 * Created by Juan Mendez on 10/18/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class LightTimeResponse
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
        return "LightTimeResponse{" +
                "results=" + results +
                ", status='" + status + '\'' +
                '}';
    }
}
