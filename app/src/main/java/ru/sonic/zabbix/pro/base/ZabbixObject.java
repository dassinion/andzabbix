package ru.sonic.zabbix.pro.base;

import java.util.Map;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.R;

/**
 * base class for all zabbix objects (hosts, items, triggers, etc)
 * @author dassinion
 *
 */
public class ZabbixObject extends JSONObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4027800885260370007L;


	public ZabbixObject() {
		super();
	}

	public ZabbixObject(Map<?, ?> map) {
		super(map);
	}
	
	public int getStatusString() {
		try {
			switch(Integer.parseInt(getStatus())) {
			case 0:
				return R.string.enable;
			case 1:
                return R.string.disable;
            default:
            	return R.string.unknown;
			}
		} catch (Exception e) {
			return R.string.unknown;
		}
	}
	
	public int getStatusImg() {
		try {
			switch(Integer.parseInt(getStatus())) {
			case 0:
				return R.drawable.ok_icon_bb;
			case 1:
                return R.drawable.error2;
            default:
            	return R.drawable.help;
			}
		} catch (Exception e) {
			return R.drawable.help;
		}
	}
	
	public String getStatus() {
		if (get("status")==null) {
			return "0";
		} else
			return (String) get("status");
	}
	
	public boolean getEnStatus(){
		if (getStatus().equals("0"))
			return true;
		else
			return false;
	}
	
	@SuppressWarnings("unchecked")
	public void setStatus(String status){
		this.remove("status");
		this.put("status",status);
	}
}