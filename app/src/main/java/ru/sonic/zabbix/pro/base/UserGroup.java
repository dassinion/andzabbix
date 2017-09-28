package ru.sonic.zabbix.pro.base;

import java.util.Map;

import org.json.simple.JSONArray;

import ru.sonic.zabbix.pro.R;


/**
 * Object Representing a Zabbix User
 * @author dassinion
 *
 */
public class UserGroup extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "UserGroup";

	public UserGroup(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("usrgrpid");
	}
	
	@Override
	public String toString() {
		String ret = getName();
		ret = ret + "\n Ststus:	"+getStatusString();
		ret = ret + "\n Gui:		"+getGuiString();
		ret = ret + "\n Debug:	"+getDebugString();
		if (getApi_access()!=null)
			ret = ret +"\n Api:		"+getApiString();	
		return ret;
	}
	
	public String getGui_access(){
		return (String)get("gui_access");
	}
	
	public boolean getbGui_access(){
		try {
			if (getGui_access().equals("0"))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getGuiString() {
		if (getbGui_access())
			return R.string.enable;
		else
			return R.string.disable;
	}
	
	public int getGuiImg() {
		try {
			switch (Integer.parseInt(getGui_access())) {
			case 0:
				return R.drawable.ok_bb;
			case 1:
				return R.drawable.error2;
			default:
				return R.drawable.help;
			}
		} catch (NullPointerException e) {
			return R.drawable.help;
		}
	}
	
	public String getUsers_status(){
		return (String)get("users_status");
	}
	
	public boolean getbUsers_status(){
		if (getUsers_status().equals("0"))
			return true;
		else
			return false;
	}
	
	public String getApi_access(){
		return (String)get("api_access");
	}
	
	public boolean getbApi_access(){
		try {
			if (getApi_access().equals("0"))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getApiString() {
		if (getApi_access()!=null)
			if (getbApi_access())
				return R.string.enable;
			else
				return R.string.disable;
		else
			return R.string.not_supported;
	}
	
	public int getApiImg() {
		try {
			switch (Integer.parseInt(getApi_access())) {
			case 0:
				return R.drawable.ok_bb;
			case 1:
				return R.drawable.error2;
			default:
				return R.drawable.help;
			}
		} catch (NullPointerException e) {
			return R.drawable.help;
		}
	}
	
	public String getDebug_mode(){
		return (String)get("debug_mode");
	}
	
	public boolean getbDebug_mode(){
		if (getDebug_mode().equals("0"))
			return true;
		else
			return false;
	}
	
	public int getDebugString() {
		if (getbDebug_mode())
			return R.string.enable;
		else
			return R.string.disable;
	}
	
	public int getDebugImg() {
		try {
			switch (Integer.parseInt(getDebug_mode())) {
			case 0:
				return R.drawable.ok_bb;
			case 1:
				return R.drawable.error2;
			default:
				return R.drawable.help;
			}
		} catch (NullPointerException e) {
			return R.drawable.help;
		}
	}
	
	public JSONArray getUsers(){
		return (JSONArray)get("users");
	}
	
	@Override
	public int getStatusString() {
		if (getbUsers_status())
			return R.string.enable;
		else
			return R.string.disable;
	}
	
	@Override
	public String getStatus() {
		return (String) get("users_status");
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
			return R.drawable.help;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setStatus(String status){
		this.remove("users_status");
		this.put("users_status",status);
	}
	
	@SuppressWarnings("unchecked")
	public void setDebug(String status){
		this.remove("debug_mode");
		this.put("debug_mode",status);
	}
	
	@SuppressWarnings("unchecked")
	public void setApi(String status){
		this.remove("api_access");
		this.put("api_access",status);
	}
	
	@SuppressWarnings("unchecked")
	public void setGuiAccess(String status){
		this.remove("gui_access");
		this.put("gui_access",status);
	}
}