package ru.sonic.zabbix.pro.base;

import java.util.Map;

import org.json.simple.JSONArray;

/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Application extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Application";

	public Application(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("applicationid");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public JSONArray getHosts(){
		return (JSONArray)get("hosts");
	}
	
	public String getTemplateid(){
		return (String)get("templateid");
	}
	
	public String getHost(){
		return (String)get("host");
	}
}