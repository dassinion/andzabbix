package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Alert;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;

public class AlertApiHandler extends ZabbixAPIHandler {
	private static final String TAG = "AlertApiHandler";

	public AlertApiHandler(Activity owner,Server zserver) {
		super(owner,zserver);
	}

	public List<Alert> get() throws ZabbixAPIException {
		JSONObject action = new JSONObject();
		action.put("output", "extend");
		ArrayList<Alert> ret = new ArrayList<Alert>();
		JSONArray jsonobjects = requestObject("alert.get", action);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Alert(obj));
			}
		return ret;
	}
}