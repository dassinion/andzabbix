package ru.sonic.zabbix.pro.base;

import android.util.Log;

import org.json.simple.JSONArray;

import java.util.Map;

import ru.sonic.zabbix.pro.R;


/**
 * Object Representing a Zabbix User
 * @author dassinion
 *
 */
public class Server extends ZabbixObject {
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Server";
	//public static final int KEY_SERVER = 0;
	public static final String KEY_SERVERNAME = "servername";
	public static final String KEY_URL = "url";
	public static final String KEY_USER = "login";
	public static final String KEY_PASS = "pass";
	public static final String KEY_TIMEOUT = "timeout";
	public static final String KEY_API_VERSION = "api_version";
	public static final String KEY_BASE_USE = "baseauth_use";
	public static final String KEY_BASE_LOGIN = "baseauth_login";
	public static final String KEY_BASE_PASS = "baseauth_pass";
	public static final String KEY_AUTH = "authkey";
	public boolean isLogedOn = false;

	public Server(Map<?, ?> m){
		super(m);
	}
	public Server(){}

	@Override
	public String toString() {
		return getName()+": "+geturl();
	}

	public String getName(){
		return (String)get(KEY_SERVERNAME);
	}
	public String getlogin(){ return (String)get(KEY_USER);}
	public String geturl(){return (String)get(KEY_URL);	}
	public String getpass(){
		return (String)get(KEY_PASS);
	}
	public String gettimeout(){
		return (String)get(KEY_TIMEOUT);
	}
	public boolean getbaseauth_use(){
		return (boolean)get(KEY_BASE_USE);
	}
	public String getbaseauth_login(){
		return (String)get(KEY_BASE_LOGIN);
	}
	public String getbaseauth_pass(){
		return (String)get(KEY_BASE_PASS);
	}
	public String getapiversion(){return (String)get(KEY_API_VERSION);}
	public void setApiVersion(String api_version) {this.put(KEY_API_VERSION,api_version);};
	public String getauthkey(){return (String)get(KEY_AUTH);}
	public void setAuthKey(String authkey){this.put(KEY_AUTH,authkey);}

	public boolean isLogedOn() {
		return this.isLogedOn;
	}
	public void setLogedOn(boolean status) {
		this.isLogedOn = status;
	}
}