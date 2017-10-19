package info.juanmendez.stylingrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import info.juanmendez.stylingrecipes.services.api.sunset_sunrise.SunTimes;
import info.juanmendez.stylingrecipes.services.api.sunset_sunrise.SunsetService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;


/**
 * Created by Juan Mendez on 10/16/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

@EActivity(R.layout.activity_second)
public class SecondActivity extends AppCompatActivity {

    @AfterViews
    public void afterViews() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.sunrise-sunset.org").addConverterFactory(GsonConverterFactory.create()).build();
        SunsetService service = retrofit.create( SunsetService.class );

        Call<SunTimes> call = service.getTimes(41.8500300, 41.8500300, 0 );

        call.enqueue(new Callback<SunTimes>() {
            @Override
            public void onResponse(Call<SunTimes> call, Response<SunTimes> response) {
                SunTimes SunTimes=response.body();
            }

            @Override
            public void onFailure(Call<SunTimes> call, Throwable t) {
                Timber.i( "error %s", t.getMessage() 
                );
            }
        });
    }

    @Click
    public void goToFirstBtn(){
        startActivity( new Intent(this, MainActivity_.class) );
    }
}
