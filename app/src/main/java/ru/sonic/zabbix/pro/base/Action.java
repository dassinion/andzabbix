package ru.sonic.zabbix.pro.base;

import java.util.Map;

public class Action extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Action";

	public Action(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("actionid");
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getEventsource(){
		return (String)get("eventsource");
	}
	
	public String getEvaltype(){
		return (String)get("evaltype");
	}
	
	public String getEsc_period(){
		return (String)get("esc_period");
	}
	
	public String getDef_shortdata(){
		return (String)get("def_shortdata");
	}
	
	public String getDef_longdata(){
		return (String)get("def_longdata");
	}
	
	public String getRecovery_msg(){
		return (String)get("recovery_msg");
	}
}