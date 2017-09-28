package ru.sonic.zabbix.pro.base;

import java.util.Map;

/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Mediatype extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Mediatype";

	public Mediatype(Map<?, ?> m){
		super(m);
	}
	
	public String getType(){
		return (String)get("type");
	}
	
	public String getTypeString(){
		try {
			switch(Integer.parseInt(getType())) {
			case 0:
				return "Email";
			case 1:
                return "Script";
			case 2:
                return "SMS";
			case 3:
                return "Jabber";
            default:
            	return "Unknown";
			}
		} catch (Exception e) {
			return "Unknown";
		}
	}
	
	public String getDetails(){
		try {
			switch(Integer.parseInt(getType())) {
			case 0:
				return "SMTP server: "+getSmtp_server()+"\n	SMTP helo:"+getSmtp_helo()+"\n	SMTP email: "+getSmtp_email();
			case 1:
                return "Name: "+getExec_path();
			case 2:
                return "GSM modem: "+getGsm_modem();
			case 3:
                return "Identifer: "+getUsername();
            default:
            	return "Unknown";
			}
		} catch (Exception e) {
			return "Unknown";
		}
	}
	
	public String getID(){
		return (String)get("mediatypeid");
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	public String getDescription(){
		return (String)get("description");
	}
	
	
	public String getSmtp_server(){
		return (String)get("smtp_server");
	}
	
	
	public String getSmtp_helo(){
		return (String)get("smtp_helo");
	}
	
	
	public String getSmtp_email(){
		return (String)get("smtp_email");
	}
	
	public String getExec_path(){
		return (String)get("exec_path");
	}
	
	public String getGsm_modem(){
		return (String)get("gsm_modem");
	}
	
	public String getUsername(){
		return (String)get("username");
	}
	
	public String getPasswd(){
		return (String)get("passwd");
	}
}