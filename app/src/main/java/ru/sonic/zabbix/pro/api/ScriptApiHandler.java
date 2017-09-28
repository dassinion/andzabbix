package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Script;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;
import android.content.Context;

@SuppressWarnings("unchecked")
public class ScriptApiHandler extends ZabbixAPIHandler {
	private static final String TAG = "ItemApiHandler";

	public ScriptApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected ScriptApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	public List<Script> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		ArrayList<Script> ret = new ArrayList<Script>();
		JSONArray jsonobjects = requestObject("script.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Script(obj));
			}
		return ret;
	}
	
	public JSONArray create(Script script) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("name",script.getName());
		params.put("command",script.getCommand());
		params.put("host_access",script.getHost_access());
		params.put("usrgrpid",script.getusrgrpid());
		params.put("groupid",script.getgroupid());
		params.put("type",script.getType());
		return requestObject("script.create", params);
	}
	
	public JSONArray delete(Script script) throws ZabbixAPIException {
		JSONArray array = new JSONArray();
		array.add(script.getID());
		return requestObject("script.delete", array);
	}
	
	public JSONArray edit(Script script) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("scriptid",script.getID());
		params.put("name",script.getName());
		params.put("command",script.getCommand());
		params.put("host_access",script.getHost_access());
		params.put("usrgrpid",script.getusrgrpid());
		params.put("groupid",script.getgroupid());
		return requestObject("script.update", params);
	}
	
	public JSONArray execute(Script script, String hostid) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("scriptid",script.getID());
		params.put("hostid",hostid);
		return requestObject("script.execute", params);
	}
}