package info.juanmendez.stylingrecipes.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.services.LightTimeRetro;
import info.juanmendez.stylingrecipes.services.api.sunrise.LightTimeCalls;
import info.juanmendez.stylingrecipes.services.api.sunrise.LightTimeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Juan Mendez on 10/28/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean
public class DroidLightTimeRetro implements LightTimeRetro {

    @SystemService
    LocationManager locationManager;

    @RootContext
    Context rootContext;

    Retrofit retrofit;
    LightTimeCalls lightTimeCalls;

    public DroidLightTimeRetro() {
        retrofit = new Retrofit.Builder().baseUrl("https://api.sunrise-sunset.org").addConverterFactory(GsonConverterFactory.create()).build();
        lightTimeCalls = retrofit.create(LightTimeCalls.class);
    }

    @Override
    public void generateTodayTimeLight(Response<LightTime> response) {
        makeCall("today", response);
    }

    @Override
    public void generateTomorrowTimeLight(Response<LightTime> response) {
        makeCall("tomorrow", response);
    }

    private void makeCall(String dateString, Response<LightTime> response) {

        Location location = requestLocation();

        Call<LightTimeResponse> call = lightTimeCalls.getLightTime(location.getLatitude(), location.getLongitude(), 0, dateString);

        call.enqueue(new Callback<LightTimeResponse>() {
            @Override
            public void onResponse(Call<LightTimeResponse> call, retrofit2.Response<LightTimeResponse> retrofitResponse) {
                LightTimeResponse sun = retrofitResponse.body();

                if (sun.getStatus().equals("OK")) {
                    response.onResult(sun.getResults());
                } else {
                    response.onResult(new LightTime());
                }
            }

            @Override
            public void onFailure(Call<LightTimeResponse> call, Throwable t) {
                response.onResult(new LightTime());
            }
        });
    }

    @SuppressLint("MissingPermission")
    private Location requestLocation() {

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

        return null;
    }
}
