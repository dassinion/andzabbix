package ru.sonic.zabbix.pro.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.api.TriggerApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Trigger;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class UpdateWidgetTXTService extends Service {
	private static final String TAG = "UpdateWidgetTXTService";
	private static final String ACTION_WIDGET_REFRESH = "ru.sonic.zabbix.pro.widget.txt.APPWIDGET_REFRESH";
	private static final String ACTION_WIDGET_NEXTTRIGGER = "ru.sonic.zabbix.pro.widget.txt.APPWIDGET_NEXTTRIGGER";
	private static final String ACTION_WIDGET_TRIGGERSLIST = "ru.sonic.zabbix.pro.widget.txt.APPWIDGET_GOTRIGGERSLIST";
    private static final String PREFS_CURRENT_TRIGG_SHOW = "CurrTrigSHow-%d";
	ExecutorService es;
	Context ctx;
	  
	public void onCreate() {
	    super.onCreate();
	    Log.d(TAG, "MyService onCreate");
	    es = Executors.newFixedThreadPool(1);
	    this.ctx = this;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			if (intent!=null && intent.hasExtra("appWidgetId") && intent.hasExtra("nexttrig")) {
				int appWidgetId = intent.getIntExtra("appWidgetId", -1);
				Log.d(TAG, "onStartCommand appWidgetId: "+appWidgetId);
				int nexttrig = intent.getIntExtra("nexttrig", 0);
				if (appWidgetId!=-1) {
					updateWidgetThreads(appWidgetId, nexttrig, startId);
				}
			}
			//return super.onStartCommand(intent, flags, startId);
			return START_REDELIVER_INTENT;
		} catch (Exception e) {
			e.printStackTrace();
			//return super.onStartCommand(intent, flags, startId);
			return START_REDELIVER_INTENT;
		}
	}
	
	private void updateWidgetThreads(int appWidgetId, int nexttrig, int startId) {
		Log.d(TAG, "updateWidgetThreads strted");
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
   		NetworkInfo ni = cm.getActiveNetworkInfo();
   		if ((ni!=null && ni.isAvailable() && ni.isConnected()) || !isCkeckInternet()) {
   			if (es==null)
   				es = Executors.newFixedThreadPool(1);
   			SetWidgetToLoading lwidget = new SetWidgetToLoading(appWidgetId);
   			es.execute(lwidget);
   			UpdateWidgetTriggers mr = new UpdateWidgetTriggers(appWidgetId, nexttrig, startId);
   			es.execute(mr);
   			//SetWidgetToNormal nwidget = new SetWidgetToNormal(appWidgetId,startId);
   			//es.execute(nwidget);
   		}
	}
	
	private void setButtonAction(Context context, RemoteViews remoteViews, int appWidgetId) {
		//Log.d(TAG, "setButtonAction appWidgetId: "+appWidgetId);
        Intent active = new Intent(context, TxtTriggsWidgetProvider.class);
        active.setAction(ACTION_WIDGET_REFRESH);
        active.putExtra("appWidgetId", appWidgetId);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_button_refresh, actionPendingIntent);
        
        active = new Intent(context, TxtTriggsWidgetProvider.class);
        active.setAction(ACTION_WIDGET_NEXTTRIGGER);
        active.putExtra("appWidgetId", appWidgetId);
        actionPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_button_nexttrigger, actionPendingIntent);
        
        active = new Intent(context, TxtTriggsWidgetProvider.class);
        active.setAction(ACTION_WIDGET_TRIGGERSLIST);
        active.putExtra("appWidgetId", appWidgetId);
        actionPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_button_triggerlist, actionPendingIntent);
        
        remoteViews.setViewVisibility(R.id.widget_button_refresh, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.widget_button_triggerlist, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.widget_button_nexttrigger, View.VISIBLE);
	}

	class UpdateWidgetTriggers implements Runnable {
		int appWidgetId;
		int nexttrig;
		int startId;

		public UpdateWidgetTriggers(int appWidgetId, int nexttrig, int startId) {
			this.appWidgetId = appWidgetId;
			this.nexttrig = nexttrig;
			this.startId = startId;
			//Log.d(LOG_TAG, "MyRun#" + startId + " create");
		}

		public void run() {
			//Log.d(TAG, "MyRun#" + startId + " start, appWidgetId = " + appWidgetId);
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_txttrgs);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			try {
	  			TriggerApiHandler zbx = new TriggerApiHandler(ctx.getApplicationContext(),null);
	  			List<Trigger> listtrig = zbx.getActiveTriggersCount(getCurrentServer(appWidgetId));
	  			String curshow = "";
	  			if (nexttrig==0) {
		  			if (listtrig.size()>0) {
						remoteView.setTextViewText(R.id.widgettextactive, listtrig.get(0).getDescription());
						remoteView.setImageViewResource(R.id.trigger_widget_img, listtrig.get(0).getActiveImg());
						curshow = "1/"+listtrig.size();
						SharedPreferences currenttriggerpref = ctx.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
		                SharedPreferences.Editor configEditor = currenttriggerpref.edit();
		                configEditor.putInt(String.format(PREFS_CURRENT_TRIGG_SHOW, appWidgetId), 1);
		                configEditor.commit();
					} else {
						remoteView.setTextViewText(R.id.widgettextactive, "No active triggers");
						remoteView.setImageViewResource(R.id.trigger_widget_img, R.drawable.close);
						curshow = "0/"+listtrig.size();
						SharedPreferences currenttriggerpref = ctx.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
		                SharedPreferences.Editor configEditor = currenttriggerpref.edit();
		                configEditor.putInt(String.format(PREFS_CURRENT_TRIGG_SHOW, appWidgetId), 0);
		                configEditor.commit();
					}
					remoteView.setViewVisibility(R.id.widgettextdata, View.VISIBLE);
		            remoteView.setTextViewText(R.id.widgettextdata, getCurrentTime());
		            remoteView.setTextViewText(R.id.widgettextserver, getCurrentServer(appWidgetId));
		            remoteView.setTextViewText(R.id.widgetcount, curshow);
		            //setButtonAction(ctx,remoteView,appWidgetId);
		            //appWidgetManager.updateAppWidget(appWidgetId, remoteView);
	  			} else {
	  				SharedPreferences currenttriggerpref = ctx.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
		        	int currtrig = currenttriggerpref.getInt(String.format(PREFS_CURRENT_TRIGG_SHOW, appWidgetId), 0);
		        	//Log.d(LOG_TAG, "currtrig: " + currtrig);
					if (listtrig.size()>0) {
						++currtrig;
						if (currtrig>listtrig.size())
							currtrig = 1;
						curshow = currtrig+"/"+listtrig.size();
						remoteView.setTextViewText(R.id.widgettextactive, listtrig.get(currtrig-1).getDescription());
						remoteView.setImageViewResource(R.id.trigger_widget_img, listtrig.get(currtrig-1).getActiveImg() );
		                SharedPreferences.Editor configEditor = currenttriggerpref.edit();
		                configEditor.putInt(String.format(PREFS_CURRENT_TRIGG_SHOW, appWidgetId), currtrig);
		                configEditor.commit();
					} else {
						currtrig = 0;
						remoteView.setTextViewText(R.id.widgettextactive, "No active triggers");
						remoteView.setImageViewResource(R.id.trigger_widget_img, R.drawable.close);
		                SharedPreferences.Editor configEditor = currenttriggerpref.edit();
		                configEditor.putInt(String.format(PREFS_CURRENT_TRIGG_SHOW, appWidgetId), currtrig);
		                configEditor.commit();
		                curshow = currtrig+"/"+listtrig.size();
					}
					remoteView.setViewVisibility(R.id.widgettextdata, View.VISIBLE);
		            remoteView.setTextViewText(R.id.widgettextdata, getCurrentTime());
		            remoteView.setTextViewText(R.id.widgettextserver, getCurrentServer(appWidgetId));
		            remoteView.setTextViewText(R.id.widgetcount, curshow);
		            //setButtonAction(ctx,remoteView,appWidgetId);
		            //appWidgetManager.updateAppWidget(appWidgetId, remoteView);
	  			}
	    	} catch (ZabbixAPIException e) {
	    		remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_txttrgs);
				remoteView.setTextViewText(R.id.widgettextactive, "Error");
				remoteView.setImageViewResource(R.id.trigger_widget_img, R.drawable.error_icon);
	   			remoteView.setViewVisibility(R.id.widgettextdata, View.VISIBLE);
				e.printStackTrace();
	    	}
			setButtonAction(ctx,remoteView,this.appWidgetId);
			appWidgetManager.updateAppWidget(appWidgetId, remoteView);
		}
	}
	
	class SetWidgetToLoading implements Runnable {
		int appWidgetId;

		public SetWidgetToLoading(int appWidgetId) {
			this.appWidgetId = appWidgetId;
		}

		public void run() {
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_txttrgs);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
	   		//remoteView.setTextViewText(R.id.widgettextdata, "");
	   		remoteView.setTextViewText(R.id.widgettextdata, ctx.getResources().getString(R.string.loading));
	   		//remoteView.setViewVisibility(R.id.widget_button_refresh, View.GONE);
	   		remoteView.setViewVisibility(R.id.widget_button_triggerlist, View.GONE);
	   		remoteView.setViewVisibility(R.id.widget_button_nexttrigger, View.GONE);
	   		appWidgetManager.updateAppWidget(appWidgetId, remoteView);
		}
	}
	
	class SetWidgetToNormal implements Runnable {
		int appWidgetId;
		int startId;

		public SetWidgetToNormal(int appWidgetId, int startId) {
			this.appWidgetId = appWidgetId;
			this.startId = startId;
		}

		public void run() {
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_txttrgs);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			setButtonAction(ctx,remoteView,appWidgetId);
			appWidgetManager.updateAppWidget(appWidgetId, remoteView);
			stop();
		}
		
		void stop() {
			//Log.d(TAG, "MyRun#" + startId + " end, stopSelf(" + startId+ ")");
			stopSelf(startId);
		}
	}
	
	public String getCurrentServer(int appWidgetId) {
		SharedPreferences config = ctx.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
		return config.getString(String.format(TxtTriggsWidgetControl.PREFS_WIDGET_SERVER, appWidgetId),"server");
	}
	
	public String getCurrentTime() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date now = new Date();
		return dateFormat.format(now);
    }
	
	public boolean isCkeckInternet() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		return prefs.getBoolean("internet_check_fix",true);
	}
}