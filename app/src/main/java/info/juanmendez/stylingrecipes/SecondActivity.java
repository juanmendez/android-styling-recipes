package info.juanmendez.stylingrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import info.juanmendez.stylingrecipes.services.api.sunset_sunrise.Sun;
import info.juanmendez.stylingrecipes.services.api.sunset_sunrise.SunriseSunset;
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
        SunriseSunset service = retrofit.create( SunriseSunset.class );

        Call<Sun> call = service.getTimes(41.8500300, -87.6500500, 0 );

        call.enqueue(new Callback<Sun>() {
            @Override
            public void onResponse(Call<Sun> call, Response<Sun> response) {
                Sun sun=response.body();

                if( sun.getStatus().equals("OK")){
                    Timber.i("UTC sunrinse at %s and sunset at %s", sun.getResults().getSunrise(), sun.getResults().getSunset() );
                }else{
                    Timber.i( "sunrise-sunset.org may be down");
                }
            }

            @Override
            public void onFailure(Call<Sun> call, Throwable t) {
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
