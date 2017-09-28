package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.Template;

import android.app.Activity;
import android.content.Context;

public class TemplateApiHandler extends ZabbixAPIHandler {
	@SuppressWarnings("unused")
	private static final String TAG = "TemplateApiHandler";

	public TemplateApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected TemplateApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	@SuppressWarnings("unchecked")
	public List<Template> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend"); 
		ArrayList<Template> ret = new ArrayList<Template>();
		JSONArray jsonobjects = requestObject("template.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Template(obj));
			}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray create(Template template) throws ZabbixAPIException {
		JSONArray grouparray = new JSONArray();
		grouparray.add(template.getGroups());
		
		JSONArray templatearray = new JSONArray();
		templatearray.add(template.getTemplates());
		
		JSONObject ujson = new JSONObject();
		if (template.getGroups().size()>0)
			ujson.put("groups", grouparray);
		if (template.getTemplates().size()>0)
			ujson.put("templates", templatearray);
		ujson.put("host",template.getHost());
		return requestObject("template.create", ujson);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray delete(Template template) throws ZabbixAPIException {
		JSONObject json = new JSONObject();
		json.put("templateid", template.getID()); 
		return requestObject("template.delete", json);
	}
}