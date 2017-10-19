package info.juanmendez.daynightthemescheduler;

import java.util.Date;
import java.util.List;

/**
 * Created by Juan Mendez on 10/17/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public interface ApiProxy {
    void provideTodaysSchedule(QuickResponse<List<Date>> respose);
}
