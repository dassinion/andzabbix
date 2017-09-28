package ru.sonic.zabbix.pro.widget;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.api.GraphApiHandler;

import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.database.DBAdapter;


import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

public class UpdateWidgetGraphService extends Service {
	private static final String TAG = "UpdateWidgetGraphService";
	private static final String ACTION_WIDGET_GRAPHCLICK = "ru.sonic.zabbix.pro.widget.graphwidget.APPWIDGET_GRAPHCLICH";
	private static final String ACTION_WIDGET_GRAPHREFRESH = "ru.sonic.zabbix.pro.widget.graphwidget.APPWIDGET_GRAPHREFRESH";
	private String baseurl = "";
	//private String chart_type_item = "/chart.php?itemid=";
	private String chart_type_graph = "/chart2.php?graphid=";
	private String charturl = chart_type_graph;
	String graphID = "";
	String server_name = "";
	ExecutorService es;
	Context ctx;

	public void onCreate() {
	    super.onCreate();
	    //Log.d(TAG, "MyService onCreate");
	    es = Executors.newFixedThreadPool(1);
	    this.ctx = this;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			if (intent!=null && intent.hasExtra("appWidgetId") && intent.hasExtra("server_name")) {
				int appWidgetId = intent.getIntExtra("appWidgetId", -1);
				graphID = intent.getStringExtra("graphID");
				server_name = intent.getStringExtra("server_name");
				//Log.d(TAG, "Widget graph ID: "+graphID);
				if (appWidgetId!=-1) {
					updateWidgetThreads(appWidgetId, graphID, startId);
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
	
	private void updateWidgetThreads(int appWidgetId, String graphID, int startId) {
		//Log.d(TAG, "updateWidgetThreads strted");
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
   		NetworkInfo ni = cm.getActiveNetworkInfo();
   		if ((ni!=null && ni.isAvailable() && ni.isConnected()) || !isCkeckInternet()) {
   			if (es==null)
   				es = Executors.newFixedThreadPool(1);
   			SetWidgetToLoading lwidget = new SetWidgetToLoading(appWidgetId);
   			es.execute(lwidget);
   			UpdateWidgetGraphs mr = new UpdateWidgetGraphs(appWidgetId, graphID, startId);
   			es.execute(mr);
   			//SetWidgetToNormal nwidget = new SetWidgetToNormal(appWidgetId,startId);
   			//es.execute(nwidget);
   		}
   		SetWidgetToNormal nwidget = new SetWidgetToNormal(appWidgetId,startId);
   		es.execute(nwidget);
	}
	
	private void setButtonAction(Context context, RemoteViews remoteViews, int appWidgetId) {
		//Log.d(TAG, "setButtonAction appWidgetId: "+appWidgetId);
        Intent active = new Intent(context, GraphWidgetProvider.class);
        active.setAction(ACTION_WIDGET_GRAPHREFRESH);
        active.putExtra("appWidgetId", appWidgetId);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_graph_refresh, actionPendingIntent);
        
        active = new Intent(context, GraphWidgetProvider.class);
        active.setAction(ACTION_WIDGET_GRAPHCLICK);
        active.putExtra("appWidgetId", appWidgetId);
        actionPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_graph_triggerlist, actionPendingIntent);
        
        remoteViews.setViewVisibility(R.id.widget_graph_refresh, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.widget_graph_triggerlist, View.VISIBLE);
	}

	class UpdateWidgetGraphs implements Runnable {
		int appWidgetId;
		String graphID;
		int startId;

		public UpdateWidgetGraphs(int appWidgetId, String graphID, int startId) {
			this.appWidgetId = appWidgetId;
			this.graphID = graphID;
			this.startId = startId;
			//Log.d(LOG_TAG, "MyRun#" + startId + " create");
		}

		public void run() {
			//Log.d(TAG, "MyRun#" + startId + " start, appWidgetId = " + appWidgetId);
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_graphs);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			try {
				GraphApiHandler api = new GraphApiHandler(ctx,getSelectedServer());
				baseurl = api.getAPIURL().split("/api_jsonrpc.php")[0];
				String imageurl = makeFullUrl(600,0,3600);
				Bitmap img = api.getGraphImage(imageurl);
				remoteView.setImageViewBitmap(R.id.widget_graph_img,img);
				remoteView.setTextViewText(R.id.widget_graph_text01, server_name);
				remoteView.setTextViewText(R.id.widget_graph_text02, getCurrentTime());
				//setButtonAction(ctx,remoteView,this.appWidgetId);
				//appWidgetManager.updateAppWidget(appWidgetId, remoteView);
	    	} catch (ZabbixAPIException e) {
				remoteView.setTextViewText(R.id.widget_graph_text02, "Error");
				remoteView.setImageViewResource(R.id.widget_graph_img, R.drawable.error_icon);
				//setButtonAction(ctx,remoteView,this.appWidgetId);
				//appWidgetManager.updateAppWidget(appWidgetId, remoteView);
				e.printStackTrace();
	    	} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setButtonAction(ctx,remoteView,this.appWidgetId);
			appWidgetManager.updateAppWidget(appWidgetId, remoteView);
		}
		
		private String makeFullUrl (int gwidth, long gstime, int gperiod) {
			String fullURL = "";
			String chartGrapgID = charturl+graphID;
			String widthurl =  "&width="+ gwidth;
			String stimeurl = "";
			if (gstime!=0) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
				stimeurl = "&stime=" + dateFormat.format(gstime*1000);
			}
			String imageurlperiod = "&period=" + gperiod;
			fullURL = baseurl + chartGrapgID + widthurl + stimeurl + imageurlperiod;
			//Log.d(TAG,"URL: "+fullURL);
			return fullURL;
		}
	}
	
	class SetWidgetToLoading implements Runnable {
		int appWidgetId;

		public SetWidgetToLoading(int appWidgetId) {
			this.appWidgetId = appWidgetId;
		}

		public void run() {
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_graphs);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
	   		remoteView.setTextViewText(R.id.widget_graph_text03, "");
	   		remoteView.setTextViewText(R.id.widget_graph_text02, ctx.getResources().getString(R.string.loading));
	   		remoteView.setViewVisibility(R.id.widget_graph_refresh, View.GONE);
	   		remoteView.setViewVisibility(R.id.widget_graph_triggerlist, View.GONE);
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
			RemoteViews remoteView = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout_graphs);
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
	
	public String getCurrentTime() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date now = new Date();
		return dateFormat.format(now);
    }
	
	public boolean isCkeckInternet() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		return prefs.getBoolean("internet_check_fix",true);
	}

	public Server getSelectedServer() {
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		return db.selectServer(server_name);
	}
}
