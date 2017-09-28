package ru.sonic.zabbix.pro.base;

import java.util.Map;

import ru.sonic.zabbix.pro.R;
import android.graphics.Color;


/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class DHost extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Host";

	public DHost(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("host");
	}
	
	public String getStatus(){
		return (String)get("status");
	}
	
	public int getStatusString() {
		String stat = getStatus();
		if (stat.equals("0"))
			return R.string.monitored;
		else if (stat.equals("1"))
			return R.string.not_monitored;
		else 
			return R.string.unknown;
	}
	
	public int getStatusColor() {
		String stat = getStatus();
		if (stat.equals("0"))
			return Color.GREEN;
		else if (stat.equals("1"))
			return Color.RED;
		else 
			return Color.GRAY;
	}

	public String getAvailableString(){
		String [] Available = new String[4];
		Available [0] = new String ("Error");
		Available [1] = new String ("Available");
		Available [2] = new String ("Unavailable");
		Available [3] = new String ("Unknown");
		String Avail = Available[Integer.parseInt(get("available").toString())];
		//Log.e(TAG, "Color:"+Available);
		return Avail;
	}
	
	public int getAvailableImg(){
		try {
		        switch( Integer.parseInt(get("available").toString()) ) {
		          case 1:
		                        return R.drawable.zabbix_available;
		          case 2:
		                        return R.drawable.zabbix_unavailable;
		          case 3:
		                        return R.drawable.zabbix_unknown;
		          default:
		        	  			return R.drawable.zabbix_unknown;
		        }
		//} else
	  	//		return R.drawable.zabbix_unknown;
		} catch (NullPointerException e) {
			return R.drawable.zabbix_unknown;
		}
	}
	
	@Override
	public String toString() {
		return getID();
	}
	
	public String getID(){
		return (String)get("dhostid");
	}
	
	public String getdns(){
		return (String)get("dns");
	}
	public String getip(){
		return (String)get("ip");
	}
	
	public String getport(){
		return (String)get("port");
	}
	
	public String geterror(){
		return (String)get("error");
	}
	
	public String getlastaccess(){
		return (String)get("lastaccess");
	}
	
	public String getsnmp_error(){
		return (String)get("snmp_error");
	}
	
	public String getproxy_hostid(){
		return (String)get("proxy_hostid");
	}
	
	public String getSnmpAvailableString(){
		String [] Available = new String[4];
		Available [0] = new String ("Disabled");
		Available [1] = new String ("Available");
		Available [2] = new String ("Unavailable");
		Available [3] = new String ("Unknown");
		String Avail = Available[Integer.parseInt(get("snmp_available").toString())];
		return Avail;
	}
	
	public String getIPMIAvailableString(){
		String [] Available = new String[4];
		Available [0] = new String ("Disabled");
		Available [1] = new String ("Available");
		Available [2] = new String ("Unavailable");
		Available [3] = new String ("Unknown");
		String Avail = Available[Integer.parseInt(get("ipmi_available").toString())];
		return Avail;
	}
	
	public int getSnmpAvailableImg(){
		try {
			switch( Integer.parseInt(get("snmp_available").toString()) ) {
	        case 1:
	                      return R.drawable.snmp_available;
	        case 2:
	                      return R.drawable.snmp_unavailable;
	        case 3:
	                      return R.drawable.snmp_unknown;
	        default:
	      	  			return R.drawable.snmp_unknown;
	      }
		} catch (NullPointerException e) {
			return R.drawable.snmp_unknown;
		}
	}
	
	public int getIPMIavailableImg(){
		try {
			switch( Integer.parseInt(get("ipmi_available").toString()) ) {
	        case 1:
	                      return R.drawable.ipmi_available;
	        case 2:
	                      return R.drawable.ipmi_unavailable;
	        case 3:
	                      return R.drawable.ipmi_unknown;
	        default:
	      	  			return R.drawable.ipmi_unknown;
			}
		} catch (NullPointerException e) {
			return R.drawable.ipmi_unknown;
		}
	}
	 
	public int getJmxAvailableImg(){
		String jmx = "3";
		try {
			jmx = get("jmx_available").toString();
		} catch (NullPointerException e) {
			// nthing
		}
		
		switch(Integer.parseInt(jmx)) {
        case 1:
                      return R.drawable.jmx_available;
        case 2:
                      return R.drawable.jmx_unavailable;
        case 3:
                      return R.drawable.jmx_unknown;
        default:
      	  			return R.drawable.jmx_unknown;
      }
	}
	
	public String getdisable_until(){
		return (String)get("disable_until");
	}
	
	public String getIPMIerror(){
		return (String)get("ipmi_error");
	}
	
	public String getJMXerror(){
		return (String)get("jmx_error");
	}
}