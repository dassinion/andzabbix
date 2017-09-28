package ru.sonic.zabbix.pro.base;

import android.graphics.Color;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import ru.sonic.zabbix.pro.R;

/**
 * Object Representing a Zabbix Trigger
 * @author dassinion
 *
 */
public class Trigger extends ZabbixObject {
	private String ackMessage;
	private static final long serialVersionUID = 1696664952321919799L;
//	private static final String TAG = "ZabbixTrigger";
	
	private static final int Information = 1;
	private static final int Warning = 2;
	private static final int Average = 3;
	private static final int High = 4;
	private static final int Disaster = 5;
	
	private static final int WithLastEventUnacknowledged = 1;
	private static final int WithUnacknowledgedEvents = 2;
	private static final int WithAcknowledgedEvents = 3;
	private static final int ALL = 4;
	
	private int Ack;
	public String colors;
	public long timecorrection;
	private boolean maintenance;
	private long id;
	private String ServerID;

	public Trigger(Map<?, ?> m) {
		super(m);
	}

	public Trigger() {
	}
	
	public void setMaintenance(boolean newmaintenance) {
		this.maintenance = newmaintenance;
	}
	
	public boolean getMaintenance() {
		return this.maintenance;
	}

	public String getDescription() {
		String str = (String) get("description"); 
		if (str!=null)
			return str;
		else
			return "";
	}
	
	public String getActive() {
		String str = (String) get("value");
		if (str!=null)
			return str;
		else
			return "0";
	}
	
	public int getActiveImg(){
        switch( Integer.parseInt(getActive()) ) {
          case 0:
                        return R.drawable.ok_icon_bb;
          case 1:
                        return R.drawable.attention;
          case 21:
        	  			return R.drawable.error_icon;
          case 41:
        	  			return R.drawable.wrench12transp;
        }
        return R.drawable.ok_icon;
	}
	
	public int getValueImg(){
        switch( Integer.parseInt(getActive()) ) {
          case 0:
                        return R.drawable.ok_bb;
          case 1:
                        return R.drawable.warning;
          case 41:
        	  			return R.drawable.wrench12transp;
        }
        return R.drawable.ok_icon;
	}

	@Override
	public int getStatusString() {
		try {
			switch(Integer.parseInt(getStatus())) {
			case 0:
				return R.string.activated;
			case 1:
                return R.string.disabled;
            default:
            	return R.string.unknown;
			}
		} catch (Exception e) {
			return R.string.unknown;
		}
	}
	
	public int getStatusYN() {
		try {
			switch(Integer.parseInt(getStatus())) {
			case 0:
				return R.string.yes;
			case 1:
                return R.string.no;
            default:
            	return R.string.unknown;
			}
		} catch (Exception e) {
			return R.string.unknown;
		}
	}
	
	@Override
	public String toString() {
		return getID();
	}
		
	public String getHost() {
		String host = (String) get("host");
		Object hostObj = get("hosts");
		if (host!=null) {
				return host;
		} else if ((hostObj!=null) && (hostObj instanceof JSONArray)) {
			Log.d("TRIGGER HostObject: ", hostObj.toString());
			JSONArray hostsArray = (JSONArray) hostObj;
			JSONObject firsthost = (JSONObject) hostsArray.get(0);
			return firsthost.get("host").toString();
		} else
			return "";
	}
	
	public String getID() {
		String str = (String) get("triggerid");
		if (str!=null)
			return str;
		else
			return "";
	}
	
	public String getSeverityColor() {
		String [] Colors = new String[6];
		if ( colors!=null && colors.equals("modern")) {
			Colors [0] = new String ("#FFFFFF");
			Colors [1] = new String ("#00EEEE");
			Colors [2] = new String ("#00EE00");
			Colors [3] = new String ("#FFFF33");
			Colors [4] = new String ("#FF66FF");
			Colors [5] = new String ("#DD0000");
		} else {
			Colors [0] = new String ("#DBDBDB");
			Colors [1] = new String ("#D6F6FF");
			Colors [2] = new String ("#FFF6A5");
			Colors [3] = new String ("#FFB689");
			Colors [4] = new String ("#FF9999");
			Colors [5] = new String ("#FF3838");
		}
		String Severity = "0";
		try {
			Severity = Colors[getSeverity()];
		} catch (Exception e) {
			Severity = "#DBDBDB";	
		}
		return Severity;
	}

    public int getSeverityColorInt() {
        Integer [] Colors = new Integer[6];
        Colors [0] = Color.parseColor("#DBDBDB");
        Colors [1] = Color.parseColor("#D6F6FF");
        Colors [2] = Color.parseColor("#FFF6A5");
        Colors [3] = Color.parseColor("#FFB689");
        Colors [4] = Color.parseColor("#FF9999");
        Colors [5] = Color.parseColor("#FF3838");

        Integer Severity = Color.parseColor("#DBDBDB");
        try {
            Severity = Colors[getSeverity()];
        } catch (Exception e) {
            Severity = Color.parseColor("#DBDBDB");
        }
        return Severity;
    }

    public String getSeverityStringt() {
        String [] severityStrings = new String[6];
        severityStrings [0] = "Information";
        severityStrings [1] = "Information";
        severityStrings [2] = "Warning";
        severityStrings [3] = "Average";
        severityStrings [4] = "High";
        severityStrings [5] = "Disaster";

        String Severity = "Unknown";
        try {
            Severity = severityStrings[getSeverity()];
        } catch (Exception e) {
            Severity = "Unknown";
        }
        return Severity;
    }


    public int getSeverity() {
		try {
			return Integer.parseInt(getPriority());
		} catch (Exception e) {
			return 0;	
		}
	}
	
	public String getAgeTime() { 
		try {
			long timestamp = Integer.parseInt((String) getLastchangeStamp());
			return getAgeTime(timestamp);
		} catch (Exception e) {
			return "Unknown";
		}
	}
	
	public String getLastchangeStamp() {
		//Log.d(TAG, "getLastchangeStamp: "+ get("lastchange"));
		String str = (String) get("lastchange");
		if (str!=null)
			return str;
		else
			return "";
	}
	
	public String getLastchangeString() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss"); 
			long timestamp = Integer.parseInt(getLastchangeStamp());
			Date lastchange = new Date(timestamp*1000);
			return dateFormat.format(lastchange).toString();
		} catch (Exception e) {
			return "Unknown";
		}
	}
	
	public int getIssueStatus() {
		try {
			if (Integer.parseInt(getActive()) == 1) { 
				return R.string.trigger_active;
			} else {
				return R.string.trigger_not_active;}
		} catch (Exception e) {
			return R.string.unknown;
		}
	}
	
	public String getPriority() {
		return get("priority").toString();
	}

	public int getPriorityString() {
		try {
			int priority;
			switch (Integer.parseInt(getPriority())) {
				case Information:
					priority = R.string.information;
					break;
				case  Warning:
					priority = R.string.warning;
					break;
				case Average:
					priority = R.string.average;
					break;
				case High:
					priority = R.string.high;
					break;
				case Disaster:
					priority = R.string.disaster;
					break;
				default:
					priority =  R.string.nop;
			}
			return priority;
		} catch (Exception e) {
			return R.string.unknown;
		}
	}
	
	public String getValue() {
		String str = (String) get("value");
		if (str!=null)
			return str;
		else
			return "";
	}
	public String gettype() {
		String str = (String) get("type");
		if (str!=null)
			return str;
		else
			return "";
	}
	public String getvalue_flags() {
		String str = (String) get("value_flags");
		if (str!=null)
			return str;
		else
			return "";
	}
	public String getflags() {
		String str = (String) get("flags");
		if (str!=null)
			return str;
		else
			return "";
	}
	public String getComments() {
		String str = (String) get("comments");
		if (str!=null)
			return str;
		else
			return "";
	}
	public String getError() {
		String str = (String) get("error");
		if (str!=null)
			return str;
		else
			return "";
	}
	public String getUrl() {
		String str = (String) get("url");
		if (str!=null)
			return str;
		else
			return "";
	}
	
	/**
	 * get Age time as String
	 * @return
	 */
	private String getAgeTime(long lastchange) {
		Date now = new Date();
		long diffTime = Math.abs(now.getTime()/1000 + (timecorrection*60*60) - lastchange);
		//Log.d(TAG, "diffTime: "+diffTime);
		StringBuffer sb = new StringBuffer();
		
		long months = diffTime/2592000;
		if ( months >= 1 ) { sb.append(months+"M "); diffTime = (diffTime % 2592000);}
		
		long weeks = diffTime/604800;
		if ( weeks >= 1 ) { sb.append(weeks+"w "); diffTime = (diffTime % 604800);}
		
		long days = diffTime/86400;
		if ( days >= 1 ) { sb.append(days+"d "); diffTime = (diffTime % 86400);}
		
		long hours = diffTime/3600;
		if ( hours >= 1 ) { sb.append(hours+"h "); diffTime = (diffTime % 3600);}
		
		long mins = diffTime/60;
		if ( mins >= 1 ) { sb.append(mins+"m "); diffTime = (diffTime % 60);}
		
		long Sec = diffTime;
		sb.append(Sec+"s");
		return sb.toString();
	}
	
	public int getAck() {
		return Ack;
	}
	
	public void setAck(int ack) {
		Ack = ack;
	}

	public int getAckString() {
		switch (getAck()) {
		case WithLastEventUnacknowledged:
			return R.string.unacknowledged;
		case WithUnacknowledgedEvents:
			return R.string.has_unacknowledged;
		case WithAcknowledgedEvents:
			return R.string.acknowledged;
		case ALL:
			return R.string.all;
		default:
			return R.string.all;
		}
	}
	
	public int getAckImg() {
		switch (getAck()) {
		case WithLastEventUnacknowledged:
			return android.R.drawable.checkbox_off_background;
		case WithUnacknowledgedEvents:
			return android.R.drawable.checkbox_on_background;
		case WithAcknowledgedEvents:
			return android.R.drawable.checkbox_on_background;
		case ALL:
			return android.R.drawable.checkbox_off_background;
		default:
			return android.R.drawable.checkbox_off_background;
		}
	}

	public String getHostID() {
		String host = (String) get("host");
		Object hostObj = get("hosts");
		if (host!=null) {
			return host;
		} else if ((hostObj!=null) && (hostObj instanceof JSONArray)) {
			JSONArray hostsArray = (JSONArray) hostObj;
			JSONObject firsthost = (JSONObject) hostsArray.get(0);
			return firsthost.get("hostid").toString();
		} else
			return "";
	}
	
	public String getExpression() {
		String str = (String) get("expression");
		if (str!=null)
			return str;
		else
			return "";
	}
	
	public void setAckMessage(String data) {
		this.ackMessage = data;
	}
	
	public String getAckMessage() {
		return this.ackMessage;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId(){
		return this.id;
	}

	public String getServerID(){
		return this.ServerID;
	}

	public void setServerID(String ServerID) {
        this.ServerID = ServerID;
    }
}