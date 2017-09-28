package ru.sonic.zabbix.pro.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Map;

/**
 * Object Representing a Zabbix Trigger
 * @author dassinion
 *
 */
public class History extends ZabbixObject {

	private static final long serialVersionUID = 1626415952936919799L;

	public History(Map<?, ?> m) {
		super(m);
	}

	public String getID() {
		return (String) get("id");
	}
	
	public String getItemID() {
		return (String) get("itemid");
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClockString());
		sb.append(" : ");
		sb.append(getValue());
		return sb.toString();
	}
	
	public String getClock() {
		return (String) get("clock");
	}
	
	public String getClockString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss"); 
		long clock = Integer.parseInt(getClock());
		Date dateClock = new Date(clock*1000);
		return dateFormat.format(dateClock).toString();
	}
	
	public String getValue() {
		return (String) get("value");
	}
}