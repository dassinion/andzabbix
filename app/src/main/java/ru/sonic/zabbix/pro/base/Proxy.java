package ru.sonic.zabbix.pro.base;

import java.util.Map;

/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Proxy extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Proxy";

	public Proxy(Map<?, ?> m){
		super(m);
	}
	
	public String gethost(){
		return (String)get("host");
	}
	
	public String getID(){
		return (String)get("proxyid");
	}
	
	@Override
	public String toString() {
		return gethost();
	}
	
	public String getstatus(){
		return (String)get("status");
	}
}