package ru.sonic.zabbix.pro.base;

import java.util.Map;

/**
 * Object Representing a Zabbix Media
 * @author dassinion
 *
 */
public class Media extends ZabbixObject {
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Media";

	public Media(Map<?, ?> m){
		super(m);
	}
	
	public String getUserid(){
		return (String)get("userid");
	}
	
	public String getID(){
		return (String)get("mediaid");
	}
	
	@Override
	public String toString() {
		return getSendTo();
	}
	
	public String getMediatypeid(){
		return (String)get("mediatypeid");
	}
	
	public String getSendTo(){
		return (String)get("sendto");
	}
	
	public String getActive(){
		return (String)get("active");
	}
	
	public String getPeriod(){
		return (String)get("period");
	}
	
	public String getSeverity(){
		return (String)get("severity");
	}
}