package ru.sonic.zabbix.pro.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.DHost;
import ru.sonic.zabbix.pro.base.Graph;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.Item;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.User;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class GraphApiHandler extends ZabbixAPIHandler {

	private static final String TAG = "ItemApiHandler";

	public GraphApiHandler(Activity owner,Server zserver) {
		super(owner,zserver);
	}
	
	public GraphApiHandler(Context context,Server zserver) {
		super(context,zserver);
	}
	/**
	 * get all graphs
	 * @return
	 * @throws ZabbixAPIException
	 */
	public List<Graph> get(String hostid) throws ZabbixAPIException{
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("hostids", hostid);
		JSONArray jsonobjects = requestObject("graph.get", params);
		
		ArrayList<Graph> ret = new ArrayList<Graph>();
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Graph(obj));
		}
		//Log.d(TAG, "Ret getGraphs: "+ret);
		return ret;
	}
	
	public Bitmap getGraphImage(String imageurl) throws ZabbixAPIException, UnsupportedEncodingException, IOException {
		return getImage(imageurl);
	}
	
}