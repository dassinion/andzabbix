package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.HostGroup;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;
import android.content.Context;

public class HostGroupApiHandler extends ZabbixAPIHandler {

	public HostGroupApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected HostGroupApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	/**
	 * get all hostgroups
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public List<HostGroup> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("selectHosts", "extend");
		params.put("select_hosts", "extend");
		JSONArray jsonobjects = requestObject("hostgroup.get", params);
		
		ArrayList<HostGroup> ret = new ArrayList<HostGroup>();
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new HostGroup(obj));
		}
		//Log.d(TAG, "getHostGroups result: "+ret);
		return ret;
	}
}