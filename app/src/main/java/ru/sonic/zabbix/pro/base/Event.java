package ru.sonic.zabbix.pro.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import android.util.Log;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;

/**
 * Object Representing a Zabbix Trigger
 * @author dassinion
 *
 */
public class Event extends ZabbixObject {
	private String ackMessage;
	private static final long serialVersionUID = 1626415952936919799L;
	private static final String TAG = "ZabbixEvent";

	public Event(Map<?, ?> m) {
		super(m);
	}

	public String getSource() {
		return (String) get("source");
	}
	
	public String getObject() {
		return (String) get("object");
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClockString());
		sb.append(" : ");
		sb.append(getValue());
		return sb.toString();
	}
		
	public String getObjectid() {
		return (String) get("objectid");
	}
	
	public String getID() {
		return (String) get("eventid");
	}
	
	public String getHost() {
			String [] Colors = new String[6];
			Colors [0] = new String ("OK");
			Colors [1] = new String ("PROBLEM");
			Colors [2] = new String ("UNKNOWN");
			String Severity = Colors[Integer.parseInt(getValue())];
		return Severity;
	}
	
	public String getDescription() {
		return getClockString();
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
	
	public String getAgeTime() {
		return getID();
	}
	
	public String getValue() {
		return (String) get("value");
	}
	
	public String getSeverity() {
		String [] Colors = new String[6];
		Colors [0] = new String ("#00EE00");
		Colors [1] = new String ("#FF6666");
		Colors [2] = new String ("#AAAAAA");
		String Severity = Colors[Integer.parseInt(getValue())];
		return Severity;
	}
	
	public int getActiveImg(){
        switch( Integer.parseInt(getValue()) ) {
          case 0:
                        return R.drawable.ok_icon;
          case 1:
                        return R.drawable.trigger_active;
        }
        return R.drawable.zabbix_unknown;
	}
	
	public String getAcknowledged() {
		return (String) get("acknowledged");
	}
	
	public int getAckImg() {
		switch(Integer.parseInt(getAcknowledged())) {
        case 0:
                      return android.R.drawable.checkbox_off_background;
        case 1:
                      return android.R.drawable.checkbox_on_background;
      }
      return android.R.drawable.checkbox_off_background;
	}
	
	/*
	public String getTriggerDesc() {
		JSONArray trigger = (JSONArray) get("triggers");
		Log.d(TAG, "trigger Aray: "+trigger);
		//JSONArray array = (JSONArray) JSONValue.parse(trigger);
		JSONObject trigger0 = (JSONObject) JSONValue.parse(trigger.get(0).toString());
		//Log.d(TAG, "Desc: "+trigger0.get("description"));
		return (String) trigger0.get("description");
	} */
	
	public void setAckMessage(String data) {
		this.ackMessage = data;
	}
	
	public String getAckMessage() {
		return this.ackMessage;
	}
	
	public JSONArray getAcknowledges() {
		if (get("acknowledges")!=null && !get("acknowledges").equals("")) {
			//String ackjsonstring = get("acknowledges").toString();
			//JSONArray ret = parseArray(ackjsonstring);
			return (JSONArray) get("acknowledges");
		} else
			return new JSONArray();
	}
	
	public boolean hasAcknowledges() {
		if (getAcknowledges().size()>0)
			return true;
		else
			return false;
	}
	
	public List<String> getAskComments() {
		List<String> askCommentsList = new ArrayList<String>();
		try {
			JSONArray asknowledges = getAcknowledges();
			for (Iterator<JSONObject> it = asknowledges.iterator(); it.hasNext();) {
				JSONObject obj = it.next();
				askCommentsList.add(obj.get("message").toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return askCommentsList;
	}
	
	public List<Acknowledge> getAcknowledgesList() {
		List<Acknowledge> askCommentsList = new ArrayList<Acknowledge>();
		try {
			JSONArray asknowledges = getAcknowledges();
			for (Iterator<JSONObject> it = asknowledges.iterator(); it.hasNext();) {
				JSONObject obj = it.next();
				Acknowledge ack = new Acknowledge(obj);
				askCommentsList.add(ack);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return askCommentsList;
	}
	
	public JSONArray parseArray(String s) {
		//JSONArray obj = (JSONArray) JSONValue.parse(s);
		JSONArray obj = null;
		try {
			obj = (JSONArray) JSONValue.parseWithException(s);
			return obj;
		} catch (ClassCastException e) {
			JSONObject json = new JSONObject();
			json.put("ClassCastException", e);
            Log.e(TAG, "Got ClassCastException: "+e);
            return null;
		} catch (ParseException e) {
			JSONObject json = new JSONObject();
			json.put("ParseException", e);
            Log.e(TAG, "Got ParseException: "+e);
            return null;
		} catch (NullPointerException e) {
			JSONObject json = new JSONObject();
			json.put("NullPointerException", e);
            Log.e(TAG, "Got NullPointerException: "+e);
            return null;
		} catch (Exception e) {
			JSONObject json = new JSONObject();
			json.put("Exception", e);
            Log.e(TAG, "Got Exception: "+e);
            return null;
		}
	}
}