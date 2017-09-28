package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.DHost;
import ru.sonic.zabbix.pro.base.History;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.Item;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.User;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class HistoryApiHandler extends ZabbixAPIHandler {

	private static final String TAG = "ItemApiHandler";

	public HistoryApiHandler(Activity owner, Server zserver) {
		super(owner, zserver);
	}
	
	protected HistoryApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	/**
	 * get history for item
	 * @param itemids
	 * @return
	 * @throws ZabbixAPIException
	 */
	public List<History> get(String itemids, String valuetype) throws ZabbixAPIException{
		JSONObject params = new JSONObject();
		JSONArray itemidArray=new JSONArray();
		itemidArray.add(itemids);
		if (valuetype==null) throw new ZabbixAPIException("Can't get history. Unknown item type.");
		params.put("history", valuetype);
		params.put("itemids", itemidArray);
		params.put("sortfield", "clock");
		params.put("sortorder", "DESC");
		params.put("output", "extend");
		params.put("limit", 100);
		
		JSONArray jsonobjects = requestObject("history.get", params);

		ArrayList<History> ret = new ArrayList<History>();
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new History(obj));
		}
		return ret;
	}
}