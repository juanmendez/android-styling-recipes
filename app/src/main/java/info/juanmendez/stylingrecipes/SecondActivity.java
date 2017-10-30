package info.juanmendez.stylingrecipes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import info.juanmendez.stylingrecipes.services.DroidLightTimeApi;
import info.juanmendez.stylingrecipes.services.DroidNetworkService;
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

    @Bean
    DroidLightTimeApi lightTimeRetro;

    @AfterViews
    public void afterViews() {
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode == 1 ){
            getLightTimes();
        }
    }

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }else{
            getLightTimes();
        }
    }

    private void getLightTimes(){
        lightTimeRetro.generateTodayTimeLight(result -> {
            Timber.i( "LightTime result for today %s", result );
        });

        lightTimeRetro.generateTomorrowTimeLight( result -> {
            Timber.i( "LightTime result for tomorrow %s", result );
        });

        Timber.i( "Is there connection %s", networkService.isOnline()?"yes":"false");
    }

    @Click
    public void goToFirstBtn(){
        startActivity( new Intent(this, MainActivity_.class) );
    }
}
