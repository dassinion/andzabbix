package ru.sonic.zabbix.pro.widget;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;

public class TxtTriggsWidgetProvider extends AppWidgetProvider {
    public static final String URI_SCHEME = "zabbix_widget_text";
	private static final String ACTION_WIDGET_REFRESH = "ru.sonic.zabbix.pro.widget.txt.APPWIDGET_REFRESH";
	private static final String ACTION_WIDGET_NEXTTRIGGER = "ru.sonic.zabbix.pro.widget.txt.APPWIDGET_NEXTTRIGGER";
	private static final String ACTION_WIDGET_TRIGGERSLIST = "ru.sonic.zabbix.pro.widget.txt.APPWIDGET_GOTRIGGERSLIST";
    boolean updating = false;
    public String servername = "Server";
    //private static final String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    //private static final String TAG = "TxtTriggsWidgetProvider";
    //Intent intnt;
    Context ctx;
    static AppWidgetManager appWgtMng;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
    
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        this.ctx = context;
        this.appWgtMng = appWidgetManager;
        //Log.d(TAG, "OnUpdate started");
        for (int appWidgetId: appWidgetIds) {
        	//Log.d(TAG, "OnUpdate ID: "+appWidgetId);
        	SharedPreferences config = context.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
            int updateRateSeconds = config.getInt(String.format(TxtTriggsWidgetControl.PREFS_UPDATE_RATE_FIELD_PATTERN, appWidgetId), -1);
            if (updateRateSeconds != -1) {
            	context.startService(new Intent(context, UpdateWidgetTXTService.class).putExtra("appWidgetId", appWidgetId).putExtra("nexttrig", 0));
            }
        }
    }
    
    public String getCurrentTime() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date now = new Date();
		return dateFormat.format(now);
    }
	
	public String getCurrentServer(int appWidgetId) {
		SharedPreferences config = ctx.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
		return config.getString(String.format(TxtTriggsWidgetControl.PREFS_WIDGET_SERVER, appWidgetId),"server");
	}

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // stop alarm
            Intent widgetUpdate = new Intent();
            widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId)));
            PendingIntent newPending = PendingIntent.getBroadcast(context, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarms.cancel(newPending);

            // remove preference
            //Log.d(LOG_TAG, "Removing preference for id " + appWidgetId);
            SharedPreferences config = context.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
            SharedPreferences.Editor configEditor = config.edit();

            configEditor.remove(String.format(TxtTriggsWidgetControl.PREFS_UPDATE_RATE_FIELD_PATTERN, appWidgetId));
            configEditor.remove(String.format(TxtTriggsWidgetControl.PREFS_WIDGET_SERVER, appWidgetId));
            configEditor.commit();
        }
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    	super.onReceive(context, intent);
    	//Bundle extras = intent.getExtras();
    	
    	try {
	    	this.ctx = context;
	        final String action = intent.getAction();
	        //Log.d(TAG, "OnReceive:Action: " + action);
	        SharedPreferences config = context.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
	        if (context != null && intent != null)
	        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
	            if (!URI_SCHEME.equals(intent.getScheme())) {
	                final int[] appWidgetIds = intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
	                for (int appWidgetId : appWidgetIds) {
	                    int updateRateSeconds = config.getInt(String.format(TxtTriggsWidgetControl.PREFS_UPDATE_RATE_FIELD_PATTERN, appWidgetId), -1);
	                    if (updateRateSeconds != -1) {
	                        //Log.i(TAG, "Starting recurring alarm for id " + appWidgetId);
	                        Intent widgetUpdate = new Intent();
	                        widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	                        widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });
	
	                        widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(TxtTriggsWidgetProvider.URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId)));
	                        PendingIntent newPending = PendingIntent.getBroadcast(context, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
	
	                        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	                        alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRateSeconds * 60000, newPending);
	                        
	        	            context.startService(new Intent(context, UpdateWidgetTXTService.class).putExtra("appWidgetId", appWidgetId).putExtra("nexttrig", 0));
	                    }
	                }
	            }
	        } else if (action.equals(ACTION_WIDGET_REFRESH)) {
	            int appWidgetId = intent.getExtras().getInt("appWidgetId");
	            //Log.d(TAG, "OnReceiver refresh button. WID: "+appWidgetId);
	            context.startService(new Intent(context, UpdateWidgetTXTService.class).putExtra("appWidgetId", appWidgetId).putExtra("nexttrig", 0));
	        } else if (action.equals(ACTION_WIDGET_NEXTTRIGGER)) {
	        	int appWidgetId = intent.getExtras().getInt("appWidgetId");
	        	context.startService(new Intent(context, UpdateWidgetTXTService.class).putExtra("appWidgetId", appWidgetId).putExtra("nexttrig", 1));
	        } else if (action.equals(ACTION_WIDGET_TRIGGERSLIST)) {
	            //Intent ActiveTriggers = new Intent(context,StartTabActivity.class);
	            //ActiveTriggers.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            //context.startActivity(ActiveTriggers);
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        super.onReceive(context, intent);
    }
}