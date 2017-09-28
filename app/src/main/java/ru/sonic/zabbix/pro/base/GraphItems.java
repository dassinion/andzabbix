package ru.sonic.zabbix.pro.base;

import java.util.Map;

/**
 * Object representing a Zabbix Item
 * @author dassinion
 *
 */
public class GraphItems extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2160624550743397912L;

	public GraphItems(Map<?, ?> m){
		super(m);
	}

	public String getDrawtype(){
		return (String)get("drawtype");
	}
	
	public String getColor(){
		return (String)get("color");
	}
	
	public String getItemID(){
		return (String)get("itemid");
	}
	
	public String getgitemid(){
		return (String)get("gitemid");
	}
	
	public String getyaxisside(){
		return (String)get("yaxisside");
	}
	
	public String gettype(){
		return (String)get("type");
	}
	
	public String getperiods_cnt(){
		return (String)get("periods_cnt");
	}
	
	@Override
	public String toString() {
		return (String)getItemID();
	}
	
}