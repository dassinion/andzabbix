package ru.sonic.zabbix.pro.base;

import java.util.Map;

import org.json.simple.JSONArray;


/**
 * Object representing a Zabbix Hostgroup
 * @author dassinion
 *
 */
public class HostGroup extends ZabbixObject {
	private static final long serialVersionUID = -2102845859667327412L;
	
	public HostGroup(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("groupid");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public JSONArray getHosts(){
		return (JSONArray)get("hosts");
	}
}
