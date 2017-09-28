package ru.sonic.zabbix.pro.base;

import java.util.Map;

/**
 * Object Representing a Zabbix Interface
 * @author dassinion
 *
 */
public class HostInterface extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "ZIntrface";

	public HostInterface(Map<?, ?> m){
		super(m);
	}
	
	@Override
	public String toString() {
		if (isUseIp())
			return getTypeString()+":	"+getIP()+":"+getPort();
		else
			return getTypeString()+":	"+getDns()+":"+getPort();
	}
	
	public String getID(){
		return (String)get("interfaceid");
	}
	
	public String getHostID(){
		return (String)get("hostid");
	}
	
	public String getMain(){
		if ((String)get("main")!=null)
			return (String)get("main");
		else
			return "1";
	}
	
	public boolean getbMain(){
		return getMain().equals("1")?true:false;
	}
	
	public String getType(){
		if ((String)get("type")!=null)
			return (String)get("type");
		else
			return "1";
	}
	
	public String getTypeString(){
		String type = "";
		switch (Integer.parseInt(getType())) {
			case 1:
				type = "Agent";
				break;
			case 2:
				type = "SNMP";
				break;
			case 3:
				type = "IPMI";
				break;
			case 4:
				type = "JMX";
				break;
			default:
				type =  "Unknown";
		}
		return type;
	}
	
	public String getPort(){
		return (String)get("port");
	}
	
	public String getUseIP(){
		return (String)get("useip");
	}

	public boolean isUseIp() {
		if (getUseIP().equals("1"))
			return true;
		else
			return false;
	}
	
	public String getDns(){
		return (String)get("dns");
	}
	
	public String getIP(){
		return (String)get("ip");
	}
	
	@SuppressWarnings("unchecked")
	public void setID(String id){
		this.remove("interfaceid");
		this.put("interfaceid",id);
	}
}