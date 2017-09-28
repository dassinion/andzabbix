package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.UserGroup;
import android.app.Activity;

@SuppressWarnings("unchecked")
public class UserGroupApiHandler extends ZabbixAPIHandler {

	public UserGroupApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	public List<UserGroup> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		//params.put("select_users", "extend");
		ArrayList<UserGroup> ret = new ArrayList<UserGroup>();
		JSONArray jsonobjects = requestObject("usergroup.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new UserGroup(obj));
			}
		return ret;
	}
	
	public JSONArray create(UserGroup group) throws ZabbixAPIException {
		//JSONArray grouparray = new JSONArray();
		//grouparray.add(group.getUsers());
		
		JSONObject ujson = new JSONObject();
		ujson.put("name",group.getName());
		//ujson.put("usrgrps", grouparray);

		return requestObject("usergroup.create", ujson);
	}
	
	public JSONArray delete(UserGroup obj) throws ZabbixAPIException {
		JSONArray array = new JSONArray();
		array.add(obj.getID());
		return requestObject("usergroup.delete", array);
	}

	public JSONArray edit(UserGroup obj) throws ZabbixAPIException {
		JSONObject ujson = new JSONObject();
		ujson.put("gui_access", obj.getGui_access());
		ujson.put("api_access", obj.getApi_access());
		ujson.put("debug_mode", obj.getDebug_mode());
		ujson.put("users_status", obj.getStatus());
		ujson.put("name",obj.getName());
		ujson.put("usrgrpid",obj.getID());

		return requestObject("usergroup.update", ujson);
	}
}