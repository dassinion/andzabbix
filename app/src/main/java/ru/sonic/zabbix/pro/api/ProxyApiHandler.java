package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.DHost;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.Item;
import ru.sonic.zabbix.pro.base.Proxy;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.User;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class ProxyApiHandler extends ZabbixAPIHandler {

	private static final String TAG = "ItemApiHandler";

	public ProxyApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected ProxyApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	public List<Proxy> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		ArrayList<Proxy> ret = new ArrayList<Proxy>();
		JSONArray jsonobjects = requestObject("proxy.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Proxy(obj));
			}
		return ret;
	}
}