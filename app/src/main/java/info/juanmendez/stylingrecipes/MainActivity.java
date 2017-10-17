package info.juanmendez.stylingrecipes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Pref
    ThemePrefs_ themePrefs;

    @ViewById
    RadioGroup radioGroup;

    @ViewById
    RadioButton autoRadioButton, dayOnlyRadioButton, nightOnlyRadioButton;

    @App
    MyApp app;

    @AfterViews
    public void afterViews(){

        reflectThemeChoice( themePrefs.dayNightMode().get() );

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            saveThemeChoice(checkedId);

            //I removed this, but it turned out the second activity wasn't updated
            //unless I closed and opened the app again.
            app.reflectNightModeTheme();

            //update takes place, and reloads the activity!
            getDelegate().setLocalNightMode( themePrefs.dayNightMode().get() );
            updateWidgets();
        });
    }

    @Click
    public void goToSecondBtn(){
        startActivity( new Intent(this, SecondActivity_.class) );
    }

    private void reflectThemeChoice(int choiceMade){
        switch ( choiceMade){
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                autoRadioButton.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                dayOnlyRadioButton.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                nightOnlyRadioButton.setChecked(true);
                break;
        }
    }

    private void saveThemeChoice( int radioButtonId ){
        switch ( radioButtonId){
            case R.id.autoRadioButton:
                themePrefs.dayNightMode().put( AppCompatDelegate.MODE_NIGHT_AUTO );
                break;
            case R.id.dayOnlyRadioButton:
                themePrefs.dayNightMode().put( AppCompatDelegate.MODE_NIGHT_NO );
                break;
            case R.id.nightOnlyRadioButton:
                themePrefs.dayNightMode().put( AppCompatDelegate.MODE_NIGHT_YES );
                break;
        }
    }

    private void updateWidgets(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(app);
        ComponentName componentName = new ComponentName( app, WidgetProvider_.class);

        int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);

        Intent  intent = new Intent(this, WidgetProvider_.class );
        intent.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE );
        intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds );
        sendBroadcast( intent );
    }
}
