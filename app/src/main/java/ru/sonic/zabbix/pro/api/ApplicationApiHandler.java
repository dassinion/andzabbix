package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Application;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;

public class ApplicationApiHandler extends ZabbixAPIHandler {
	private static final String TAG = "ApplicationApiHandler";
	public ApplicationApiHandler(Activity owner, Server zserver) {
		super(owner, zserver);
	}
	
	public List<Application> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		ArrayList<Application> ret = new ArrayList<Application>();
		JSONArray jsonobjects = requestObject("application.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Application(obj));
			}
		return ret;
	}
	
	public JSONArray delete(Application obj) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("params", obj.getID());
		return requestObject("application.delete", params);
	}
	
	public JSONArray create(Application appl) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("name",appl.getName());
		return requestObject("application.create", params);
	}
}