package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.Trigger;
import ru.sonic.zabbix.pro.database.DBAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class TriggerApiHandler extends ZabbixAPIHandler {
	private static final String TAG = "TriggerApiHandler";
	private static final int WithLastEventUnacknowledged = 1;
	private static final int WithUnacknowledgedEvents = 2;
	private static final int WithAcknowledgedEvents = 3;
	private static final int ALL = 4;
	
	public TriggerApiHandler(Activity owner,Server zserver) {
		super(owner,zserver);
	}
	public TriggerApiHandler(Context context,Server zserver) {
		super(context,zserver);
	}

	/**
	 * get all active triggers
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public List<Trigger> get(int eventack) throws ZabbixAPIException {
        if (isDebug) Log.d(TAG, "Trigger list get started");
		List<Trigger> ret = new ArrayList<Trigger>();
		int trigger_check_type = check_type();
		JSONObject jtrigger = new JSONObject();
		
		if (get_trigger_filter() == 3) {
			jtrigger.put("monitored","1");
		} else if (get_trigger_filter() == 2) {
			jtrigger.put("active","1");
		} else if (get_trigger_filter() == 0) {
			trigger_check_type = 0;
		}
		
		if (trigger_check_type == 1) {
			jtrigger.put("only_true", "yes");
		} else if (trigger_check_type == 2) {
			float time = getTimeActiveTriggersCheck();
			Date now = new Date();
			String From = (now.getTime()/1000-time*60)+"";
			String to = (now.getTime()/1000)+"";
			jtrigger.put("lastChangeSince", From);
			jtrigger.put("lastChangeTill", to);
		} else if (trigger_check_type == 3) {
			Hashtable<Object, Object> filter = new Hashtable<Object, Object>();
				filter.put("status", "0");
				filter.put("value", "1");
				jtrigger.put("filter", filter);
		}

		if (!old_zbx_fix())
			jtrigger.put("expandDescription","yes");
		if (isHideDependenceTrigger())
			jtrigger.put("skipDependent","1");
		jtrigger.put("output", "extend");
		jtrigger.put("expandData","hostid");
		jtrigger.put("selectHosts","extend");
		jtrigger.put("sortfield","lastchange");
		jtrigger.put("sortorder","DESC");
		jtrigger.put("limit","1000");
		jtrigger.put("min_severity",filter_min_severity());
		switch (eventack) {
		case WithLastEventUnacknowledged:
			jtrigger.put("withLastEventUnacknowledged",1);
			break;
		case WithUnacknowledgedEvents:
			jtrigger.put("withUnacknowledgedEvents",1);
			break;
		case WithAcknowledgedEvents:
			jtrigger.put("withAcknowledgedEvents",1);
			break;
		case ALL:
			break;
		}

		//JSONArray jsonobjects = requestObject("trigger.get", jtrigger);
		JSONArray jsonobjects = new JSONArray();
		try {
			if (!filter_maintenance()) {
				JSONArray alljsonobjects = new JSONArray();
				alljsonobjects.addAll(requestObject("trigger.get", jtrigger));
				JSONArray maintenancejsonobjects = new JSONArray();
				try {
					jtrigger.put("maintenance", true);
					maintenancejsonobjects.addAll(requestObject("trigger.get", jtrigger));
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (Iterator<JSONObject> it = alljsonobjects.iterator(); it.hasNext(); ) {
					boolean maintencedtrigger = false;
					JSONObject trigObj = it.next();
					for (Iterator<JSONObject> mit = maintenancejsonobjects.iterator(); mit.hasNext(); ) {
						JSONObject trigMObj = mit.next();
						//Log.d(TAG,"Maintenanced trigger: "+trigMObj.get("triggerid"));
						if (trigObj.get("triggerid").equals(trigMObj.get("triggerid")))
							maintencedtrigger = true;
					}
					if (!maintencedtrigger) {
						jsonobjects.add(trigObj);
					} else {
						trigObj.put("value", "41");
						//if (!filter_maintenance())
							jsonobjects.add(trigObj);
					}
				}
			} else {
				jsonobjects = requestObject("trigger.get", jtrigger);
			}
		} catch (Exception e) {
				e.printStackTrace();
				//jsonobjects = requestObject("trigger.get", jtrigger);
		}

		if (jsonobjects==null || jsonobjects.size()==0) {
			return ret;
		}
		String colors = "";
		if (use_modern_colors()) {
			colors = "modern";
		} else {
			colors = "default";
		}

		long timecorection = getTimeCorrection();
		
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();

			if (obj.containsKey("message") && obj.containsKey("data")) {
				String errmsg = obj.get("message").toString();
				String errdata = obj.get("data").toString();
				throw new ZabbixAPIException(errmsg+"\n"+errdata);
			}

			Trigger trigger = new Trigger(obj);
			trigger.setAck(eventack);
			trigger.colors = colors;
			trigger.timecorrection = timecorection;
			trigger.setServerID(getCurrentServername());
			try {
				if (trigger.get("status").toString().equals("0"))     // enabled
					ret.add(trigger);
				else if (showdisabled())
					ret.add(trigger);
			} catch (Exception e) {
				ret.add(trigger);
			}
		}
		return ret;
	}
	
	/**
	 * get all active triggers count
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public List<Trigger> getActiveTriggersCount(String server_name) throws ZabbixAPIException {
		List<Trigger> ret = new ArrayList<Trigger>();
		try {
			JSONObject params = new JSONObject();
			int trigger_check_type = check_type();
			params.put("output", "extend");
			params.put("monitored", "1");
            params.put("sortfield","lastchange");
            params.put("sortorder","DESC");
			params.put("selectHosts","extend");
            params.put("limit","1000");
			if (trigger_check_type == 1) {
				params.put("only_true", "yes");
			} else if (trigger_check_type == 2) {
				float time = getTimeActiveTriggersCheck();
				Date now = new Date();
				String From = (now.getTime()/1000-time*60)+"";
				String to = (now.getTime()/1000)+"";
				params.put("lastChangeSince", From);
				params.put("lastChangeTill", to);
			} else if (trigger_check_type == 3) {
				Hashtable<Object, Object> filter = new Hashtable<Object, Object>();
				filter.put("status", "0");
				filter.put("value", "1");
				params.put("filter", filter);
			}
			params.put("expandData","hostid");
			if (!old_zbx_fix())
				params.put("expandDescription","yes");
			if (isHideDependenceTrigger())
				params.put("skipDependent","1");
			
			JSONArray jsonobjects = new JSONArray();
			try {
					JSONArray alljsonobjects = new JSONArray();
					alljsonobjects.addAll(requestObjectCount("trigger.get", params));
					
					JSONArray maintenancejsonobjects = new JSONArray();
					try {
						params.put("maintenance", true);
						maintenancejsonobjects.addAll(requestObjectCount("trigger.get", params));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					for (Iterator<JSONObject> it = alljsonobjects.iterator(); it.hasNext();) {
						boolean maintencedtrigger = false;
						JSONObject trigObj = it.next();
						for (Iterator<JSONObject> mit = maintenancejsonobjects.iterator(); mit.hasNext();) {
							JSONObject trigMObj = mit.next();
							//Log.d(TAG,"Maintenanced trigger: "+trigMObj.get("triggerid"));
							if (trigObj.get("triggerid").equals(trigMObj.get("triggerid")))
								maintencedtrigger=true;
						}
						if (!maintencedtrigger) {
							jsonobjects.add(trigObj);
						} else {
							trigObj.put("value", "41");
							jsonobjects.add(trigObj);
						}
					}
					
			} catch (Exception e) {
					e.printStackTrace();
					jsonobjects = requestObjectCount("trigger.get", params);
			}
			//} else
			//	jsonobjects = requestObjectCount(server_name,"trigger.get", params);
			
			String colors = "";
			if (use_modern_colors()) {
				colors = "modern";
			} else {
				colors = "default";
			}
			long timecorection = getTimeCorrection();
			
			for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
				JSONObject obj = it.next();
				Trigger trigger = new Trigger(obj);
				trigger.colors = colors;
				trigger.timecorrection = timecorection;
				trigger.setServerID(server_name);
				ret.add(trigger);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject obj = new JSONObject();
			obj.put("description", e.getMessage());
			obj.put("value", "21");
			ret.add(new Trigger(obj));
		}
		return ret;
	} 
	
	/**
	 * get all active triggers count for service
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public List<Trigger> getActiveTriggersService(float interval) throws ZabbixAPIException {
		DBAdapter db = new DBAdapter(ctx);
  		db.open();
  		List<String> server = db.selectServerNames();
	  	db.close();
		Date now = new Date();
		String Since = (now.getTime()/1000-(interval*60)+3)+"";
		String Till = (now.getTime()/1000)+"";
	  	
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("monitored", "1");
		params.put("lastChangeSince", Since);
		params.put("lastChangeTill", Till);
		params.put("expandData","hostid");
		params.put("selectHosts","extend");
		if (!old_zbx_fix())
			params.put("expandDescription","yes");
		Hashtable<Object, Object> filter = new Hashtable<Object, Object>();
			filter.put("status", "0");
			filter.put("value", "1");
		params.put("filter", filter);
        params.put("sortfield","lastchange");
        params.put("sortorder","DESC");
        params.put("limit","1000");
		
		List<Trigger> ret = new ArrayList<Trigger>();
		for (String server_nm: server) {
			//Log.d(TAG, "Request: Active Triggers count server: "+string);
			List<Trigger> triggers = new ArrayList<Trigger>();
			try {
				JSONArray jsonobjects = new JSONArray();
				if (filter_service_maintnance()) {
					JSONArray alljsonobjects = new JSONArray();
					JSONArray maintenancejsonobjects = new JSONArray();
					try {
						alljsonobjects.addAll(requestObjectCount( "trigger.get", params));
						if (isDebug) Log.d(TAG, "Request: Active Triggers count alljsonobjects size: " + alljsonobjects.size());
						params.put("maintenance", true);
						maintenancejsonobjects.addAll(requestObjectCount( "trigger.get", params));
						if (isDebug) Log.d(TAG, "Request: Active Triggers count maintenancejsonobjects size: " + maintenancejsonobjects.size());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					for (Iterator<JSONObject> it = alljsonobjects.iterator(); it.hasNext();) {
						boolean maintencedtrigger = false;
						JSONObject trigObj = it.next();
						for (Iterator<JSONObject> mit = maintenancejsonobjects.iterator(); mit.hasNext();) {
							JSONObject trigMObj = mit.next();
							if (isDebug) Log.d(TAG,"Maintenanced trigger: "+trigMObj.get("triggerid"));
							if (trigObj.get("triggerid").equals(trigMObj.get("triggerid")))
								maintencedtrigger=true;
						}
						if (!maintencedtrigger) {
							if (isDebug) Log.d(TAG,"Add trigger: "+trigObj.get("triggerid"));
							jsonobjects.add(trigObj);
						}
					}
				} else {
					jsonobjects = requestObjectCount("trigger.get", params);
				}
				
				for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
					JSONObject obj = it.next();
					Trigger trigobj = new Trigger(obj);
					trigobj.setServerID(server_nm);
					triggers.add(trigobj);
				}
			} catch (ZabbixAPIException e) {
				e.printStackTrace();
			}
			ret.addAll(triggers);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray create(Trigger obj) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("status", obj.getStatus());
		params.put("description", obj.getDescription());
		params.put("url", obj.getUrl());
		//params.put("value", obj.getValue()));
		params.put("priority", obj.getPriority());
		//params.put("dep_level", obj.getDescription())));
		params.put("comments", obj.getComments());
		params.put("error",obj.getError());
		//params.put("templateid",obj.gettype());
		params.put("type",obj.gettype());
		params.put("expression",obj.getExpression());
		return requestObject("trigger.create", params);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray edit(Trigger obj) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("triggerid", obj.getID());
		params.put("status", obj.getStatus());
		params.put("description", obj.getDescription());
		params.put("url", obj.getUrl());
		//params.put("value", obj.getValue()));
		params.put("priority", obj.getPriority());
		//params.put("dep_level", obj.getDescription())));
		params.put("comments", obj.getComments());
		params.put("error",obj.getError());
		//params.put("templateid",obj.gettype());
		params.put("type",obj.gettype());

		return requestObject("trigger.update", params);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray delete(Trigger obj) throws ZabbixAPIException {
		JSONArray array = new JSONArray();
		array.add(obj.getID());
		return requestObject("trigger.delete", array);
	}
	
	public int check_type(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		int type = Integer.parseInt(prefs.getString("check_type","1"));
        return type;
	}
	
	public int get_trigger_filter(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		int filter_type = Integer.parseInt(prefs.getString("trigger_filter","3"));
        return filter_type;
	}
	
	public boolean isHideDependenceTrigger(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean("hide_dependences",true);
	}
	
	public boolean old_zbx_fix(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean("old_zbx_fix",false);
	}
	
	public boolean filter_service_maintnance(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean("service_maintemce",false);
	}
	
	public boolean filter_maintenance(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean("triggercheck_maintenance",false);
	}
	
	public String filter_min_severity(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString("min_severity","0");
	}
	
	public boolean timecorr(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean("use_timecorrection",false);
	}
	
	/**
	 * get correction timezone
	 * @return
	 * @throws ZabbixAPIException
	 */
	protected long getTimeCorrection() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		String tc = "0";
		if (prefs.getBoolean("use_timecorrection",false))
			tc = prefs.getString("time_correction","0");
        return Integer.parseInt(tc);
	}
	
	public boolean showdisabled(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean("showdisabled",false);
	}
	
	public float getTimeActiveTriggersCheck(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return Float.parseFloat(prefs.getString("activetriggertime","0"));
	}
	
	public boolean use_modern_colors(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean("use_modern_colors",false);
	}
}