package info.juanmendez.stylingrecipes.services;

import android.net.ConnectivityManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import info.juanmendez.daynightthemescheduler.services.NetworkService;


/**
 * Created by Juan Mendez on 10/28/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean
public class DroidNetworkService implements NetworkService {
    @SystemService
    ConnectivityManager cm;

    @Override
    public boolean isOnline() {
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
