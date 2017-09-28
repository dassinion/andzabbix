package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.HostInterface;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;
import android.content.Context;

public class HostInterfaceApiHandler extends ZabbixAPIHandler {

	private static final String TAG = "HostInterfaceApiHandler";

	public HostInterfaceApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected HostInterfaceApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	public List<HostInterface> get() throws ZabbixAPIException {
		JSONObject hinterface = new JSONObject();
		hinterface.put("output", "extend");
		hinterface.put("selectHosts", "extend");
		ArrayList<HostInterface> ret = new ArrayList<HostInterface>();
		JSONArray jsonobjects = requestObject("hostinterface.get", hinterface);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new HostInterface(obj));
			}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray create(HostInterface hostinterface) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("hostid",hostinterface.getHostID());
		params.put("dns",hostinterface.getDns());
		params.put("ip",hostinterface.getIP());
		params.put("main",hostinterface.getMain());
		params.put("port",hostinterface.getPort());
		params.put("type",hostinterface.getType());
		params.put("useip",hostinterface.getUseIP());
		return requestObject("hostinterface.create", params);
	}
	
	public JSONArray delete(HostInterface hostinterface) throws ZabbixAPIException {
		JSONArray array = new JSONArray();
		array.add(hostinterface.getID());
		return requestObject("hostinterface.delete", array);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray edit(HostInterface hostinterface) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("interfaceid", hostinterface.getID());
		params.put("hostid",hostinterface.getHostID());
		params.put("dns",hostinterface.getDns());
		params.put("ip",hostinterface.getIP());
		params.put("main",hostinterface.getMain());
		params.put("port",hostinterface.getPort());
		params.put("type",hostinterface.getType());
		params.put("useip",hostinterface.getUseIP());
		return requestObject("hostinterface.update", params);
	}
}