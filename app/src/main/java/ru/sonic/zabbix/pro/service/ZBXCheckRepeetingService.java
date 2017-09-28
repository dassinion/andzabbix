package ru.sonic.zabbix.pro.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.R.color;
import ru.sonic.zabbix.pro.activities.ActiveTrigerActivity;
import ru.sonic.zabbix.pro.api.TriggerApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Maintence;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.Trigger;
import ru.sonic.zabbix.pro.database.DBAdapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ZBXCheckRepeetingService extends BroadcastReceiver {
	private static final int MSG_DATA_RETRIEVED = 0;
	private static final int MSG_ERROR = 1;
	public final static String TAG = "ZBXCheckRepeetingSrvc";
	Context context;
	DBAdapter db;
	//ArrayList<Maintence> mlist;

	@Override
	public void onReceive(Context ctsx, Intent intent) {
		Log.i(TAG,"onReceive: "+intent);
	      this.context = ctsx;
	      if ((isCkeckInternet() && checkInternet()>0) || !isCkeckInternet()) {
			  db = new DBAdapter(context);
			  db.open();
	    	  new Thread(new Runnable() {
		          public void run() {
						try {
							Message msg=new Message();
							msg.obj = get_active_triggers_for_servers();
							msg.arg1=MSG_DATA_RETRIEVED;
							handler.sendMessage(msg);
					} catch (Exception e) {
							Log.e(TAG, "run() Error: " + e);
							Message msg=new Message();
							msg.arg1=MSG_ERROR;
							msg.obj=e;
							handler.sendMessage(msg);
							return;
					}
		          }
		      }).start();
	      }
	}

	public int checkInternet() {
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(0).getState();
		State wifi = conMan.getNetworkInfo(1).getState();
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return -1;
		}
		if (info.isRoaming()) {
	        return -2;
	    }
		if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
		    //mobile
			return 1;
		} else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
		    //wifi
			return 2;
		}
		return 0;
	}
		 
	@SuppressWarnings("deprecation")
	public void notify(String info, String hostname, String age, String severity, String zservername) {
		int NOTIFY_ID = Integer.parseInt(age);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean service_alert_sound = prefs.getBoolean("service_alert_sound",false);
		boolean service_alert_sound_custom_melody = prefs.getBoolean("service_alert_sound_custom_melody",false);
		String service_alert_sound_melody = prefs.getString("service_alert_sound_melody","");
		Log.i(TAG, "service_alert_sound_melody: " + service_alert_sound_melody);
		Uri melodyUri = null;
		if (service_alert_sound_custom_melody)
			if (service_alert_sound_melody.equals("")) {
				melodyUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			} else {
				melodyUri = Uri.parse(service_alert_sound_melody);
			}
		else
			melodyUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		boolean service_alert_led = prefs.getBoolean("service_alert_led",false);
		boolean service_alert_vibration = prefs.getBoolean("service_alert_vibration",false);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(context,ActiveTrigerActivity.class);
		//Notification notification = new Notification(android.R.drawable.ic_menu_info_details, info, System.currentTimeMillis());
        PendingIntent ac = PendingIntent.getActivity(context, 0, i, 0);
		NotificationCompat.Builder mNotificationBuilder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.attention)
						.setContentTitle(severity+" alert on "+zservername)
						.setStyle(new NotificationCompat.BigTextStyle().bigText("Host: "+hostname+"\n"+info))
						.setAutoCancel(true)
						.setContentText("Host: "+hostname+"\n"+info)
						.setContentIntent(ac);

		if (service_alert_sound) {
			if (service_alert_sound_custom_melody) {
				mNotificationBuilder.setSound(melodyUri);
				//notification.sound = melodyUri;
			} else {
				//notification.defaults |= Notification.DEFAULT_SOUND;
				//notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				mNotificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
				mNotificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			}
		}
		if (service_alert_vibration) {
			//notification.defaults |= Notification.DEFAULT_VIBRATE;
			mNotificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
		}

		Notification notification = mNotificationBuilder.build();
		notification.ledARGB = context.getResources().getColor(color.orange);
		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		if (service_alert_led)
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        //notification.setLatestEventInfo(context, severity +" alert on host: "+hostname, info,ac);
		mNotificationManager.notify(NOTIFY_ID, notification);
	}

	protected void parse_trigger_list(ArrayList<Trigger> obj) {
		for (Trigger trigger : obj) {
			if (trigger.getSeverity()>=getMinAlertSerenivity())
			notify(trigger.getDescription(), trigger.getHost(), trigger.getLastchangeStamp(), trigger.getSeverityStringt(), trigger.getServerID());
		}
	}
		
	public Integer getInterval(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    Integer interval = Integer.parseInt(prefs.getString("service_update_interval","30"));
	    return interval;
	}
	
	public Integer getMinAlertSerenivity(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	    Integer interval = Integer.parseInt(prefs.getString("alert_severity","1"));
	    return interval;
	}

	public boolean isCkeckInternet() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean("internet_check_fix",true);
	}

	protected ArrayList<Trigger> get_active_triggers_for_servers() {
		List<Server> srvlist = getZabbixServers();
		ArrayList<Trigger> triggerlist = new ArrayList<Trigger>();
		int n;
		if (srvlist!=null) {
			for (n = 0; n < srvlist.size(); n++) {
				Server srvname = srvlist.get(n);
				try {
					TriggerApiHandler zbx = new TriggerApiHandler(context.getApplicationContext(), srvname);
					triggerlist.addAll(zbx.getActiveTriggersService(getInterval()));
				} catch (ZabbixAPIException e) {
					e.printStackTrace();
					//return null;
				}
			}
		}
		return triggerlist;
	}

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
	    public void handleMessage(Message msg) { 
			switch (msg.arg1) {
	 		case  MSG_ERROR:
	 			Log.e(TAG,"ERROR getting data: "+msg.obj);
	 			break;
	 		case MSG_DATA_RETRIEVED:
				Log.d(TAG,"Do check from service!"+msg.obj);
				if (msg.obj != null)
				parse_trigger_list((ArrayList<Trigger>) msg.obj);
	 		}
		}
	};

	//@Override
	public IBinder onBind(Intent intent) {
	return null;
	}

	/**
	 * @return array of Zabbix servers
	 */
	public List<Server> getZabbixServers() {
		if (db.open()!=null) {
			List<Server> zabbixServers = db.getAllServers();
			//if (isDebug) Log.d(TAG, "getStringPref return value: ");
			return zabbixServers;
		} else {
			//throw new ZabbixAPIException(R.string.unknown_database_error + "");
			return null;
		}
	}
}