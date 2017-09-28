package ru.sonic.zabbix.pro.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Maps;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

public class MapApiHandler extends ZabbixAPIHandler {
	private static final String TAG = "ItemApiHandler";

	public MapApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected MapApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	public List<Maps> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		//params.put("selectSelements", "extend");
		//params.put("selectLinks", "extend");
		ArrayList<Maps> ret = new ArrayList<Maps>();
		JSONArray jsonobjects = requestObject("map.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Maps(obj));
			}
		return ret;
	}
	
	public List<Maps> get(String sysmapids) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("selectSelements", "extend");
		params.put("selectLinks", "extend");
		params.put("sysmapids", sysmapids);
		ArrayList<Maps> ret = new ArrayList<Maps>();
		JSONArray jsonobjects = requestObject("map.get", params);
		//Log.d(TAG,"Response: "+jsonobjects);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Maps(obj));
			}
		return ret;
	}
	
	public Bitmap getMapImage(String imageurl) throws ZabbixAPIException, UnsupportedEncodingException, IOException {
		return getImage(imageurl);
	}
}