package ru.sonic.zabbix.pro.base;

import java.util.Map;

import org.json.simple.JSONArray;

/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Template extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Template";

	public Template(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return getHost();
	}
	
	public String getHost(){
		return (String)get("host");
	}
	
	public String getID(){
		return (String)get("templateid");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getHostid(){
		return (String)get("hostid");
	}
	
	public String getproxy_hostid(){
		return (String)get("proxy_hostid");
	}
	
	public String getdns(){
		return (String)get("dns");
	}
	
	public String getuseip(){
		return (String)get("useip");
	}
	
	public String getip(){
		return (String)get("ip");
	}
	
	public String getport(){
		return (String)get("port");
	}
	
	public String getdisable_until(){
		return (String)get("disable_until");
	}
	
	public String geterror(){
		return (String)get("error");
	}
	
	public String getavailable(){
		return (String)get("available");
	}
	
	public JSONArray getGroups(){
		return (JSONArray)get("groups");
	}
	
	public JSONArray getTemplates(){
		return (JSONArray)get("templates");
	}
}