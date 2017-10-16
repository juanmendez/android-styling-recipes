package info.juanmendez.stylingrecipes;

import android.support.v7.app.AppCompatDelegate;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Juan Mendez on 10/16/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface ThemePrefs {
    @DefaultInt(AppCompatDelegate.MODE_NIGHT_AUTO)
    int dayNightMode();
}
