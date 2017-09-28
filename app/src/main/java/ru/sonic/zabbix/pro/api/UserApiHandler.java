package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.Media;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.User;
import android.app.Activity;
import android.content.Context;

public class UserApiHandler extends ZabbixAPIHandler {

	public UserApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}
	
	protected UserApiHandler(Context context, Server zserver) {
		super(context,zserver);
	}
	
	@SuppressWarnings("unchecked")
	public List<User> get() throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("selectUsrgrps", "extend"); 
		params.put("select_usrgrps", "extend");
		ArrayList<User> ret = new ArrayList<User>();
		JSONArray jsonobjects = requestObject("user.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new User(obj));
			}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getInfo(String userid) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		JSONObject filter = new JSONObject();
		filter.put("userid", userid);
		params.put("filter", filter);
		params.put("output", "extend");
		params.put("selectUsrgrps", "extend");
		params.put("selectMedias", "extend");
		params.put("selectMediatypes", "extend");
		params.put("select_usrgrps", "extend");
		params.put("select_medias", "extend");
		params.put("select_mediatypes", "extend");
		return requestObject("user.get", params);
	}

	@SuppressWarnings("unchecked")
	public JSONArray create(User user) throws ZabbixAPIException {
		JSONObject ujson = new JSONObject();
		ujson.put("usrgrps", user.getGroups());
		ujson.put("alias",user.getalias());
		ujson.put("name",user.getName());
		ujson.put("surname",user.getsurname());
		ujson.put("passwd",user.getpasswd());
		ujson.put("url",user.geturl());
		ujson.put("autologin",user.getautologin());
		ujson.put("autologout",user.getautologout());
		ujson.put("lang",user.getlang());
		ujson.put("refresh",user.getrefresh());
		ujson.put("type",user.gettype());
		ujson.put("theme",user.gettheme());
		ujson.put("attempt_failed",user.getattempt_failed());
		ujson.put("attempt_ip",user.getattempt_ip());
		ujson.put("attempt_clock",user.getattempt_clock());
		ujson.put("rows_per_page",user.getrows_per_page());
		return requestObject("user.create", ujson);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray delete(User user) throws ZabbixAPIException {
		JSONObject json = new JSONObject();
		json.put("userid", user.getID()); 
		return requestObject("user.delete", json);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray media_delete(Media media) throws ZabbixAPIException {
		JSONArray ujson = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("mediaid", media.getID()); 
		ujson.add(json);
		return requestObject("user.deleteMedia", ujson);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray media_add(User user, Object medias) throws ZabbixAPIException {
		JSONArray ajson = new JSONArray();
			JSONObject json = new JSONObject();
			json.put("userid", user.getID()); 
			ajson.add(json);
		JSONObject bjson = new JSONObject();
		bjson.put("users", ajson);
		
		JSONArray ujson = new JSONArray();
		ujson.add(bjson);
		ujson.add(medias);
		return requestObject("user.addMedia", ujson);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray media_edit(User user, Object mediatypes) throws ZabbixAPIException {
		JSONArray ujson = new JSONArray();
			JSONObject json = new JSONObject();
			json.put("userid", user.getID()); 
		ujson.add(json);
		ujson.add(mediatypes);
		return requestObject("user.updateMedia", ujson);
	}

	@SuppressWarnings("unchecked")
	public JSONArray edit(User user) throws ZabbixAPIException {
		JSONObject ujson = new JSONObject();
		ujson.put("userid", user.getID());
		ujson.put("usrgrps", user.getGroups());
		ujson.put("alias",user.getalias());
		ujson.put("name",user.getName());
		ujson.put("surname",user.getsurname());
		ujson.put("passwd",user.getpasswd());
		ujson.put("url",user.geturl());
		ujson.put("autologin",user.getautologin());
		ujson.put("autologout",user.getautologout());
		ujson.put("lang",user.getlang());
		ujson.put("refresh",user.getrefresh());
		ujson.put("type",user.gettype());
		ujson.put("theme",user.gettheme());
		ujson.put("attempt_failed",user.getattempt_failed());
		ujson.put("attempt_ip",user.getattempt_ip());
		ujson.put("attempt_clock",user.getattempt_clock());
		ujson.put("rows_per_page",user.getrows_per_page());
		return requestObject("user.update", ujson);
	}
}