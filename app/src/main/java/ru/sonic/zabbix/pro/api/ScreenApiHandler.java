package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Screen;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;
import android.content.Context;

@SuppressWarnings("unchecked")
public class ScreenApiHandler extends ZabbixAPIHandler {
	private static final String TAG = "ItemApiHandler";

	public ScreenApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected ScreenApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	public List<Screen> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		ArrayList<Screen> ret = new ArrayList<Screen>();
		JSONArray jsonobjects = requestObject("screen.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Screen(obj));
			}
		return ret;
	}
	
	public List<Screen> getScreenItems(String screenids) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("selectScreenItems", "extend");
		params.put("screenids", screenids);
		ArrayList<Screen> ret = new ArrayList<Screen>();
		JSONArray jsonobjects = requestObject("screen.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Screen(obj));
			}
		return ret;
	}
	
	public JSONArray create(Screen screen) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("name",screen.getName());
		params.put("hsize",screen.gethsize());
		params.put("vsize",screen.getvsize());
		params.put("screenitems",screen.getScreenitems());
		return requestObject("screen.create", params);
	}
	
	public JSONArray delete(Screen screen) throws ZabbixAPIException {
		JSONArray array = new JSONArray();
		array.add(screen.getID());
		return requestObject("screen.delete", array);
	}
	
	public JSONArray edit(Screen screen) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("screenid",screen.getID());
		params.put("name",screen.getName());
		params.put("hsize",screen.gethsize());
		params.put("vsize",screen.getvsize());
		//params.put("screenitems",screen.getScreenitems());
		return requestObject("screen.create", params);
	}
}