package ru.sonic.zabbix.pro.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Acknowledge extends ZabbixObject {
	/**
	 * "acknowledgeid": "1",
                    "userid": "1",
                    "eventid": "9695",
                    "clock": "1350640590",
                    "message": "Problem resolved.\n\r----[BULK ACKNOWLEDGE]----",
                    "alias": "Admin"
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Acknowledge";

	public Acknowledge(Map<?, ?> m){
		super(m);
	}
	
	public String getAcknowledgeid(){
		return (String)get("acknowledgeid");
	}
	
	public String getuserid(){
		return (String)get("userid");
	}
	
	@Override
	public String toString() {
		return getalias() + ": "+getmessage();
	}
	
	public String geteventid(){
		return (String)get("eventid");
	}
	
	public String getclock(){
		return (String)get("clock");
	}
	
	public String getmessage(){
		return (String)get("message");
	}
	
	public String getalias(){
		return (String)get("alias");
	}
	
	public String getClockString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); 
		long clock = Integer.parseInt(getclock());
		Date dateClock = new Date(clock*1000);
		return dateFormat.format(dateClock).toString();
	}
}