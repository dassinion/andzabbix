package ru.sonic.zabbix.pro.base;

import java.util.Map;

import org.json.simple.JSONArray;


/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Maps extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Script";

	public Maps(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("sysmapid");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getwidth(){
		return (String)get("width");
	}
	
	public String getheight(){
		return (String)get("height");
	}
	
	public String getbackgroundid(){
		return (String)get("backgroundid");
	}
	
	public String getlabel_type(){
		return (String)get("label_type");
	}
	
	public String gethighlight(){
		return (String)get("highlight");
	}
	
	public String getexpandproblem(){
		return (String)get("expandproblem");
	}
	
	public String getmarkelements(){
		return (String)get("markelements");
	}
	
	public String getshow_unack(){
		return (String)get("show_unack");
	}
	
	public JSONArray getselements(){
		return (JSONArray)get("selements");
	}
}