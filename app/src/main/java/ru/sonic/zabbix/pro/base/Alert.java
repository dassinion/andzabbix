package ru.sonic.zabbix.pro.base;

import java.util.Map;


/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Alert extends ZabbixObject {
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Alert";

	public Alert(Map<?, ?> m){
		super(m);
	}
	
	public String getID(){
		return (String)get("alertid");
	}
	
	@Override
	public String toString() {
		return getID();
	}
}