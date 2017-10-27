package info.juanmendez.daynightthemescheduler.models;

/**
 * Created by Juan Mendez on 10/17/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public interface Response<T> {
    void onResult(T result );
}
