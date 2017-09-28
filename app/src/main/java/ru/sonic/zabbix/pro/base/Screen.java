package ru.sonic.zabbix.pro.base;

import java.util.Map;

import org.json.simple.JSONArray;
/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Screen extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Screen";

	public Screen(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("screenid");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String gethsize(){
		return (String)get("hsize");
	}
	
	public String getvsize(){
		return (String)get("vsize");
	}
	
	public JSONArray getScreenitems(){
		return (JSONArray)get("screenitems");
	}
}