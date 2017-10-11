package info.juanmendez.stylingrecipes;

import android.app.Application;

import timber.log.Timber;


/**
 * Created by Juan Mendez on 10/11/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant( new Timber.DebugTree());
    }
}
