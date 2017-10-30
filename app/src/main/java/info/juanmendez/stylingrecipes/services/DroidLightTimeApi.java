package info.juanmendez.stylingrecipes.services;

import android.location.Location;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.services.LightTimeApi;
import info.juanmendez.stylingrecipes.services.api.sunrise.LightTimeCalls;
import info.juanmendez.stylingrecipes.services.api.sunrise.LightTimeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;


/**
 * Created by Juan Mendez on 10/28/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean
public class DroidLightTimeApi implements LightTimeApi {

    @Bean
    DroidLocationService locationService;

    Retrofit retrofit;
    LightTimeCalls lightTimeCalls;

    public DroidLightTimeApi() {
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

        Location location = locationService.getLastKnownLocation();

        if( location == null ){
            Timber.e( "There is no location found!");
            response.onResult( new LightTime() );
            return;
        }

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
}
