package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.DHost;
import ru.sonic.zabbix.pro.base.Event;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.Item;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.User;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class EventApiHandler extends ZabbixAPIHandler {
	private static final String TAG = "ItemApiHandler";

	public EventApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected EventApiHandler(Context context, Server zserver) {
		super(context, zserver);
	}
	
	/**
	 * 
	 * @param triggerId
	 * @return
	 * @throws ZabbixAPIException
	 */
	public List<Event> get(String triggerId) throws ZabbixAPIException{
		JSONObject params = new JSONObject();
		JSONArray triggerids=new JSONArray();
		if (!triggerId.equals("0")) { 
			triggerids.add(triggerId);
			params.put("objectids", triggerids);
		}
		params.put("output", "extend");
		params.put("select_triggers", "extend");
		params.put("select_acknowledges","extend");
		params.put("sortfield","eventid");
		params.put("sortorder","DESC");
		params.put("limit",60);
		JSONArray jsonobjects = requestObject("event.get", params);
		
		ArrayList<Event> ret = new ArrayList<Event>();
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Event(obj));
		}
		return ret;
	}
	
	/**
	 * Acknowledge trigger
	 * @param eventId
	 * @return
	 * @throws ZabbixAPIException
	 */
	public void ackEvent(String eventId, String message) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("eventids", eventId);
		params.put("message", message);
		requestObject("event.acknowledge", params);
	}
	
	public void delEvent(String eventId) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("params", eventId);
		requestObject("event.delete", params);
	}
}