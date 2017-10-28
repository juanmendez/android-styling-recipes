package info.juanmendez.stylingrecipes.services;

import info.juanmendez.daynightthemescheduler.models.LightTime;
import info.juanmendez.daynightthemescheduler.models.Response;
import info.juanmendez.daynightthemescheduler.services.LightTimeRetro;
import info.juanmendez.stylingrecipes.services.api.sunrise.LightTimeCalls;
import info.juanmendez.stylingrecipes.services.api.sunrise.LightTimeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


/**
 * Created by Juan Mendez on 10/28/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
public class DroidLightTimeRetro implements LightTimeRetro {
    Retrofit retrofit;
    LightTimeCalls lightTimeCalls;

    public DroidLightTimeRetro(Retrofit retrofit, LightTimeCalls lightTimeCalls) {
        this.retrofit = retrofit;
        this.lightTimeCalls = lightTimeCalls;
    }

    @Override
    public void generateTodayTimeLight(Response<LightTime> response) {
        Call<LightTimeResponse> call = lightTimeCalls.getLightTime(41.8500300, -87.6500500, 0 );

        call.enqueue(new Callback<LightTimeResponse>() {
            @Override
            public void onResponse(Call<LightTimeResponse> call, retrofit2.Response<LightTimeResponse> retrofitResponse) {
                LightTimeResponse sun= retrofitResponse.body();

                if( sun.getStatus().equals("OK")){
                    response.onResult( sun.getResults() );
                }else{
                    response.onResult( new LightTime() );
                }
            }

            @Override
            public void onFailure(Call<LightTimeResponse> call, Throwable t) {
                response.onResult( new LightTime() );
            }
        });
    }

    @Override
    public void generateTomorrowTimeLight(Response<LightTime> response) {

    }
}
