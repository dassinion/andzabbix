package ru.sonic.zabbix.pro.ZControl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.DiffAdapter;
import ru.sonic.zabbix.pro.adapters.MediaAdapter;
import ru.sonic.zabbix.pro.adapters.UserGroupAdapter;
import ru.sonic.zabbix.pro.api.MediatypeApiHandler;
import ru.sonic.zabbix.pro.api.UserApiHandler;
import ru.sonic.zabbix.pro.api.UserGroupApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Media;
import ru.sonic.zabbix.pro.base.Mediatype;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.User;
import ru.sonic.zabbix.pro.base.UserGroup;
import ru.sonic.zabbix.pro.database.DBAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class UserCreateActivity extends Activity {
	private UserApiHandler uapi = null;
	private UserGroupApiHandler gapi = null;
	private MediatypeApiHandler mapi = null;
	User usr;
	List<UserGroup> UserGroups;
	List<Mediatype> Mediatypes;
	List<Media> Medias;
	String action;
	protected static final int MSG_ERROR = 0;
	protected static final int MSG_GROUP_RETRIEVED = 1;
	protected static final int MSG_LOAD_MEDIA = 2;
	protected static final int MSG_USER_RETRIEVED = 3;
	protected static final int MSG_GROUP_NU_RETRIEVED = 4;
	/* Execute */
	protected static final int MSG_EXEC_OP = 5; 
	protected static final int MSG_MEDIA_EXEC_OP = 6;
	protected static final String TAG = "UserCreateActivity";
	View mailpage;
	View groupspage;
	ListView groupsAvail;
	ListView groupsAdded;
	ArrayList<UserGroup> groupslistAvail;
	ArrayList<UserGroup> groupslistAdded;
	UserGroupAdapter listArrayAdapterAvail;
	UserGroupAdapter listArrayAdapterAdded;
	
	ListView mediaslistview;
	ArrayList<Media> mediaarray;
	MediaAdapter mediaadapter;
	
	public Context ctx;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.ctx = this;
		Server selectedZabbixServer = getSelectedServer();
		uapi = new UserApiHandler(this,selectedZabbixServer);
		gapi = new UserGroupApiHandler(this,selectedZabbixServer);
		mapi = new MediatypeApiHandler(this,selectedZabbixServer);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> pages = new ArrayList<View>();

		mailpage = inflater.inflate(R.layout.user_create, null);
		pages.add(mailpage);

		groupspage = inflater.inflate(R.layout.user_create_groups, null);
		pages.add(groupspage);

		DiffAdapter pagerAdapter = new DiffAdapter(pages);
		ViewPager viewPager = new ViewPager(this);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);

		setContentView(viewPager);
		
		groups_mtypes_config();

		String userid = "";
		Button save = (Button) mailpage.findViewById(R.id.userCreateSave);

		try {
			Bundle extras = getIntent().getExtras();
			userid = extras.getString("userid");
			action = extras.getString("action");
			setProgressBarIndeterminateVisibility(true);
			executeop("load_media", null);
			executeop("getusr", userid);
			if (action.equals("edit")) {
				save.setText(R.string.save);
				save.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						edit(usr);
					};
				});
			} else {
				save.setText(R.string.create);
				save.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						create();
					};
				});
			}
		} catch (Exception e) {
			executeop("load_media", null);
			executeop("getgroups_nu", null);
			save.setText(R.string.create);
			save.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					create();
				};
			});
		}

		Button cancel = (Button) mailpage.findViewById(R.id.userCreateCancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			};
		});
		/*
		Button addmedia = (Button) groupspage.findViewById(R.id.addmedia);
		addmedia.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				JSONArray ujson = new JSONArray();
				JSONObject json = new JSONObject();
				JSONObject mtype = new JSONObject();
				mtype.put("mediatypeid", "1");
				mtype.put("sendto", "meandz");
				mtype.put("active", "1");
				mtype.put("severity", "0");
				mtype.put("period", "1-7,00:00-24:00");
				ujson.add(mtype);
				json.put("medias", ujson);
				executeop("add_media", json);
			};
		});
		*/
	}

	private void groups_mtypes_config() {
		/*Groups*/
		groupslistAvail = new ArrayList<UserGroup>();
		groupslistAdded = new ArrayList<UserGroup>();
		listArrayAdapterAvail = new UserGroupAdapter(this, groupslistAvail);
		listArrayAdapterAdded = new UserGroupAdapter(this, groupslistAdded);
		
		groupsAvail = (ListView) groupspage.findViewById(R.id.groupsavail);
		groupsAvail.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				UserGroup gr = (UserGroup)(listArrayAdapterAvail.getItem(position));
				groupslistAvail.remove(gr);
				groupslistAdded.add(gr);
				listArrayAdapterAvail.notifyDataSetChanged();
				listArrayAdapterAdded.notifyDataSetChanged();
			}
		}); 
		groupsAdded = (ListView) groupspage.findViewById(R.id.groupsadded);
		groupsAdded.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				UserGroup gr = (UserGroup)(listArrayAdapterAdded.getItem(position));
				groupslistAdded.remove(gr);
				groupslistAvail.add(gr);
				listArrayAdapterAvail.notifyDataSetChanged();
				listArrayAdapterAdded.notifyDataSetChanged();
			}
		});
		
		groupsAvail.setAdapter(listArrayAdapterAvail);
		groupsAdded.setAdapter(listArrayAdapterAdded);
		
		/*Medias*/
		mediaarray = new ArrayList<Media>();
		mediaadapter = new MediaAdapter(this, mediaarray);
		
		mediaslistview = (ListView) groupspage.findViewById(R.id.medias);
		/*
		mediaslistview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {    
				menu.setHeaderTitle(R.string.contentMenu);
	            menu.add(0, 0, 0, "delete");
	            }
			});
			*/
		mediaslistview.setAdapter(mediaadapter);
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	ListView lv=(ListView)groupspage.findViewById(R.id.medias);
    	final Media obj=(Media)(lv.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
    	case 0:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(R.string.are_you_sure)
    		.setTitle(R.string.delete_media)
            .setCancelable(false)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	executeop("delete_media", obj);
                }
            })
    		
    		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
    		});
    		
    		AlertDialog alert = builder.create();
    		alert.show();
    		break;
    	default:
    		break;
    	}
    	return true;
	}
	
	public void add_media(final Mediatype obj) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.add_media)
		.setTitle(R.string.add_media)
        .setCancelable(false)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	executeop("add_media", obj);
            }
        })
		
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
		});
		
		AlertDialog alert = builder.create();
		alert.show();
	}

	@SuppressWarnings("unchecked")
	protected void loadusr(User user) {
		for (Iterator<UserGroup> it = UserGroups.iterator(); it.hasNext();) {
			JSONObject sonobj = (JSONObject) it.next();
			UserGroup gr = new UserGroup(sonobj);
			groupslistAvail.add(gr);
		}

		for (Iterator<UserGroup> it = user.getGroups().iterator(); it.hasNext();) {
			JSONObject sonobj = (JSONObject) it.next();
			UserGroup gr = new UserGroup(sonobj);
			groupslistAdded.add(gr);
			groupslistAvail.remove(gr);
		}
		
		for (Iterator<Media> it = user.getMedias().iterator(); it.hasNext();) {
			JSONObject sonobj = (JSONObject) it.next();
			mediaarray.add(new Media(sonobj));
		}
		mediaadapter.notifyDataSetChanged();

		listArrayAdapterAvail.notifyDataSetChanged();
		listArrayAdapterAdded.notifyDataSetChanged();

		TextView tw = (TextView) mailpage.findViewById(R.id.userCreateAlias);
		if (action.equals("edit"))
			tw.setText(user.getalias());
		else
			tw.setText(R.string.copy);

		tw = (TextView) mailpage.findViewById(R.id.userCreateName);
		tw.setText(user.getName());

		tw = (TextView) mailpage.findViewById(R.id.userCreateSurname);
		tw.setText(user.getsurname());

		tw = (TextView) mailpage.findViewById(R.id.userCreatePass);
		tw.setText("");

		tw = (TextView) mailpage.findViewById(R.id.userCreateLogoutTime);
		tw.setText(user.getautologout());

		tw = (TextView) mailpage.findViewById(R.id.userCreateUrl);
		tw.setText(user.geturl());

		tw = (TextView) mailpage.findViewById(R.id.userCreateRefresh);
		tw.setText(user.getrefresh());

		tw = (TextView) mailpage.findViewById(R.id.userCreateRows);
		tw.setText(user.getrows_per_page());

		Spinner lang = (Spinner) mailpage.findViewById(R.id.userCreateLang);
		String[] elements = getResources().getStringArray(
				R.array.UserLanguagesVal);
		for (int i = 0; i < elements.length - 1; i++) {
			if (elements[i].equals(user.getlang())) {
				lang.setSelection(i);
				break;
			}
		}
		
		Spinner types = (Spinner) mailpage.findViewById(R.id.userCreateType);
		String[] telements = getResources().getStringArray(
				R.array.UserTypesVal);
		for (int i = 0; i < telements.length - 1; i++) {
			if (telements[i].equals(user.gettype())) {
				types.setSelection(i);
				break;
			}
		}
	}

	public void defnewuser() {
		for (Iterator<UserGroup> it1 = UserGroups.iterator(); it1.hasNext();) {
			JSONObject sonobj = (JSONObject) it1.next();
			UserGroup gr = new UserGroup(sonobj);
			groupslistAvail.add(gr);
		}
		
		listArrayAdapterAvail.notifyDataSetChanged();

		TextView tw = (TextView) mailpage.findViewById(R.id.userCreateAlias);
		tw.setText("");

		tw = (TextView) mailpage.findViewById(R.id.userCreateName);
		tw.setText("");

		tw = (TextView) mailpage.findViewById(R.id.userCreateSurname);
		tw.setText("");

		tw = (TextView) mailpage.findViewById(R.id.userCreatePass);
		tw.setText("");

		tw = (TextView) mailpage.findViewById(R.id.userCreateLogoutTime);
		tw.setText("900");

		tw = (TextView) mailpage.findViewById(R.id.userCreateUrl);
		tw.setText("");

		tw = (TextView) mailpage.findViewById(R.id.userCreateRefresh);
		tw.setText("30");

		tw = (TextView) mailpage.findViewById(R.id.userCreateRows);
		tw.setText("50");
	}

	@SuppressWarnings("unchecked")
	public void create() {
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
		
		JSONArray grouparray = new JSONArray();
		for (Iterator<UserGroup> it1 = groupslistAdded.iterator(); it1.hasNext();) {
			JSONObject sonobj = (JSONObject) it1.next();
			UserGroup gr = new UserGroup(sonobj);
			JSONObject grobj = new JSONObject();
			grobj.put("usrgrpid", gr.getID());
			grouparray.add(grobj);
		}
		//if (grouparray.size()==0) {
		//	displayPopup("Error","No groups for user.\nPlease add group on second page");
		//	return;
		//}
		params.put("usrgrps", grouparray);

		TextView tw = (TextView) mailpage.findViewById(R.id.userCreateAlias);
		params.put("alias", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreateName);
		params.put("name", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreateSurname);
		params.put("surname", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreatePass);
		params.put("passwd", tw.getText().toString());
		CheckBox autologout = (CheckBox) mailpage
				.findViewById(R.id.userCreateALogin);
		if (autologout.isChecked()) {
			tw = (TextView) mailpage.findViewById(R.id.userCreateLogoutTime);
			params.put("autologout", tw.getText().toString());
		}

		tw = (TextView) mailpage.findViewById(R.id.userCreateUrl);
		params.put("url", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreateRefresh);
		params.put("refresh", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreateRows);
		params.put("rows_per_page", tw.getText().toString());

		CheckBox autologin = (CheckBox) mailpage
				.findViewById(R.id.userCreateALogin);
		if (autologin.isChecked())
			params.put("autologin", "1");
		else
			params.put("autologin", "0");

		Spinner lang = (Spinner) mailpage.findViewById(R.id.userCreateLang);
		int pos = lang.getSelectedItemPosition();
		params.put("lang",
				getResources().getStringArray(R.array.UserLanguagesVal)[pos]);

		Spinner types = (Spinner) mailpage.findViewById(R.id.userCreateType);
		int typepos = types.getSelectedItemPosition();
		params.put("type",
				getResources().getStringArray(R.array.UserTypesVal)[typepos]);
		
		//params.put("theme", "css_ob.css");
		params.put("attempt_failed", "0");
		params.put("attempt_ip", "");
		params.put("attempt_clock", "0");

		//Log.d("Creating", "User params added");
		User user = new User(params);
		executeop("create", user);
	}

	@SuppressWarnings("unchecked")
	public void edit(User user) {
		User editeduser = user;

		JSONArray grouparray = new JSONArray();
		for (Iterator<UserGroup> it1 = groupslistAdded.iterator(); it1.hasNext();) {
			JSONObject sonobj = (JSONObject) it1.next();
			UserGroup gr = new UserGroup(sonobj);
			JSONObject grobj = new JSONObject();
			grobj.put("usrgrpid", gr.getID());
			grouparray.add(grobj);
		}
		//if (grouparray.size()==0) {
		//	displayPopup("Error","User cannot be without user group. Aplease add group on second window");
		//	return;
		//}
		editeduser.put("usrgrps", grouparray);
		
		TextView tw = (TextView) mailpage.findViewById(R.id.userCreateAlias);
		editeduser.put("alias", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreateName);
		editeduser.put("name", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreateSurname);
		editeduser.put("surname", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreatePass);
		if (tw.getText().toString() != null
				&& !tw.getText().toString().equals(""))
			editeduser.put("passwd", tw.getText().toString());

		CheckBox autologout = (CheckBox) mailpage
				.findViewById(R.id.userCreateALogin);
		if (autologout.isChecked()) {
			tw = (TextView) mailpage.findViewById(R.id.userCreateLogoutTime);
			editeduser.put("autologout", tw.getText().toString());
		}

		tw = (TextView) mailpage.findViewById(R.id.userCreateUrl);
		editeduser.put("url", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreateRefresh);
		editeduser.put("refresh", tw.getText().toString());

		tw = (TextView) mailpage.findViewById(R.id.userCreateRows);
		editeduser.put("rows_per_page", tw.getText().toString());

		// params.put("usrgrps", grouparray);
		CheckBox autologin = (CheckBox) mailpage
				.findViewById(R.id.userCreateALogin);
		if (autologin.isChecked())
			editeduser.put("autologin", "1");
		else
			editeduser.put("autologin", "0");

		Spinner lang = (Spinner) mailpage.findViewById(R.id.userCreateLang);
		int pos = lang.getSelectedItemPosition();
		editeduser.put("lang",
				getResources().getStringArray(R.array.UserLanguagesVal)[pos]);
		
		Spinner types = (Spinner) mailpage.findViewById(R.id.userCreateType);
		int typepos = types.getSelectedItemPosition();
		editeduser.put("type",
				getResources().getStringArray(R.array.UserTypesVal)[typepos]);

		executeop("edit", editeduser);
	}

	/**
	 * start thread to refresh the data from the server
	 * 
	 * @throws ZabbixAPIException
	 */
	public void executeop(final String operation, final Object obj) {
		Thread background = new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					if (operation.equals("getusr")) {
						msg.arg1 = MSG_USER_RETRIEVED;
						msg.obj = uapi.getInfo((String) obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("getgroups")) {
						msg.arg1 = MSG_GROUP_RETRIEVED;
						msg.obj = gapi.get();
						exechandler.sendMessage(msg);
					} else if (operation.equals("getgroups_nu")) { 
						msg.arg1 = MSG_GROUP_NU_RETRIEVED;
						msg.obj = gapi.get();
						exechandler.sendMessage(msg);
					} else if (operation.equals("load_media")) {
						msg.arg1 = MSG_LOAD_MEDIA;
						msg.obj = mapi.get();
						exechandler.sendMessage(msg);
					} else if (operation.equals("create")) {
						msg.arg1 = MSG_EXEC_OP;
						msg.obj = uapi.create((User) obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("edit")) {
						msg.arg1 = MSG_EXEC_OP;
						msg.obj = uapi.edit((User) obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("delete_media")) {
						msg.arg1 = MSG_MEDIA_EXEC_OP;
						msg.obj = uapi.media_delete((Media) obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("add_media")) {
						msg.arg1 = MSG_MEDIA_EXEC_OP;
						msg.obj = uapi.media_add(usr,obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("edit_media")) {
						msg.arg1 = MSG_MEDIA_EXEC_OP;
						msg.obj = uapi.media_edit(usr, obj);
						exechandler.sendMessage(msg);
					}
				} catch (ZabbixAPIException e) {
					Log.e(TAG, "RPC Call failed: " + e);
					msg.arg1 = MSG_ERROR;
					msg.obj = e;
					exechandler.sendMessage(msg);
				}
			}
		});
		background.start();
	}

	/**
	 * the retriever thread cannot modify the gui after it's done Therefore it
	 * justs sends the information (data or error) to the handler which again
	 * does the gui stuff
	 */
	private Handler exechandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case MSG_ERROR:
				displayPopup(R.string.error,
						((ZabbixAPIException) msg.obj).getMessage());
				break;
			case MSG_USER_RETRIEVED:
				JSONObject obj = (JSONObject) ((JSONArray) msg.obj).get(0);
				usr = new User(obj);
				executeop("getgroups", null);
				break;
			case MSG_LOAD_MEDIA:
				Mediatypes = (List<Mediatype>) msg.obj;
				break;
			case MSG_GROUP_RETRIEVED:
				UserGroups = (List<UserGroup>) msg.obj;
				try {
					setProgressBarIndeterminateVisibility(false);
				} catch (Exception e) {};
				loadusr(usr);
				break;
			case MSG_GROUP_NU_RETRIEVED:
				UserGroups = (List<UserGroup>) msg.obj;
				try {
					setProgressBarIndeterminateVisibility(false);
				} catch (Exception e) {};
				defnewuser();
				break;
			case MSG_EXEC_OP:
				Log.i(TAG, "ALL FINE ");
				finish();
				break;
			case MSG_MEDIA_EXEC_OP:
				mediaadapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * display an error poup
	 * 
	 * @param message
	 */
	public void displayPopup(int title, String message) {
		try {
			new AlertDialog.Builder(this).setMessage(message).setTitle(getResources().getString(title))
					.show();
		} catch (Exception e) {
		}
	}

	public String getPrefCurrentServer() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String srvname = prefs.getString("currentServer", getResources().getString(R.string.not_selected));
		return srvname;
	}

	public Server getSelectedServer() {
		String srvname = getPrefCurrentServer();
		DBAdapter db = new DBAdapter(this);
		db.open();
		return db.selectServer(srvname);
	}
	
	   @Override
	    public void onStart() {
	      super.onStart();
	      EasyTracker.getInstance(this).activityStart(this);
	    }

	    @Override
	    public void onStop() {
	      super.onStop();
	      EasyTracker.getInstance(this).activityStop(this);
	    }
}