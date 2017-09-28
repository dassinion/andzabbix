package ru.sonic.zabbix.pro.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.R.string;
import android.graphics.Color;
import android.util.Log;

/**
 * Object Representing a Zabbix Host
 * 
 * @author dassinion
 * 
 */
public class Host extends ZabbixObject {
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Host";

	public Host(Map<?, ?> m) {
		super(m);
	}
 
	public String getHost() {
		return (String) get("host");
	}

	public String getName() {
		return (String) get("name");
	}
	
	public String getVisName() {
		if (getName()==null || getName().equals(""))
			return getHost();
		else
			return getName();
	}

	public String getStatus() {
		return (String) get("status");
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
			return Color.GRAY;
		else
			return Color.RED;
	}

	public int getStatusImg() {
		try {
			switch (Integer.parseInt(getStatus())) {
			case 0:
				return R.drawable.ok_bb;
			case 1:
				return R.drawable.error2;
			default:
				return R.drawable.help;
			}
		} catch (NullPointerException e) {
			return R.drawable.zabbix_unknown;
		} catch (Exception e) {
			return R.drawable.zabbix_unknown;
		}
	}

	public int getAvailableString() {
		try {
			switch (Integer.parseInt(getAvailable())) {
			case 0:
				return R.string.unknown;
			case 1:
				return R.string.available;
			case 2:
				return R.string.unavailable;
			case 3:
				return R.string.unknown;
			default:
				return R.string.unknown;
			} 
		}catch (NullPointerException e) {
				return R.string.unknown;
		} catch (Exception e) {
				return R.string.unknown;
		}
	}

	public String getAvailable() {
		return get("available").toString();
	}

	public int getAvailableImg() {
		try {
			switch (Integer.parseInt(getAvailable())) {
			case 1:
				return R.drawable.zabbix_available_bb;
			case 2:
				return R.drawable.zabbix_unavailable;
			case 3:
				return R.drawable.zabbix_unknown;
			default:
				return R.drawable.zabbix_unknown;
			}
		} catch (NullPointerException e) {
			return R.drawable.zabbix_unknown;
		} catch (Exception e) {
			return R.drawable.zabbix_unknown;
		}
	}
	
	public boolean getIsUnAvailable() {
		return getAvailable().equals("2")?true:false;
	}

	public String getDisable_until() {
		return (String) get("disable_until");
	}

	public String getDisable_untilDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd MMMM yyyy, HH:mm:ss");
		long timestamp = Integer.parseInt(getDisable_until());
		Date lastchange = new Date(timestamp * 1000);
		return dateFormat.format(lastchange).toString();
	}

	public String getMaintenance_status() {
		return (String) get("maintenance_status");
	}

	public String getMaintenance_statusstring() {
		return (String) get("maintenance_status");
	}

	public String getErrors_from() {
		return (String) get("errors_from");
	}

	public String getErrors_fromDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd MMMM yyyy, HH:mm:ss");
		long timestamp = Integer.parseInt(getErrors_from());
		Date lastchange = new Date(timestamp * 1000);
		return dateFormat.format(lastchange).toString();
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getID() {
		return (String) get("hostid");
	}

	public String getError() {
		return (String) get("error");
	}

	public String getLastaccess() {
		return (String) get("lastaccess");
	}

	public String getProxy_hostid() {
		return (String) get("proxy_hostid");
	}

	public int getIPMIAvailableString() {
		try {
			switch (Integer.parseInt(getIPMI_available())) {
			case 0:
				return R.string.unknown;
			case 1:
				return R.string.available;
			case 2:
				return R.string.unavailable;
			case 3:
				return R.string.unknown;
			default:
				return R.string.unknown;
			} 
		}catch (NullPointerException e) {
				return R.string.unknown;
		} catch (Exception e) {
				return R.string.unknown;
		}
	}

	public int getIPMIavailableImg() {
		try {
			switch (Integer.parseInt(getIPMI_available())) {
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
		} catch (Exception e) {
			return R.drawable.snmp_unknown;
		}
	}

	public String getIPMIerror() {
		return (String) get("ipmi_error");
	}

	public String getIPMI_authtype() {
		return (String) get("ipmi_authtype");
	}

	public String getIPMI_privilege() {
		return (String) get("ipmi_privilege");
	}

	public String getIPMI_username() {
		return (String) get("ipmi_username");
	}

	public String getIPMI_password() {
		return (String) get("ipmi_password");
	}

	public String getIPMI_disable_until() {
		return (String) get("ipmi_disable_until");
	}
	
	public String getIPMI_disable_untilDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd MMMM yyyy, HH:mm:ss");
		long timestamp = Integer.parseInt(getIPMI_disable_until());
		Date lastchange = new Date(timestamp * 1000);
		return dateFormat.format(lastchange).toString();
	}

	public String getIPMI_available() {
		return (String) get("ipmi_available");
	}
	
	public boolean getIsIPMIUnAvailable() {
		return getIPMI_available().equals("2")?true:false;
	}

	public int getSnmpAvailableString() {
		try {
			switch (Integer.parseInt(getSnmp_available())) {
			case 0:
				return R.string.unknown;
			case 1:
				return R.string.available;
			case 2:
				return R.string.unavailable;
			case 3:
				return R.string.unknown;
			default:
				return R.string.unknown;
			} 
		}catch (NullPointerException e) {
				return R.string.unknown;
		} catch (Exception e) {
				return R.string.unknown;
		}
	}

	public int getSnmpAvailableImg() {
		try {
			switch (Integer.parseInt(getSnmp_available())) {
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
		} catch (Exception e) {
			return R.drawable.snmp_unknown;
		}
	}

	public boolean getIsSnmpUnAvailable() {
		return getSnmp_available().equals("2")?true:false;
	}
	
	public String getSnmp_error() {
		return (String) get("snmp_error");
	}

	public String getSnmp_disable_until() {
		return (String) get("snmp_disable_until");
	}
	
	public String getSnmp_disable_untilDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd MMMM yyyy, HH:mm:ss");
		long timestamp = Integer.parseInt(getSnmp_disable_until());
		Date lastchange = new Date(timestamp * 1000);
		return dateFormat.format(lastchange).toString();
	}

	public String getSnmp_available() {
		return (String) get("snmp_available");
	}
	
	public String getJMX_available() {
		return (String) get("jmx_available");
	}

	public boolean getIsJMXUnAvailable() {
		try {
			return getJMX_available().equals("2")?true:false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getJmxAvailableImg() {
		int i = 3;
		try {
			String jmx = getJMX_available();
			i = Integer.parseInt(jmx);
		} catch (NullPointerException e) {

		} catch (NumberFormatException e) {
			
		} catch (Exception e) {}

		switch (i) {
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

	public String getJmx_disable_until() {
		if ((String) get("jmx_disable_until")!=null)
			return (String) get("jmx_disable_until");
		else
			return "0";
	}
	
	public String getJmx_disable_untilDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd MMMM yyyy, HH:mm:ss");
		long timestamp = Integer.parseInt(getJmx_disable_until());
		Date lastchange = new Date(timestamp * 1000);
		return dateFormat.format(lastchange).toString();
	}

	public String getJMXerror() {
		if ((String) get("jmx_error")!=null)
			return (String) get("jmx_error");
		else
			return "";
	}

	public String getMaintenance_type() {
		return (String) get("maintenance_type");
	}

	public String getMaintenance_from() {
		return (String) get("maintenance_from");
	}
	
	public String getMaintenance_fromDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd MMMM yyyy, HH:mm:ss");
		long timestamp = Integer.parseInt(getMaintenance_from());
		Date lastchange = new Date(timestamp * 1000);
		return dateFormat.format(lastchange).toString();
	}

	@SuppressWarnings("unchecked")
	public JSONArray getInterfaces() {
		try {
			//Log.i(TAG,"getInterfaces: "+get("interfaces"));
			JSONObject interf = (JSONObject) get("interfaces");
			if (interf!=null) {
				JSONArray interfarr = new JSONArray();
				interfarr.add(interf);
				return interfarr;
			} else {
				JSONObject interfjsonobj = new JSONObject();
				interfjsonobj.put("dns", getDns());
				interfjsonobj.put("ip", getIP());
				interfjsonobj.put("port", getPort());
				interfjsonobj.put("useip", getUseIP());
				JSONObject interfaces = new JSONObject();
				interfaces.put(null, interfjsonobj);
				JSONArray interfarr = new JSONArray();
				interfarr.add(interfjsonobj);
				return interfarr;
			}
		} catch (Exception e) {
			JSONArray interfarr = (JSONArray) get("interfaces");
			if (interfarr!=null)
				return interfarr;
			else return new JSONArray();
		}
	}

	public JSONArray getArrayInterfaces() {
		return (JSONArray) get("interfaces");
	}

	public JSONArray getGroups() {
		JSONArray grps = (JSONArray) get("groups");
		if (grps!=null)
			return grps;
		else
			return new JSONArray();
	}

	public JSONArray getTemplates() {
		JSONArray grps = (JSONArray) get("templates");
		if (grps!=null)
			return grps;
		else
			return new JSONArray();
	}

	public JSONArray getParentTemplates() {
		JSONArray grps = (JSONArray) get("parentTemplates");
		if (grps!=null)
			return grps;
		else
			return new JSONArray();
	}
	
	/*OLD zabbix servers (1.8.x)*/
	
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
}