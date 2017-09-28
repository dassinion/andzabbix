package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Action;
import ru.sonic.zabbix.pro.base.Maintence;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;
import android.content.Context;

public class MaintenceApiHandler extends ZabbixAPIHandler {

	private static final String TAG = "MaintenceApiHandler";

	public MaintenceApiHandler(Activity owner,Server zserver) {
		super(owner,zserver);
	}
	
	public MaintenceApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	public List<Maintence> get() throws ZabbixAPIException {
		JSONObject action = new JSONObject();
		action.put("output", "extend");
		action.put("selectTimeperiods", "extend");
		action.put("selectGroups", "extend");
		ArrayList<Maintence> ret = new ArrayList<Maintence>();
		JSONArray jsonobjects = requestObject("maintenance.get", action);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Maintence(obj));
			}
		return ret;
	}
	
	/*
	public JSONArray create(Action action) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("name",action.getName());
		params.put("eventsource",action.getEventsource());
		params.put("status",action.getStatus());
		params.put("esc_period",action.getEsc_period());
		params.put("def_shortdata",action.getDef_shortdata());
		params.put("def_longdata",action.getDef_longdata());
		return requestObject("action.create", params);
	}
	
	public JSONArray delete(Action action) throws ZabbixAPIException {
		JSONArray array = new JSONArray();
		array.add(action.getID());
		return requestObject("action.delete", array);
	}
	
	public JSONArray edit(Action action) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("actionid", action.getID());
		params.put("eventsource",action.getEventsource());
		params.put("name",action.getName());
		params.put("status",action.getStatus());
		params.put("esc_period",action.getEsc_period());
		params.put("def_shortdata",action.getDef_shortdata());
		params.put("def_longdata",action.getDef_longdata());
		return requestObject("action.update", params);
	}
	*/
}