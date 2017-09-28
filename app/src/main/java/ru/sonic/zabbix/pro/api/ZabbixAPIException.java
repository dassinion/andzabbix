package ru.sonic.zabbix.pro.api;

import org.json.JSONException;

/**
 * Exeption thrown by Code dealing with the Zabbix API (Login failures, network
 * errors etc)
 * 
 * @author dassinion
 * 
 */
public class ZabbixAPIException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1633513964299323021L;

	public ZabbixAPIException(String description) {
		super(description);
	}
}
