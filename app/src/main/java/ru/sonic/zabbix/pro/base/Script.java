package ru.sonic.zabbix.pro.base;

import java.util.Map;

/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Script extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Script";

	public Script(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("scriptid");
	}
	
	@Override
	public String toString() {
		return getName()+"\n"+getCommand();
	}
	
	public String getCommand(){
		return (String)get("command");
	}
	
	public String getHost_access(){
		return (String)get("host_access");
	}
	
	public String getusrgrpid(){
		return (String)get("usrgrpid");
	}
	
	public String getgroupid(){
		return (String)get("groupid");
	}
	
	public String getType(){
		if ((String)get("type")!=null)
			return (String)get("type");
		else
			return "0";
	}
	
	public String getTypeString(){
		String type = "";
		switch (Integer.parseInt(getType())) {
			case 0:
				type = "Script";
				break;
			case 1:
				type = "IPMI";
				break;
			default:
				type = "Unknown";
				break;
		}
		return type;
	}
	
	public String getExecuteOn(){
		if ((String)get("execute_on")!=null)
			return (String)get("execute_on");
		else
			return "1";
	}
	
	public String getExecuteOnString(){
		String type = "";
		switch (Integer.parseInt(getExecuteOn())) {
			case 1:
				type = "Zabbix server";
				break;
			case 2:
				type = "Zabbix agent";
				break;
			default:
				type = "Unknown";
				break;
		}
		return type;
	}
	
	public String getDescription(){
		return (String)get("description");
	}
	
	public String getConfirmation(){
		if ((String)get("confirmation")!=null)
			return (String)get("confirmation");
		else
			return "";
	}
}