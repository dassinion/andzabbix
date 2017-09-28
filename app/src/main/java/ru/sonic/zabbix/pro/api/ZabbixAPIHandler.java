package ru.sonic.zabbix.pro.api;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import ru.sonic.zabbix.pro.base.Base64;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.database.DBAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Zabbix API Main Object. All other classes can retrieve data via this class.
 * 
 * @author dassinion
 *
 */
public abstract class ZabbixAPIHandler {
	//which activity do we belong to? required for error popups and stuff
	protected Activity owner = null;
    protected boolean isDebug = true;

	// current context
	protected Context ctx = null;

	// Server id
	protected Server server = null;

	//id counter
	private int id = 100;

	//logging tag
	protected static final String TAG = "ZabbixAPIHandler";

	/**
	 * create an api handler
	 * @param owner Activity this api handler belongs to
	 */
	protected ZabbixAPIHandler(Activity owner,Server zabbixServer) {
		this.owner = owner;
		this.ctx = owner.getApplicationContext();
		this.server = zabbixServer;
	}

	protected ZabbixAPIHandler(Context context,Server zabbixServer) {
		this.ctx = context;
		this.server = zabbixServer;
	}


	/**
	 * @return the API Limit Active Trigger
	 */
	public String getTimeOut() throws ZabbixAPIException {
		return this.server.gettimeout();
	}


	/*** 
	 * @return the API use base autentification
	 */
	public boolean get_base_use() throws ZabbixAPIException {
		return this.server.getbaseauth_use();
	}
	
	/*** 
	 * @return the API use base autentification login
	 */
	public String get_base_login() throws ZabbixAPIException {
		return this.server.getbaseauth_login();
	}

	/*** 
	 * @return the API use base autentification password
	 */
	public String get_base_pass() throws ZabbixAPIException {
		return this.server.getbaseauth_pass();
	}
	
	/*** 
	 * @return the API URL
	 */
	public String getAPIURL() throws ZabbixAPIException {
		return this.server.geturl();
	}

    /***
     * @return the API auth key
     */
    public String getAPIAuth() throws ZabbixAPIException {
	    return this.server.getauthkey();
    }

	/**
	 * Retrieve Application Preference
	 * @return url
	 */
	public String getServerUrlForWidget(String server_name) throws ZabbixAPIException {
		DBAdapter db = new DBAdapter(this.ctx);
		db.open();
		String serverUrl = db.getUrl(server_name);
		db.close();
		if (serverUrl.length() == 0) throw new ZabbixAPIException("Please select server");
        if (isDebug) Log.d(TAG, "getServerUrlForWidget serverUrl: "+ serverUrl);
		return serverUrl;
	}
	
	public String getLoginForWidget(String server_name) throws ZabbixAPIException {
		DBAdapter db = new DBAdapter(this.ctx);
		db.open();
		String login = db.getLogin(server_name);
		db.close();
		if (login.length() == 0) throw new ZabbixAPIException("No user");
		//Log.d(TAG, "login: "+ login);
		return login;
	}
	
	public String getPassForWidget(String server_name) throws ZabbixAPIException {
		DBAdapter db = new DBAdapter(this.ctx);
		db.open();
		String pass = db.getPassword(server_name);
		db.close();
		if (pass.length() == 0) throw new ZabbixAPIException("No pass");
		//Log.d(TAG, "pass: "+ pass);
		return pass;
	}

	/**
	 * get current server name
	 * @param
	 */
	public String getCurrentServername() {
		return this.server.getName();
	}

    /**
     * get API version
     * @param
     */
    public String getApiVersion() {
        if (isDebug) Log.d(TAG, "getApiVersion");
		return this.server.getapiversion();
    }

    /**
     * is ignoring wrong ssl cert
     * @param
     */
    public boolean isIgnoreWrongSSLCert() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        return prefs.getBoolean("ignoreWrongSSLCert", true);
    }

    /**
     * set current server
     * @param
     */
    public void setActivetServerID(Server server) {
        this.server = server;
    }

	public boolean veryfyLogedOn() throws ZabbixAPIException {
		if (this.server.isLogedOn)
			return true;
		else {
			login();
			if (this.server.isLogedOn)
				return true;
			else {
				Log.e(TAG,"Can't login to server!!!");
				return false;
			}
		}
	}

	public boolean isLogedOn(JSONObject reply) {
		JSONObject err = parseObject(reply.get("error").toString());
		int errcode = Integer.parseInt(err.get("code").toString());
		if (errcode==-32602) {
			this.server.setAuthKey(null);
			if (isDebug) Log.d(TAG, "Error: "+err+", relogining....");
			//verifyAuthKeyExist();
		}
		return true;
	}

	/**
	 * call an API function that returns a list for deleted objects
	 * @param method name of the function to call, eg. trigger.get
	 * @param params params as defined by the zabbix api
	 * @return a JSONArray with the returned objects
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public JSONArray requestObject(String method, Object params) throws ZabbixAPIException {
        if (isDebug) Log.d(TAG, "requestObject started: method "+method+", params: "+params);
		try {
			//serverUrl = getAPIURL();
			boolean isLogedOn = veryfyLogedOn();
			if (!isLogedOn) {
				if (isDebug) Log.d(TAG, "requestObject NotLoged on");
				return null;
			}
			String ret = post(makeRPCObject(method, params).toString());

			JSONObject reply = parseObject(ret);
			if (reply.containsKey("error")) {
				return parseError(reply);
			}

			String replistring = reply.get("result").toString();
			if (replistring.length() == 0 || replistring.equals("[]")) {
				return new JSONArray();
			} else {
				JSONArray array = parseArray(replistring);
				if (array == null || array.size() == 0) {
					JSONObject obj = parseObject(replistring);
					JSONArray objarray = new JSONArray();
					objarray.add(obj);
					if (isDebug) Log.d(TAG, "ParseArray to JsonObject complete");
					return objarray;
				} else
					return array;
			}
		}catch (ZabbixAPIException e) {
			return makeErrorTrigger(e.getMessage());
		} catch (Exception e) {
			throw new ZabbixAPIException(e.getMessage());
		}
	}

	/**
	 * call an API function that returns a list of objects (items, triggers, ...)
	 * @param method name of the function to call, eg. trigger.get
	 * @param params params as defined by the zabbix api
	 * @return a JSONArray with the returned objects
	 * @throws ZabbixAPIException
	*/
	public JSONArray requestObjectCount(String method, JSONObject params) throws ZabbixAPIException {
		boolean isLogedOn = veryfyLogedOn();
		if (!isLogedOn) {
			if (isDebug) Log.d(TAG, "requestObjectCount NotLoged on");
			return null;
		}

		JSONObject methodcall = makeRPCObject(method, params);
		String ret = post(methodcall.toString());

		JSONObject reply = parseObject(ret);
		if (reply.containsKey("error")) {
			parseError(reply);
		}
		
		String res = reply.get("result").toString();
		return parseArray(res);
	}

	/**
	 * generate a json rpc methodcall
	 * @param method name of the method to call
	 * @param params method params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject makeRPCObject(String method, Object params) throws ZabbixAPIException {
		JSONObject methodcall = new JSONObject();
		methodcall.put("jsonrpc", "2.0");
		methodcall.put("method", method);
        if (!method.equals("user.login")) {
            methodcall.put("auth", this.server.getauthkey());
        }
		methodcall.put("params", params);
        if (isDebug) Log.d(TAG, "makeRPCObject Method for makeRPCObject: "+method + ", Auth: "+this.server.getauthkey());
        if (isDebug) Log.d(TAG, "makeRPCObject Paramf for makeRPCObject: "+params);
		id++;
		methodcall.put("id", "" + id);

		return methodcall;
	}

	/**
	 * generate Api version
	 */
	public void setApiVersion() throws ZabbixAPIException {

		JSONObject version = new JSONObject();
		version.put("jsonrpc", "2.0");
		version.put("method", "apiinfo.version");
		id++;
		version.put("id", "" + id);

		String ret = post(version.toString());
		JSONObject reply = parseObject(ret);
		if (reply.containsKey("error")) {
				parseError(reply);
		}
		String zapiversion = reply.get("result").toString();
		this.server.setApiVersion(zapiversion);
	}
	
	public JSONArray parseError(JSONObject reply) {
		JSONObject err = parseObject(reply.get("error").toString());
		String errmsg = err.get("message").toString();
		String errdata = err.get("data").toString();
		return makeErrorTrigger(errdata);
	}

	public JSONArray makeErrorTrigger(String errdata) {
		String zabbixServer = this.server.getName();
		JSONObject obj = new JSONObject();
		obj.put("priority","5");
		obj.put("value","1");
		obj.put("description",errdata);
		obj.put("host",zabbixServer);
		obj.put("triggerid","99999");
		obj.put("error","1");
		JSONArray objarray = new JSONArray();
		objarray.add(obj);
		return objarray;
	}

	/**
	 * parses a string into a JSONArray
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray parseArray(String s) {
        if (isDebug) Log.d(TAG, "parseArray started");
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
	
	/**
	 * parses a string into a JSONArray
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject parseObject(String s) {
        if (isDebug) Log.d(TAG, "parseObject started: "+s);
		JSONObject obj = null;
		try {
			obj = (JSONObject) JSONValue.parseWithException(s);
			return obj;
		} catch (ClassCastException e) {
			JSONObject json = new JSONObject();
			json.put("ClassCastException", e);
            Log.e(TAG, "Got ClassCastException: "+e);
            return json;
		} catch (ParseException e) {
			JSONObject json = new JSONObject();
			json.put("ParseException", e);
            Log.e(TAG, "Got ParseException: "+e);
            return json;
			//Log.e(TAG, "Got ParseException: "+e);
		} catch (NullPointerException e) {
			JSONObject json = new JSONObject();
			json.put("NullPointerException", e);
            Log.e(TAG, "Got NullPointerException: "+e);
            return json;
		} catch (Exception e) {
			JSONObject json = new JSONObject();
			json.put("Exception", e);
            Log.e(TAG, "Got Exception: "+e);
            return json;
		}
	}

	/**
	 * login to the zabbix server and retrieve auth token
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public void login() throws ZabbixAPIException {
		String username = this.server.getlogin();
		String password = this.server.getpass();
		if (username.length() == 0 | password.length() ==0) {
			throw new ZabbixAPIException("You can not use empty username or password");
		}
		JSONObject logining = new JSONObject();
		logining.put("user", username);
		logining.put("password", password);
        JSONObject methodcall = makeRPCObject("user.login", logining);
        if (isDebug) Log.d(TAG, "LOGIN Methodcall: \n"+methodcall);

		String ret = post(methodcall.toString());
        if (isDebug) Log.d(TAG, "LOGIN Ret: \n"+ret);
		
		JSONObject reply = parseObject(ret); 

		boolean iferror = false;

		try {
			iferror = reply.containsKey("error");
		} catch (NullPointerException e) {
			throw new ZabbixAPIException("Can't login. Something wrong with Zabbix server or incorrect URL.");
		} catch (Exception e) {
			throw new ZabbixAPIException("Unknown error: "+ e.getMessage());
		}
		
		if (iferror) {
			parseError(reply);
		}
		try {
			String auth = reply.get("result").toString();
			//this.auth = auth;
            DBAdapter db = new DBAdapter(this.ctx);
            db.open();
            db.updateAuth(this.server,auth);
            db.close();
			this.server.setAuthKey(auth);
			this.server.setLogedOn(true);
			setApiVersion();
		} catch (Exception e) {
			this.server.setLogedOn(false);
			throw new ZabbixAPIException("Can't login. Something wrong with Zabbix server or incorrect URL.");
		}
        if (isDebug) Log.d(TAG, "Log success, got token!");
	}
 
	/**
	 * post to the zabbix api url and retrieve result
	 * @param postcontent
	 * @return
	 * @throws ZabbixAPIException
	 * @throws KeyManagementException 
	 * @throws NoSuchAlgorithmException 
	 */
	public String post(String postcontent) throws ZabbixAPIException {
        String serverUrl = this.server.geturl();
		try {
            if (isIgnoreWrongSSLCert()) {
                SSLContext sc;
                sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
            }

			URL zabbixserverurl = new URL(serverUrl);
            if (isDebug) Log.d(TAG, "post: Opening connection to " + zabbixserverurl);
			URLConnection zabbixservercon = zabbixserverurl.openConnection();
			
			if (get_base_use()) {
				String base_string = get_base_login()+":"+get_base_pass();
				zabbixservercon.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes(base_string.getBytes()));
			}

			zabbixservercon.setDoOutput(true);
			
			if (zabbixservercon instanceof HttpURLConnection) {
					((HttpURLConnection) zabbixservercon).setRequestMethod("POST");
				} else if (zabbixservercon instanceof HttpsURLConnection) {
					((HttpsURLConnection) zabbixservercon).setRequestMethod("POST");	
				}
			zabbixservercon.setRequestProperty("Content-Type", "application/json-rpc");
			zabbixservercon.setConnectTimeout(Integer.parseInt(getTimeOut())*1000);
			OutputStreamWriter out = new OutputStreamWriter(zabbixservercon.getOutputStream());
			out.write(postcontent);
			out.close();
            if (isDebug) Log.d(TAG, "post: Start reading response from server");
			BufferedReader in = new BufferedReader(new InputStreamReader(zabbixservercon.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String s = null;
			while ((s = in.readLine()) != null && sb.length()<10091872) {
				sb.append(s);
				sb.append("\n");
			}
			in.close();
			if (sb.length() == 0) {throw new ZabbixAPIException("Connection problem.\nPlease select server\nor check URL");}
		    if (isDebug) Log.d(TAG, "Post Rsponse reading complete:\n" + sb);
			return sb.toString();
			
		} catch (FileNotFoundException f) {
			if (get_base_use())
				throw new ZabbixAPIException("Wrong API URL or login/pass for basic authentication.\n Can't get data from \n"+f.getMessage()+"\n\n Try fix path or check login/password.");
			else
				throw new ZabbixAPIException("Wrong API URL. Can't get \n"+f.getMessage()+"\n\n Try fix URL path.");
		} catch (MalformedURLException m) {
			throw new ZabbixAPIException("Please check API URL");
		} catch (KeyManagementException e) {
			e.printStackTrace();
			throw new ZabbixAPIException("Key Management Exception");
		} catch (NoSuchAlgorithmException e) {
			 e.printStackTrace();
			 throw new ZabbixAPIException("No Such Algorithm Exception");
		} catch (IOException e) {
			throw new ZabbixAPIException("API Communication error: "+ e.getMessage());
		} catch (NullPointerException e) {
			throw new ZabbixAPIException("Address communication error: "+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ZabbixAPIException("Unknown error: "+ e.getMessage());
		}
	}
	
	public Bitmap getImage(String imageurl) throws ZabbixAPIException, IOException {
		try {
			SSLContext sc;
			sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());  
        
			URL url = new URL(imageurl);
			URLConnection connection  = (URLConnection) url.openConnection();
			if (connection instanceof HttpURLConnection) {
				((HttpURLConnection) connection).setRequestMethod("GET");
			} else if (connection instanceof HttpsURLConnection) {
				((HttpsURLConnection) connection).setRequestMethod("GET");	
			}
			if (get_base_use()) {
				String base_string = get_base_login()+":"+get_base_pass();
				connection.setRequestProperty("Authorization", "basic " + Base64.encodeBytes(base_string.getBytes()));
			}
			connection.addRequestProperty("Cookie", "zbx_sessionid="+this.server.getauthkey());
			InputStream is = connection.getInputStream();
			return BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			throw new ZabbixAPIException("Unknown error: "+ e.getMessage());
		}
	}
}

class MyHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
            return true;
    }
}
class MyTrustManager implements X509TrustManager {	 
    public void checkClientTrusted(X509Certificate[] chain, String authType) {

    }
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }
    public X509Certificate[] getAcceptedIssuers() {
            return null;
    }
}
