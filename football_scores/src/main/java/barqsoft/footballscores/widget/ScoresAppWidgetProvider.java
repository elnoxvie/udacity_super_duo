package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.util.Util;

import java.util.Calendar;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.receiver.ServiceUpdateBroadcast;
import barqsoft.footballscores.util.Utilities;

/**
 * Created by elnoxvie on 26/8/15.
 */
public class ScoresAppWidgetProvider extends AppWidgetProvider{

    public static String ACTION_NEXT = "barqsoft.footballscores.scoreswidget.next";
    public static String ACTION_PREV  = "barqsoft.footballscores.scoreswidget.previous";
    public static String ACTION_UPDATE = "barqsoft.footballscores.scoreswidget.previous";


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        if (action.equals(ACTION_NEXT)){
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);


            //check if the widget is invalid
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                int mode = Utilities.getDateModeFromPreference(context, appWidgetId);
                int nextMode = Utilities.getNextMode(mode);
                Utilities.setDateModeToPreference(context, appWidgetId, nextMode);

                updateAppWidget(context, mgr, appWidgetId);
            }


            mgr.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView);
        }else if (action.equals(ACTION_PREV)){
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            //check if the widget is invalid
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
                int mode = Utilities.getDateModeFromPreference(context, appWidgetId);
                int prevMode = Utilities.getPrevMode(mode);
                Utilities.setDateModeToPreference(context, appWidgetId, prevMode);

                updateAppWidget(context, mgr, appWidgetId);
            }

            mgr.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView);
        }else if (action.equals(ACTION_UPDATE)){

            final ComponentName cn = new ComponentName(context, ScoresAppWidgetProvider.class);

            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listView);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++){
            Utilities.removeDateModeFromPreference(context, appWidgetIds[i]);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){


        int mode = Utilities.getDateModeFromPreference(context, appWidgetId);


        // Sets up the intent that points to the StackViewService that will
        // provide the views for this collection.
        Intent intent = new Intent(context, ScoreWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        //rv.setRemoteAdapter(appWidgetIds[i], R.id.listView, intent);
        if (Build.VERSION.SDK_INT >= 14){
            rv.setRemoteAdapter(R.id.listView, intent);
        }else{
            rv.setRemoteAdapter(appWidgetId, R.id.listView, intent);
        }

        rv.setEmptyView(R.id.listView, android.R.id.empty);

        Calendar calendar = Utilities.getCalendarByMode(mode);
        long timeInMillis = calendar.getTimeInMillis();
        rv.setTextViewText(R.id.dayName, Utilities.getDayName(context, timeInMillis));


        switch (mode){
            case Utilities.DAY_BEFORE_YESTERDAY:
                rv.setViewVisibility(R.id.prev, View.INVISIBLE);
                break;
            case Utilities.YESTERDAY:
                rv.setViewVisibility(R.id.prev, View.VISIBLE);
                break;
            case Utilities.TODAY:
                break;
            case Utilities.TOMORROW:
                rv.setViewVisibility(R.id.next, View.VISIBLE);
                break;
            case Utilities.DAY_AFTER_TOMORROW:
                rv.setViewVisibility(R.id.prev, View.VISIBLE);
                rv.setViewVisibility(R.id.next, View.INVISIBLE);
                break;
        }

        Intent updateIntent = new Intent(context, ServiceUpdateBroadcast.class);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.refresh, updatePendingIntent);

        Intent nextIntent = new Intent(context, ScoresAppWidgetProvider.class);
        nextIntent.setAction(ScoresAppWidgetProvider.ACTION_NEXT);
        nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        rv.setOnClickPendingIntent(R.id.next, nextPendingIntent);

        Intent startIntent = new Intent(context, MainActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.putExtra(MainActivity.EXTRA_SELECTION_FROM_WIDGET, mode);
        PendingIntent startPendingIntent = PendingIntent.getActivity(context, appWidgetId, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        rv.setOnClickPendingIntent(R.id.dayName, startPendingIntent);

        Intent prevIntent = new Intent(context, ScoresAppWidgetProvider.class);
        prevIntent.setAction(ScoresAppWidgetProvider.ACTION_PREV);
        prevIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        rv.setOnClickPendingIntent(R.id.prev, prevPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    public static void updateWidgets(Context context){
        Intent intent = new Intent(context, ScoresAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(new ComponentName(context.getApplicationContext(), ScoresAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }

}
