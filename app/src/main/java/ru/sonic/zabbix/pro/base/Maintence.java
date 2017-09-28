package ru.sonic.zabbix.pro.base;

import java.util.Map;

import org.json.simple.JSONArray;


/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Maintence extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Maintence";

	public Maintence(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("maintenanceid");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getmaintenance_type(){
		return (String)get("maintenance_type");
	}
	
	public String getdescription(){
		return (String)get("description");
	}
	
	public String getactive_since(){
		return (String)get("active_since");
	}
	
	public String getactive_till(){
		return (String)get("active_till");
	}

	public JSONArray getGroups() {
		return (JSONArray)get("groups");
	}
	
	public JSONArray getTimeperiods() {
		return (JSONArray)get("timeperiods");
	}
}