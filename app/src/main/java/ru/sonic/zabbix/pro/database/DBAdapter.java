package ru.sonic.zabbix.pro.database;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Graph;
import ru.sonic.zabbix.pro.base.Server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DBAdapter {
	private static final String TAG = "DBAdapter";
    long id = 0;
    /* Main Zabbix server table keys*/
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TIMEOUT = "Timeout";
    public static final String KEY_SERVER = "Server";
    public static final String KEY_URL = "ServerUrl";
    public static final String KEY_USER = "User";
    public static final String KEY_PASS = "Pass";
    public static final String KEY_BASE_USE = "Base_auth_use";
    public static final String KEY_BASE_LOGIN = "Base_auth_login";
    public static final String KEY_BASE_PASS = "Base_auth_pass";
    public static final String KEY_AUTH = "authkey";
    public static final String KEY_API_VERSION = "api_version";
    
    /* Favorite graphs table keys*/
    public static final String KEY_GRAPH_ID = "graphid";
    public static final String KEY_GRAPH_NAME = "number";
    public static final String KEY_GRAPH_WIDTH = "width";
    public static final String KEY_GRAPH_HEIGHT = "height";
    public static final String KEY_GRAPH_TYPE = "graphtype";
    public static final String KEY_GRAPH_HOSTNAME = "hostname";
    public static final String KEY_GRAPH_SERVERNAME = "server_name";
    public static final String KEY_GRAPH_URL = "url";

    /* Active triggers table keys*/
    public static final String KEY_TRIGGER_ID = "triggerid";
    public static final String KEY_TRIGGER_DESCRIPTION = "description";
    public static final String KEY_TRIGGER_VALUE = "value";
    public static final String KEY_TRIGGER_HOST = "host";
    public static final String KEY_TRIGGER_PRIORITY = "priority";
    public static final String KEY_TRIGGER_LASTCHANGE = "lastchange";
    public static final String KEY_TRIGGER_TYPE = "type";
    public static final String KEY_TRIGGER_COMMENTS = "comments";
    public static final String KEY_TRIGGER_ERROR = "error";
    public static final String KEY_TRIGGER_URL = "url";
    public static final String KEY_TRIGGER_HOSTID = "hostid";
    
    private static final String DATABASE_NAME = "zabbix";
    private static final String DATABASE_SERVERS_TABLE = "servers";
    private static final String DATABASE_FGRAPHS_TABLE = "favoritegraphs";
    private static final String DATABASE_ACTIVETRIGGERS_TABLE = "active_triggers";
    private static final int DATABASE_VERSION = 5;

    private static final String DATABASE_CREATE_SERVERS_TABLE =
    		"create table "
    		+ DATABASE_SERVERS_TABLE + " (_id integer primary key autoincrement, "
    		+ KEY_SERVER + " TEXT, " 
    		+ KEY_URL + " TEXT, " 
    		+ KEY_USER + " TEXT, " 
    		+ KEY_PASS + " TEXT, " 
    		+ KEY_TIMEOUT + " TEXT, "
            + KEY_AUTH + " TEXT DEFAULT '', "
            + KEY_API_VERSION + " TEXT DEFAULT '', "
    		+ KEY_BASE_USE + " INTEGER NOT NULL DEFAULT 0, " 
    		+ KEY_BASE_LOGIN + " TEXT NOT NULL DEFAULT '', " 
    		+ KEY_BASE_PASS + " TEXT NOT NULL DEFAULT '')";
    
    private static final String DATABASE_CREATE_FGRAPHS_TABLE =
    	     "create table "
    	     + DATABASE_FGRAPHS_TABLE + " ("+KEY_GRAPH_ID+" integer primary key autoincrement, "
    	     + KEY_GRAPH_NAME + " TEXT NOT NULL DEFAULT '', " 
    	     + KEY_GRAPH_WIDTH + " TEXT NOT NULL DEFAULT '', " 
    	     + KEY_GRAPH_HEIGHT + " TEXT NOT NULL DEFAULT '', " 
    	     + KEY_GRAPH_TYPE + " TEXT NOT NULL DEFAULT '', " 
    	     + KEY_GRAPH_HOSTNAME + " TEXT NOT NULL DEFAULT '', " 
    	     + KEY_GRAPH_SERVERNAME + " TEXT NOT NULL DEFAULT '', "
    	     + KEY_GRAPH_URL + " TEXT NOT NULL DEFAULT '')";
    
    private static final String DATABASE_CREATE_ACTIVETRIGGERS_TABLE =
   	     "create table "
   	     + DATABASE_ACTIVETRIGGERS_TABLE + " ("+KEY_TRIGGER_ID+" integer primary key autoincrement, "
   	     + KEY_TRIGGER_DESCRIPTION + " TEXT NOT NULL DEFAULT '', " 
   	     + KEY_TRIGGER_VALUE + " TEXT NOT NULL DEFAULT '', " 
   	     + KEY_TRIGGER_HOST + " TEXT NOT NULL DEFAULT '', " 
   	     + KEY_TRIGGER_PRIORITY + " TEXT NOT NULL DEFAULT '', " 
   	     + KEY_TRIGGER_LASTCHANGE + " TEXT NOT NULL DEFAULT '', " 
   	     + KEY_TRIGGER_TYPE + " TEXT NOT NULL DEFAULT '', "
   	     + KEY_TRIGGER_ERROR + " TEXT NOT NULL DEFAULT '', "
   	     + KEY_TRIGGER_URL + " TEXT NOT NULL DEFAULT '', "
   	     + KEY_TRIGGER_HOSTID + " TEXT NOT NULL DEFAULT '', "
   	     + KEY_TRIGGER_COMMENTS + " TEXT NOT NULL DEFAULT '')";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
 
	private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
 
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_SERVERS_TABLE);
            db.execSQL(DATABASE_CREATE_FGRAPHS_TABLE);
            db.execSQL(DATABASE_CREATE_ACTIVETRIGGERS_TABLE);
			
			ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_SERVER, "Zabbix server");
	        initialValues.put(KEY_URL, "http://example.com/zabbix/api_jsonrpc.php");
	        initialValues.put(KEY_USER, "user");
	        initialValues.put(KEY_PASS, "");
	        initialValues.put(KEY_TIMEOUT, "10");
	        initialValues.put(KEY_BASE_USE, 0);
	        initialValues.put(KEY_BASE_LOGIN, "");
	        initialValues.put(KEY_BASE_PASS, "");
            initialValues.put(KEY_API_VERSION, "");
	        db.insert(DATABASE_SERVERS_TABLE, null, initialValues);
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
        	if (oldVersion == 1) {
        		db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD "+KEY_BASE_USE+" INTEGER NOT NULL DEFAULT 0 ;");
        		db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD "+KEY_BASE_LOGIN+" TEXT NOT NULL DEFAULT '';");
        		db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD "+KEY_BASE_PASS + " TEXT NOT NULL DEFAULT '';");
                db.execSQL(DATABASE_CREATE_FGRAPHS_TABLE);
                db.execSQL(DATABASE_CREATE_ACTIVETRIGGERS_TABLE);
                db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD " + KEY_AUTH + " TEXT DEFAULT '';");
                db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD " + KEY_API_VERSION + " TEXT DEFAULT '';");
        	}else if (oldVersion == 2) {
        		db.execSQL(DATABASE_CREATE_FGRAPHS_TABLE);
        		db.execSQL(DATABASE_CREATE_ACTIVETRIGGERS_TABLE);
                db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD " + KEY_AUTH + " TEXT DEFAULT '';");
                db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD " + KEY_API_VERSION + " TEXT DEFAULT '';");
        	} else if (oldVersion == 3) {
                db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD " + KEY_AUTH + " TEXT DEFAULT '';");
                db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD " + KEY_API_VERSION + " TEXT DEFAULT '';");
            } else if (oldVersion == 4) {
                db.execSQL("ALTER TABLE " + DATABASE_SERVERS_TABLE + " ADD " + KEY_API_VERSION + " TEXT DEFAULT '';");
        	} else {
                db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SERVERS_TABLE);
                onCreate(db);
        	}
        }
    }

    public DBAdapter open() {
    	try {
    		db = DBHelper.getWritableDatabase();
    		return this;
    	} catch (Exception e) {
    		return null;
    	}
    }
 
    public void close(){
        DBHelper.close();
    }

    public void flush() {
    	try {
    		db.execSQL("DELETE FROM " + DATABASE_SERVERS_TABLE);
    	} catch (Exception e) {
    	}
    }
    
    public long insertServer(String server, String url, String user, String pass, String timeout, int base_use, String base_login, String base_pass) {
    	try {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_SERVER, server);
	        initialValues.put(KEY_URL, url);
	        initialValues.put(KEY_USER, user);
	        initialValues.put(KEY_PASS, pass);
	        initialValues.put(KEY_TIMEOUT, timeout);
	        initialValues.put(KEY_BASE_USE, base_use);
	        initialValues.put(KEY_BASE_LOGIN, base_login);
	        initialValues.put(KEY_BASE_PASS, base_pass);
	        return db.insert(DATABASE_SERVERS_TABLE, null, initialValues);
    	} catch (Exception e) {
    		return -1;
    	}
    }
    
	public boolean updateServer(String oldname, String server, String url, String user, String pass, String timeout, int base_use, String base_login, String base_pass) {
		try {
				ContentValues initialValues = new ContentValues();
		        initialValues.put(KEY_SERVER, server);
		        initialValues.put(KEY_URL, url);
		        initialValues.put(KEY_USER, user);
		        initialValues.put(KEY_PASS, pass);
		        initialValues.put(KEY_TIMEOUT, timeout);
		        initialValues.put(KEY_BASE_USE, base_use);
		        initialValues.put(KEY_BASE_LOGIN, base_login);
		        initialValues.put(KEY_BASE_PASS, base_pass);
                //initialValues.put(KEY_API_VERSION, api_version);
				return db.update(DATABASE_SERVERS_TABLE, initialValues, KEY_SERVER + "= '" + oldname + "'", null) > 0;
		} catch (Exception e) {
			return false;
		}
	}
        
    /*public long getAllServers() {
        return DatabaseUtils.queryNumEntries(db,DATABASE_SERVERS_TABLE);
    } */

    public List<Server> getAllServers() {
        List<Server> allServers = new ArrayList<>();
        List<String> serverNames = selectServerNames();
        int n = 0;
        for (n=0;n<serverNames.size();n++) {
            String srvName = serverNames.get(n);
            Server srv = selectServer(srvName);
            allServers.add(srv);
        }
        return allServers;
    }

    public Server selectServer(String serverName) {
        Server zabbixServer = new Server();
        //Cursor cursor = this.db.query(DATABASE_TABLE, new String[] { KEY_SERVER, KEY_URL, KEY_USER, KEY_PASS, KEY_TIMEOUT },
        //		  KEY_SERVER, new String[] { serverName }, null, null, "_id");
    	Cursor cursor = this.db.rawQuery("SELECT * FROM "+ DATABASE_SERVERS_TABLE + " WHERE " + KEY_SERVER + " = '" + serverName+ "' limit 1", null);
          int serverCol = cursor.getColumnIndex(KEY_SERVER);
          int urlCol = cursor.getColumnIndex(KEY_URL);
          int userCol = cursor.getColumnIndex(KEY_USER);
          int passCol = cursor.getColumnIndex(KEY_PASS);
          int timeoutCol = cursor.getColumnIndex(KEY_TIMEOUT);
          int base_useCol = cursor.getColumnIndex(KEY_BASE_USE);
          int base_loginCol = cursor.getColumnIndex(KEY_BASE_LOGIN);
          int base_passCol = cursor.getColumnIndex(KEY_BASE_PASS);
          int authKey = cursor.getColumnIndex(KEY_AUTH);
          int apiversion = cursor.getColumnIndex(KEY_API_VERSION);
          if (cursor.moveToFirst()) {
             do {
                     zabbixServer.put("servername",cursor.getString(serverCol));
                     zabbixServer.put("url",cursor.getString(urlCol));
                     zabbixServer.put("login",cursor.getString(userCol));
                     zabbixServer.put("pass",cursor.getString(passCol));
                     zabbixServer.put("api_version",cursor.getString(passCol));
                     zabbixServer.put("timeout",cursor.getString(timeoutCol));
                if (cursor.getInt(base_useCol) == 0)
                    zabbixServer.put("baseauth_use",false);
                else
                    zabbixServer.put("baseauth_use",true);

                    zabbixServer.put("baseauth_login",cursor.getString(base_loginCol));
                    zabbixServer.put("baseauth_pass",cursor.getString(base_passCol));
                    zabbixServer.put("authkey",cursor.getString(authKey));
                    zabbixServer.put("api_version",cursor.getString(apiversion));
             } while (cursor.moveToNext());
          }
          if (cursor != null && !cursor.isClosed()) {
             cursor.close();
          }
        return zabbixServer;
    }
    
    public String getUrl(String serverName) {
       	Cursor cursor = this.db.rawQuery("SELECT " + KEY_URL + " FROM " + DATABASE_SERVERS_TABLE + " WHERE " + KEY_SERVER + " = '" + serverName + "'", null);
        int urlCol = cursor.getColumnIndex(KEY_URL);
        String serverUrl = "";
              if (cursor.moveToFirst()) {
                 do {
                	 serverUrl = cursor.getString(urlCol);
                 } while (cursor.moveToNext());
              }
              if (cursor != null && !cursor.isClosed()) {
                 cursor.close();
              }
        return serverUrl;
    }
    
    public String getLogin(String serverName) {
       	Cursor cursor = this.db.rawQuery("SELECT " + KEY_USER + " FROM "+ DATABASE_SERVERS_TABLE + " WHERE "+ KEY_SERVER + " = '" + serverName+ "'", null);
        int userCol = cursor.getColumnIndex(KEY_USER);
        String user = "";
              if (cursor.moveToFirst()) {
                 do {
                	 user = cursor.getString(userCol);
                 } while (cursor.moveToNext());
              }
              if (cursor != null && !cursor.isClosed()) {
                 cursor.close();
              }
        return user;
    }
    
    public String getPassword(String serverName) {
       	Cursor cursor = this.db.rawQuery("SELECT " + KEY_PASS + " FROM "+ DATABASE_SERVERS_TABLE + " WHERE "+ KEY_SERVER + " = '" + serverName+ "'", null);
        int passCol = cursor.getColumnIndex(KEY_PASS);
        String pass = "";
              if (cursor.moveToFirst()) {
                 do {
                	 pass = cursor.getString(passCol);
                 } while (cursor.moveToNext());
              }
              if (cursor != null && !cursor.isClosed()) {
                 cursor.close();
              }
        return pass;
    }
    
    public int get_use_base_auth(String serverName) {
       	Cursor cursor = this.db.rawQuery("SELECT " + KEY_BASE_USE + " FROM "+ DATABASE_SERVERS_TABLE + " WHERE "+ KEY_SERVER + " = '" + serverName+ "'", null);
        int passCol = cursor.getColumnIndex(KEY_PASS);
        int pass = 0;
              if (cursor.moveToFirst()) {
                 do {
                	 pass = cursor.getInt(passCol);
                 } while (cursor.moveToNext());
              }
              if (cursor != null && !cursor.isClosed()) {
                 cursor.close();
              }
        return pass;
    }
    
    public List<String> selectServerNames() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = this.db.query(DATABASE_SERVERS_TABLE, new String[]{KEY_SERVER},
                null, null, null, null, "_id");
        int serverCol = cursor.getColumnIndex(KEY_SERVER);
        if (cursor.moveToFirst()) {
           do {
              list.add(cursor.getString(serverCol));;
           } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        return list;
    }

    public String getFirstServerName() {
        String namesrv = "";
        Cursor cursor = this.db.query(DATABASE_SERVERS_TABLE, new String[] { KEY_SERVER },null, null, null, null, "_id");
        int serverCol = cursor.getColumnIndex(KEY_SERVER);
        if (cursor.moveToFirst()) {
            namesrv=cursor.getString(serverCol);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        //Log.d(TAG,"getFirstServerName: "+namesrv);
        return namesrv;
    }

    public String getAuth(String serverName) {
        Cursor cursor = this.db.rawQuery("SELECT " + KEY_AUTH + " FROM "+ DATABASE_SERVERS_TABLE + " WHERE "+ KEY_SERVER + " = '" + serverName+ "'", null);
        int userCol = cursor.getColumnIndex(KEY_AUTH);
        String authkey = "";
        if (cursor.moveToFirst()) {
            do {
                authkey = cursor.getString(userCol);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return authkey;
    }

    public boolean updateAuth(Server server, String authkey) {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_AUTH, authkey);
            return db.update(DATABASE_SERVERS_TABLE, initialValues, KEY_SERVER + "= '" + server + "'", null) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void deleteServer(String serverName) {
    	db.execSQL("DELETE FROM " + DATABASE_SERVERS_TABLE + " WHERE " + KEY_SERVER + " = '" + serverName+"'");
    }

    public void deleteAllServers() {
        db.execSQL("DELETE FROM " + DATABASE_SERVERS_TABLE);
    }
    
    /* Database for fovorite graphs methods*/
    public void insertFavoriteGraph(Graph graph, String hostname, String server_name) {
    	try {
    		ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_GRAPH_ID, graph.getID());
	        initialValues.put(KEY_GRAPH_NAME, graph.getName());
	        initialValues.put(KEY_GRAPH_WIDTH, graph.getwidth());
	        initialValues.put(KEY_GRAPH_HEIGHT, graph.getheight());
	        initialValues.put(KEY_GRAPH_TYPE, graph.getgraphtype());
	        initialValues.put(KEY_GRAPH_HOSTNAME, hostname);
	        initialValues.put(KEY_GRAPH_SERVERNAME, server_name);
	        db.insert(DATABASE_FGRAPHS_TABLE, null, initialValues);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public List<Graph> getFavoriteGraphs() {
    	List<Graph> favoriteGraphs = new ArrayList<Graph>();
    	try {
    		Cursor cursor = this.db.query(DATABASE_FGRAPHS_TABLE, null, null, null, null, null, null);
    		        int graphidCol = cursor.getColumnIndex(KEY_GRAPH_ID);
    		        int graphnameCol = cursor.getColumnIndex(KEY_GRAPH_NAME);
    		        int graphwidthCol = cursor.getColumnIndex(KEY_GRAPH_WIDTH);
    		        int graphheightCol = cursor.getColumnIndex(KEY_GRAPH_HEIGHT);
    		        int graphtypeCol = cursor.getColumnIndex(KEY_GRAPH_TYPE);
    		        int graphhostnameCol = cursor.getColumnIndex(KEY_GRAPH_HOSTNAME);
    		        //int graphservernameCol = cursor.getColumnIndex(KEY_GRAPH_SERVERNAME);
    		        if (cursor.moveToFirst()) {
    		           do {
    		        	   Log.d(TAG,"Get graph: "+cursor.getString(graphidCol));
    		        	   String graphid = cursor.getString(graphidCol);
    		        	   String name = cursor.getString(graphnameCol);
    		        	   String width = cursor.getString(graphwidthCol);
    		        	   String height = cursor.getString(graphheightCol);
    		        	   String type = cursor.getString(graphtypeCol);
    		        	   String hostname = cursor.getString(graphhostnameCol);
    		        	   //String server_name = cursor.getString(graphservernameCol);
    		        	   Graph graph = new Graph(graphid,name,width,height,type,hostname);
    		        	   favoriteGraphs.add(graph);
    		           } while (cursor.moveToNext());
    		        }
    		        if (cursor != null && !cursor.isClosed()) {
    		           cursor.close();
    		        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return favoriteGraphs;
    }
    
    public void delFavoriteGraph(Graph graph) {
    	db.execSQL("DELETE FROM " + DATABASE_FGRAPHS_TABLE + " WHERE " + KEY_GRAPH_ID + " = '" + graph.getID()+"'");
    }
}