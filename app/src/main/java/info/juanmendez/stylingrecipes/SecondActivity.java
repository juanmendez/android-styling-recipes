package info.juanmendez.stylingrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import info.juanmendez.stylingrecipes.services.DroidLightTimeRetro;
import info.juanmendez.stylingrecipes.services.DroidNetworkService;
import info.juanmendez.stylingrecipes.services.api.sunrise.LightTimeCalls;
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

    @Bean
    DroidNetworkService networkService;

    @AfterViews
    public void afterViews() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.sunrise-sunset.org").addConverterFactory(GsonConverterFactory.create()).build();
        LightTimeCalls service = retrofit.create( LightTimeCalls.class );

        DroidLightTimeRetro lightTimeRetro = new DroidLightTimeRetro(retrofit, service );

        lightTimeRetro.generateTodayTimeLight(result -> {
            Timber.i( "LightTime result %s", result );
        });

        Timber.i( "Is there connection %s", networkService.isOnline()?"yes":"false");
    }

    @Click
    public void goToFirstBtn(){
        startActivity( new Intent(this, MainActivity_.class) );
    }
}
