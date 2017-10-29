package info.juanmendez.stylingrecipes;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.widget.RemoteViews;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

import info.juanmendez.stylingrecipes.services.DroidLightTimeRetro;
import info.juanmendez.stylingrecipes.services.DroidNetworkService;
import timber.log.Timber;


/**
 * Created by Juan Mendez on 9/4/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EReceiver
public class WidgetProvider extends AppWidgetProvider {
    @Pref
    ThemePrefs_ themePrefs;

    @App
    MyApp myApp;

    @Bean
    DroidNetworkService networkService;

    @Bean
    DroidLightTimeRetro lightTimeRetro;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i( "widget.onReceive! " + intent.getAction());
        if ( intent.getAction() == null ) {

            int[] widget_ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            for( int widget_id: widget_ids){
                updateWidget( context, AppWidgetManager.getInstance(context), widget_id );
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        int len = appWidgetIds.length;

        for( int i = 0; i < len; i++ ) {
            updateWidget( ctxt, appWidgetManager, appWidgetIds[i]);
        }

        super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
    }

    private void updateWidget( Context context, AppWidgetManager manager, int widgetId) {
        Timber.i( "udpate widget " + widgetId  );
        if( widgetId > 0 ){

            /**
             * The only way to update a widget day-night theme is done by choosing the layout
             */
            getLightTimes();
            RemoteViews widget = new RemoteViews(context.getPackageName(),
                            isDayTime(context)?R.layout.widget_layout :
                            R.layout.widet_layout_night);

            manager.updateAppWidget(widgetId, widget);
        }
    }

    private void getLightTimes(){

        if( themePrefs.isLocationGranted().getOr(false) ){
            Timber.e( "there is no locationGranted stored, or is false");
            return;
        }

        lightTimeRetro.generateTodayTimeLight(result -> {
            Timber.i( "LightTime result for today %s", result );
        });

        lightTimeRetro.generateTomorrowTimeLight( result -> {
            Timber.i( "LightTime result for tomorrow %s", result );
        });

        Timber.i( "Is there connection %s", networkService.isOnline()?"yes":"false");
    }

    /**
     * Tells if the app is in day or night theme. It doesn't understand night-auto. But it sure
     * figures out what theme cor,responds.
     * @see https://medium.com/@chrisbanes/appcompat-v23-2-daynight-d10f90c83e94
     * @return
     */
    private boolean isDayTime(Context context){
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return Configuration.UI_MODE_NIGHT_NO == currentMode;
    }
}