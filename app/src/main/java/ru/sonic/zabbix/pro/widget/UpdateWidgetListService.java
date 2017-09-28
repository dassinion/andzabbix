package ru.sonic.zabbix.pro.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.database.DBAdapter;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetListService extends Service {
	private static final String TAG = "UpdateWidgetListService";
	public static String ACTION_WIDGET_REFRESH = "ru.sonic.zabbix.pro.widget.listwidget.APPWIDGET_LISTREFRESH";
	public static final String ACTION_ON_CLICK = "ru.sonic.zabbix.pro.widget.listwidget.itemonclick";
	//public static final String ITEM_POSITION = "item_position";
	String server_name = "";
	ExecutorService es;
	Context ctx;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
	    super.onCreate();
	    es = Executors.newFixedThreadPool(1);
	    this.ctx = this;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			Log.d(TAG, "onStartCommand");
			if (intent!=null && intent.hasExtra("appWidgetId") && intent.hasExtra("server_name")) {
				int appWidgetId = intent.getIntExtra("appWidgetId", -1);
				server_name = intent.getStringExtra("server_name");
				Log.d(TAG, "onStartCommand appWidgetId: "+appWidgetId);
				if (appWidgetId!=-1) {
					updateWidgetThreads(appWidgetId, startId);
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
	
	private void updateWidgetThreads(int appWidgetId, int startId) {
		Log.d(TAG, "updateWidgetThreads started");
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
   		NetworkInfo ni = cm.getActiveNetworkInfo();
   		if ((ni!=null && ni.isAvailable() && ni.isConnected()) || !isCkeckInternet()) {
   			if (es==null)
				es = Executors.newFixedThreadPool(1);
			SetWidgetToLoading lwidget = new SetWidgetToLoading(appWidgetId);
			es.execute(lwidget);
			UpdateWidgetTriggers mr = new UpdateWidgetTriggers(appWidgetId, startId);
			es.execute(mr);
			//SetWidgetToNormal nwidget = new SetWidgetToNormal(appWidgetId,startId);
			//es.execute(nwidget);
   		}
	}
	
	private void setButtonAction(Context context, RemoteViews remoteViews, int appWidgetId) {
		Log.d(TAG, "setButtonAction appWidgetId: "+appWidgetId);
        Intent active = new Intent(context, ListWidgetProvider.class);
        active.setAction(ACTION_WIDGET_REFRESH);
        active.putExtra("appWidgetId", appWidgetId);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_list_refresh, actionPendingIntent);
	}
	
	class UpdateWidgetTriggers implements Runnable {
		int appWidgetId;
		//int nexttrig;
		int startId;

		public UpdateWidgetTriggers(int appWidgetId, int startId) {
			this.appWidgetId = appWidgetId;
			this.startId = startId;
			//Log.d(LOG_TAG, "MyRun#" + startId + " create");
		}

		public void run() {
			//Log.d(TAG, "MyRun#" + startId + " start, appWidgetId = " + appWidgetId);
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_list);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			try {
	  			remoteView.setTextViewText(R.id.widget_list_01, getCurrentServer(appWidgetId));
	  			remoteView.setTextViewText(R.id.widget_list_03, "...");
	  			
	  			Intent svcIntent=new Intent(ctx, ListWidgetRVService.class);
	  			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	  	      	svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
	  			remoteView.setRemoteAdapter(R.id.widget_triggers_list, svcIntent);
	  			
	  			Intent clickIntent=new Intent(ctx, ListWidgetProvider.class);
	  			clickIntent.setAction(ACTION_ON_CLICK);
	  			PendingIntent listClickPIntent = PendingIntent.getBroadcast(ctx, 0,clickIntent, 0);
	  	      	remoteView.setPendingIntentTemplate(R.id.widget_triggers_list, listClickPIntent);
	  			
	  			//setButtonAction(ctx,remoteView,this.appWidgetId);
	  	      	//appWidgetManager.updateAppWidget(appWidgetId, remoteView);
	  	      	
				remoteView.setTextViewText(R.id.widget_list_02, getCurrentTime());
				remoteView.setTextViewText(R.id.widget_list_03, "");
				
	  	        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.widget_triggers_list);
	    	} catch (Exception e) {
				remoteView.setTextViewText(R.id.widget_list_02, "Error");
				//remoteView.setImageViewResource(R.id.trigger_widget_img, R.drawable.error_icon);
	   			//remoteView.setViewVisibility(R.id.widgettextdata, View.VISIBLE);
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
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_list);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
	   		remoteView.setTextViewText(R.id.widget_list_02, ctx.getResources().getString(R.string.loading));
	   		appWidgetManager.updateAppWidget(appWidgetId, remoteView);
		}
	}
	
	/*
	class SetWidgetToNormal implements Runnable {
		int appWidgetId;
		int startId;

		public SetWidgetToNormal(int appWidgetId, int startId) {
			this.appWidgetId = appWidgetId;
			this.startId = startId;
		}

		public void run() {
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_list);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			//remoteView.setTextViewText(R.id.widget_list_02, getCurrentTime());
			remoteView.setTextViewText(R.id.widget_list_03, "");
			appWidgetManager.updateAppWidget(appWidgetId, remoteView);
			stop();
		}
		
		void stop() {
			//Log.d(TAG, "MyRun#" + startId + " end, stopSelf(" + startId+ ")");
			stopSelf(startId);
		}
	}*/
	
	public String getCurrentServer(int appWidgetId) {
		SharedPreferences config = ctx.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
		return config.getString(String.format(TxtTriggsWidgetControl.PREFS_WIDGET_SERVER, appWidgetId),"server");
	}
	
	public boolean isCkeckInternet() {
		SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(ctx);
		return config.getBoolean("internet_check_fix",true);
	}
	
	public String getCurrentTime() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date now = new Date();
		return dateFormat.format(now);
    }

	public Server getSelectedServer() {
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		return db.selectServer(server_name);
	}
}
