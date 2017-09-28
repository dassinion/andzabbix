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
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.User;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class ItemApiHandler extends ZabbixAPIHandler {

	private static final String TAG = "ItemApiHandler";

	public ItemApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected ItemApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	/**
	 * get all items for a given host
	 * @param hostid
	 * @return
	 * @throws ZabbixAPIException
	 */
	public List<Item> get(String hostid) throws ZabbixAPIException{
		JSONObject item = new JSONObject();
		//JSONArray hostids=new JSONArray();
		//Log.d(TAG,"getItems: Host id: "+hostid);
		//hostids.add(hostid);
		item.put("hostids", hostid);
		item.put("output", "extend");
		JSONArray jsonobjects = requestObject("item.get", item);
		
		ArrayList<Item> ret = new ArrayList<Item>();
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Item(obj));
		}
		return ret;
	}
}