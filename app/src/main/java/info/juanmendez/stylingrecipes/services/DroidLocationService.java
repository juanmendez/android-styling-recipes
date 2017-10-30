package info.juanmendez.stylingrecipes.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import info.juanmendez.daynightthemescheduler.services.LocationService;

/**
 * Created by Juan Mendez on 10/29/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean
public class DroidLocationService implements LocationService {

    @RootContext
    Context rootContext;

    @SystemService
    LocationManager locationManager;

    @Override
    public boolean isGranted() {

        return ActivityCompat.checkSelfPermission(rootContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(rootContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
    }



    @SuppressLint("MissingPermission")
    @Override
    public Location getLastKnownLocation() {
        if( isGranted() ){

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {

                if (isNetworkEnabled) {
                    return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }else if (isGPSEnabled) {
                    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }

        return null;
    }
}
