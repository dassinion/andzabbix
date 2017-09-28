package ru.sonic.zabbix.pro.base;

import java.util.Map;

import org.json.simple.JSONArray;

import ru.sonic.zabbix.pro.R;


/**
 * Object Representing a Zabbix User
 * @author dassinion
 *
 */
public class User extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "User";

	public User(Map<?, ?> m){
		super(m);
	}
	
	public String getName(){
		return (String)get("name");
	}
	
	public String getID(){
		return (String)get("userid");
	}
	
	@Override
	public String toString() {
		return getalias()+"\n  Name:	"+getName()+"  "+getsurname();
	}
	
	public String getalias(){
		return (String)get("alias");
	}
	
	public String getsurname(){
		return (String)get("surname");
	}
	
	public String geturl(){
		return (String)get("url");
	}
	
	public String getautologin(){
		return (String)get("autologin");
	}
	
	public boolean getbautologin(){
		return getautologin().equals("0")?false:true;
	}
	
	public int getautologinImg(){
		if (getbautologin())
			return R.drawable.ok_bb;
		else
			return R.drawable.error2;
	}
	
	public String getautologout(){
		return (String)get("autologout");
	}
	
	public String getlang(){
		return (String)get("lang");
	}
	
	public String getrefresh(){
		return (String)get("refresh");
	}
	
	public String gettype(){
		return (String)get("type");
	}
	
	public String getTypeString(){
		String type = "";
		switch (Integer.parseInt(gettype())) {
			case 1:
				type = "Zabbix user";
				break;
			case 2:
				type = "Zabbix admin";
				break;
			case 3:
				type = "Superadmin";
				break;
			default:
				type =  "Unknown";
		}
		return type;
	}
	
	public String gettheme(){
		return (String)get("theme");
	}
	
	public String getattempt_failed(){
		return (String)get("attempt_failed");
	}
	
	public String getattempt_ip(){
		return (String)get("attempt_ip");
	}
	
	public String getattempt_clock(){
		return (String)get("attempt_clock");
	}
	
	public String getrows_per_page(){
		return (String)get("rows_per_page");
	}
	
	public String getpasswd(){
		return (String)get("passwd");
	}
	
	public JSONArray getGroups(){
		if (get("usrgrps")!=null)
			return (JSONArray)get("usrgrps");
		else
			return new JSONArray();
	}

	public JSONArray getMedias() {
		if (get("medias")!=null)
			return (JSONArray)get("medias");
		else
			return new JSONArray();
	}
	
	public JSONArray getMediaTypes() {
		if (get("mediatypes")!=null)
			return (JSONArray)get("mediatypes");
		else
			return new JSONArray();
	}
}