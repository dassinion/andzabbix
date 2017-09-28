package ru.sonic.zabbix.pro.widget;

import java.util.ArrayList;
import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.api.TriggerApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.Trigger;
import ru.sonic.zabbix.pro.database.DBAdapter;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ListWidgetRemotevierfactory implements RemoteViewsService.RemoteViewsFactory {
	private static final String TAG = "ListWidget_remotevierfactory";
	private Context ctx = null;
	private int appWidgetId;
	private List<Trigger> triggerList;
	
	public ListWidgetRemotevierfactory(Context context, Intent intent) {
		this.ctx = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
	}
	
	public void onCreate() {
		triggerList = new ArrayList<Trigger>();
	}

	public int getCount() {
		return triggerList.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public RemoteViews getLoadingView() {
		RemoteViews rv = null;
		return rv;
	}

	public RemoteViews getViewAt(int position) {
		RemoteViews row = new RemoteViews(ctx.getPackageName(),R.layout.widget_list_row);
		try {
			row.setTextViewText(R.id.widget_list_row_hostname, triggerList.get(position).getHost());
			row.setTextViewText(R.id.widget_list_row_desc, triggerList.get(position).getDescription().trim());
            row.setTextColor(R.id.widget_list_row_desc, triggerList.get(position).getSeverityColorInt());
			row.setImageViewResource(R.id.widget_list_row_image, triggerList.get(position).getActiveImg());
			
			Intent clickIntent = new Intent();
		    clickIntent.putExtra(ListWidgetProvider.ITEM_POSITION, position);
		    row.setOnClickFillInIntent(R.id.widget_list_row_desc, clickIntent);
		} catch (Exception e) {
			row.setTextViewText(R.id.widget_list_row_hostname, "Unknown");
			row.setTextViewText(R.id.widget_list_row_desc, "Error");
			row.setImageViewResource(R.id.widget_list_row_image, R.drawable.error_icon);
			
			Intent clickIntent = new Intent();
		    clickIntent.putExtra(ListWidgetProvider.ITEM_POSITION, position);
		    row.setOnClickFillInIntent(R.id.widget_list_row_desc, clickIntent);
		}
	    
		return (row);
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onDataSetChanged() {
		try {
			TriggerApiHandler zbx = new TriggerApiHandler(ctx.getApplicationContext(),getSelectedServer(appWidgetId));
			triggerList = zbx.getActiveTriggersCount(getCurrentServer(appWidgetId));
		} catch (ZabbixAPIException e) {
			triggerList = new ArrayList<Trigger>();
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
	}
	
	public int getViewTypeCount() {
		return 1;
	}
		
	public String getCurrentServer(int appWidgetId) {
		SharedPreferences config = ctx.getSharedPreferences(TxtTriggsWidgetControl.PREFS_NAME, 0);
		return config.getString(String.format(TxtTriggsWidgetControl.PREFS_WIDGET_SERVER, appWidgetId),"server");
	}

	public Server getSelectedServer(int appWidgetId) {
		DBAdapter db = new DBAdapter(ctx);
		String srvname = getCurrentServer(appWidgetId);
		db.open();
		return db.selectServer(srvname);
	}
}