package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Mediatype;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;
import android.content.Context;

public class MediatypeApiHandler extends ZabbixAPIHandler {
	public MediatypeApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected MediatypeApiHandler(Context context,Server zserver) {
		super(context,zserver);
	}
	
	@SuppressWarnings("unchecked")
	public List<Mediatype> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		ArrayList<Mediatype> ret = new ArrayList<Mediatype>();
		JSONArray jsonobjects = requestObject("mediatype.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Mediatype(obj));
			}
		return ret;
	}
}