package info.juanmendez.stylingrecipes;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import timber.log.Timber;


/**
 * Created by Juan Mendez on 10/11/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

@EApplication
public class MyApp extends Application {

    @Pref
    ThemePrefs_ themePrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant( new Timber.DebugTree());
        reflectNightModeTheme();
        Timber.i( "last stored %d", themePrefs.dayNightMode().get());
    }

    public void reflectNightModeTheme(){
        AppCompatDelegate.setDefaultNightMode(themePrefs.dayNightMode().get());
    }
}
