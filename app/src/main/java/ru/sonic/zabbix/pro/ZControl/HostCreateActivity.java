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
import ru.sonic.zabbix.pro.adapters.HostGroupAdapter;
import ru.sonic.zabbix.pro.adapters.HostInterfaceAdapter;
import ru.sonic.zabbix.pro.adapters.TemplateAdapter;
import ru.sonic.zabbix.pro.api.HostApiHandler;
import ru.sonic.zabbix.pro.api.HostGroupApiHandler;
import ru.sonic.zabbix.pro.api.HostInterfaceApiHandler;
import ru.sonic.zabbix.pro.api.TemplateApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.HostGroup;
import ru.sonic.zabbix.pro.base.HostInterface;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.Template;
import ru.sonic.zabbix.pro.database.DBAdapter;

import android.annotation.SuppressLint;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class HostCreateActivity extends Activity {
	private HostApiHandler hapi = null;
	private HostGroupApiHandler gapi = null;
	private TemplateApiHandler tapi = null;
	private HostInterfaceApiHandler iapi = null;
	Host host;
	List<Template> templates;
	List<HostGroup> hostgroups;
	List<HostInterface> interfaces;
	String action;
	String hostid = "";
	protected static final int MSG_ERROR = 0;
	protected static final int MSG_TEMPLATES_RETRIEVED = 1;
	protected static final int MSG_GROUP_RETRIEVED = 2;
	protected static final int MSG_HOST_RETRIEVED = 3;
	/* Execute */
	protected static final int MSG_EXEC_OP = 4;
	protected static final int MSG_EXEC_INTERFACE_CREATE = 5;
	protected static final int MSG_EXEC_INTERFACE_DELETE = 6;
	protected static final int MSG_EXEC_INTERFACE_EDIT = 7;
	protected static final String TAG = "HostCreateActivity";
	View mailpage;
	View groupspage;
	ListView groupsAvail;
	ListView groupsAdded;
	ArrayList<HostGroup> groupslistAvail;
	ArrayList<HostGroup> groupslistAdded;
	HostGroupAdapter listArrayAdapterAvail;
	HostGroupAdapter listArrayAdapterAdded;
	ListView templatesAvail;
	ListView templatesAdded;
	ArrayList<Template> templatelistAvail;
	ArrayList<Template> templatelistAdded;
	TemplateAdapter templateArrayAdapterAvail;
	TemplateAdapter templateArrayAdapterAdded;
	
	ListView interfaceslistview;
	ArrayList<HostInterface> interfacearray;
	HostInterfaceAdapter zinterfaceadapter;
	float ApiVersion;
	public Context ctx;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.ctx = this;
		Server selectedZabbixServer = getSelectedServer();
		hapi = new HostApiHandler(this,selectedZabbixServer);
		gapi = new HostGroupApiHandler(this,selectedZabbixServer);
		tapi = new TemplateApiHandler(this,selectedZabbixServer);
		iapi = new HostInterfaceApiHandler(this,selectedZabbixServer);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> pages = new ArrayList<View>();

		mailpage = inflater.inflate(R.layout.host_create, null);
		pages.add(mailpage);

		groupspage = inflater.inflate(R.layout.host_create_groups, null);
		pages.add(groupspage);

		DiffAdapter pagerAdapter = new DiffAdapter(pages);
		ViewPager viewPager = new ViewPager(this);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);

		setContentView(viewPager);
		
		groups_mtypes_config();

		Button save = (Button) mailpage.findViewById(R.id.hostCreateSave);
		
		try {
			Bundle extras = getIntent().getExtras();
			action = extras.getString("action");
		} catch (Exception e) {
			finish();
		}
		
		try {
			Bundle extras = getIntent().getExtras();
			hostid = extras.getString("hostid");
			setProgressBarIndeterminateVisibility(true);
			executeop("loading", null);
			if (action.equals("edit")) {
				save.setText(R.string.save);
				save.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						edit(host);
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
			executeop("loading", null);
			save.setText(R.string.create);
			save.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					create();
				};
			});
		}
	}

	private void groups_mtypes_config() {
		/*Groups*/
		groupslistAvail = new ArrayList<HostGroup>();
		groupslistAdded = new ArrayList<HostGroup>();
		listArrayAdapterAvail = new HostGroupAdapter(this, groupslistAvail);
		listArrayAdapterAdded = new HostGroupAdapter(this, groupslistAdded);
		
		groupsAvail = (ListView) groupspage.findViewById(R.id.groupsavail);
		groupsAvail.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HostGroup gr = (HostGroup)(listArrayAdapterAvail.getItem(position));
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
				HostGroup gr = (HostGroup)(listArrayAdapterAdded.getItem(position));
				groupslistAdded.remove(gr);
				groupslistAvail.add(gr);
				listArrayAdapterAvail.notifyDataSetChanged();
				listArrayAdapterAdded.notifyDataSetChanged();
			}
		});
		
		groupsAvail.setAdapter(listArrayAdapterAvail);
		groupsAdded.setAdapter(listArrayAdapterAdded);
		
		/*Templates*/
		templatelistAvail = new ArrayList<Template>();
		templatelistAdded = new ArrayList<Template>();
		templateArrayAdapterAvail = new TemplateAdapter(this, templatelistAvail);
		templateArrayAdapterAdded = new TemplateAdapter(this, templatelistAdded);
		
		templatesAvail = (ListView) groupspage.findViewById(R.id.templatesavail);
		templatesAvail.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Template gr = (Template)(templateArrayAdapterAvail.getItem(position));
				templatelistAvail.remove(gr);
				templatelistAdded.add(gr);
				templateArrayAdapterAvail.notifyDataSetChanged();
				templateArrayAdapterAdded.notifyDataSetChanged();
			}
		}); 
		templatesAdded = (ListView) groupspage.findViewById(R.id.templatesadded);
		templatesAdded.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Template gr = (Template)(templateArrayAdapterAdded.getItem(position));
				templatelistAdded.remove(gr);
				templatelistAvail.add(gr);
				templateArrayAdapterAvail.notifyDataSetChanged();
				templateArrayAdapterAdded.notifyDataSetChanged();
			}
		});
		
		templatesAvail.setAdapter(templateArrayAdapterAvail);
		templatesAdded.setAdapter(templateArrayAdapterAdded);
		
		/*Interfaces*/
		interfacearray = new ArrayList<HostInterface>();
		zinterfaceadapter = new HostInterfaceAdapter(this, interfacearray);
		
		interfaceslistview = (ListView) mailpage.findViewById(R.id.hostinterfaceslist);
		interfaceslistview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.contentMenu);
	            menu.add(0, 0, 0, R.string.delete_interface);
	            menu.add(0, 1, 0, R.string.edit_interface);
			}
		});
		interfaceslistview.setAdapter(zinterfaceadapter);
		
		Button cancel = (Button) mailpage.findViewById(R.id.hostCreateCancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			};
		});
		
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.zinterface_create, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
		alert.setTitle(R.string.create_interface);
		alert.setView(layout);
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int whichButton) {
		    	JSONObject interfaceobj = new JSONObject();
		    	
		    	CheckBox defaultinterface = (CheckBox)layout.findViewById(R.id.defaultInterface);
		    	interfaceobj.put("main", defaultinterface.isChecked()?"1":"0");

				Spinner types = (Spinner) layout.findViewById(R.id.zinterfacetypes);
				int typepos = types.getSelectedItemPosition();
				interfaceobj.put("type",getResources().getStringArray(R.array.ZInterfacesTypesVal)[typepos]);

				RadioButton useip = (RadioButton)layout.findViewById(R.id.useIP);
		    	interfaceobj.put("useip", useip.isChecked()?"1":"0");
		    	
		    	EditText et = (EditText)layout.findViewById(R.id.dnsaddr);
				interfaceobj.put("dns", et.getText().toString());
				
				et = (EditText)layout.findViewById(R.id.ipaddr);
				interfaceobj.put("ip", et.getText().toString());

				et = (EditText)layout.findViewById(R.id.port);
				interfaceobj.put("port", et.getText().toString());
				
				HostInterface zinterf = new HostInterface(interfaceobj);
		    	if (action.equals("edit") && ApiVersion>1.3) {
		    		zinterf.put("hostid",host.getID());
		    		executeop("create_interface", zinterf);
		    	} else {
		    		interfacearray.add(zinterf);
			    	zinterfaceadapter.notifyDataSetChanged();
		    	}
		    	dialog.dismiss();
			}
		});

		alert.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		final AlertDialog dialog = alert.create();
		
		Button addinterface = (Button) mailpage.findViewById(R.id.addzinterface);
		addinterface.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (interfacearray.size()>0 && ApiVersion<=1.3) {
					displayPopup(R.string.not_allow,R.string.interface_limit_err_string);
				} else {
					CheckBox defaultinterface = (CheckBox)layout.findViewById(R.id.defaultInterface);
		    		defaultinterface.setChecked(true);
			    	EditText et = (EditText)layout.findViewById(R.id.dnsaddr);
			    	et.setText("");
					et = (EditText)layout.findViewById(R.id.ipaddr);
					et.setText("127.0.0.1");
					et = (EditText)layout.findViewById(R.id.port);
					et.setText("10050");
					Spinner types = (Spinner) layout.findViewById(R.id.zinterfacetypes);
					types.setSelection(0);
					RadioButton useip = (RadioButton)layout.findViewById(R.id.useIP);
					RadioButton usedns = (RadioButton)layout.findViewById(R.id.useDNS);
					useip.setChecked(true);
					usedns.setChecked(false);
					dialog.show();
				}
			}
		});
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	final HostInterface obj=(HostInterface)(interfaceslistview.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
    	case 0:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(R.string.are_you_sure)
    		.setTitle(R.string.delete_interface)
            .setCancelable(false)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	if (ApiVersion>1.3)
                		executeop("delete_interface", obj);
                	else {
                		interfacearray.remove((HostInterface) obj);
                		zinterfaceadapter.notifyDataSetChanged();
                	}
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
    	case 1:
    		LayoutInflater inflater = LayoutInflater.from(this);
    		final View layout = inflater.inflate(R.layout.zinterface_create, null);
    		final AlertDialog.Builder editalert = new AlertDialog.Builder(ctx);

    		editalert.setTitle(R.string.edit_interface);
    		editalert.setView(layout);
    		
    		CheckBox defaultinterface = (CheckBox)layout.findViewById(R.id.defaultInterface);
    		defaultinterface.setChecked(obj.getbMain());
    		
	    	EditText et = (EditText)layout.findViewById(R.id.dnsaddr);
	    	et.setText(obj.getDns());
			
			et = (EditText)layout.findViewById(R.id.ipaddr);
			et.setText(obj.getIP());

			et = (EditText)layout.findViewById(R.id.port);
			et.setText(obj.getPort());
			
			Spinner types = (Spinner) layout.findViewById(R.id.zinterfacetypes);
			try {
				types.setSelection(Integer.parseInt(obj.getType().toString())-1);
			} catch (Exception e) {
				types.setSelection(0);
			}
			
			RadioButton useip = (RadioButton)layout.findViewById(R.id.useIP);
			RadioButton usedns = (RadioButton)layout.findViewById(R.id.useDNS);
			if (obj.isUseIp())
				useip.setChecked(true);
			else
				usedns.setChecked(true);

    		editalert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    			@SuppressWarnings("unchecked")
    			public void onClick(DialogInterface dialog, int whichButton) {
    				interfacearray.remove((HostInterface) obj);
    		    	JSONObject interfaceobj = new JSONObject();
    		    	
    		    	interfaceobj.put("interfaceid", obj.getID());
    		    	
    		    	CheckBox defaultinterface = (CheckBox)layout.findViewById(R.id.defaultInterface);
    		    	interfaceobj.put("main", defaultinterface.isChecked()?"1":"0");

    				Spinner types = (Spinner) layout.findViewById(R.id.zinterfacetypes);
    				int typepos = types.getSelectedItemPosition();
    				interfaceobj.put("type",getResources().getStringArray(R.array.ZInterfacesTypesVal)[typepos]);

    				RadioButton useip = (RadioButton)layout.findViewById(R.id.useIP);
    		    	interfaceobj.put("useip", useip.isChecked()?"1":"0");
    		    	
    		    	EditText et = (EditText)layout.findViewById(R.id.dnsaddr);
    				interfaceobj.put("dns", et.getText().toString());
    				
    				et = (EditText)layout.findViewById(R.id.ipaddr);
    				interfaceobj.put("ip", et.getText().toString());

    				et = (EditText)layout.findViewById(R.id.port);
    				interfaceobj.put("port", et.getText().toString());
    				
    				HostInterface zinterf = new HostInterface(interfaceobj);
    		    	if (action.equals("edit") && ApiVersion>1.3) {
    		    		zinterf.put("hostid",host.getID());
    		    		executeop("edit_interface", zinterf);
    		    	} else {
    		    		interfacearray.add(zinterf);
    			    	zinterfaceadapter.notifyDataSetChanged();
    		    	}
    			}
    		});

    		editalert.setNegativeButton(R.string.cancel,
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
    						//Nothing
    					}
    				});
    		final AlertDialog editdialog = editalert.create();
    		editdialog.show();
    		break;
    	default:
    		break;
    	}
    	return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void loadhost(Host host) {
		for (Iterator<HostGroup> it = hostgroups.iterator(); it.hasNext();) {
			JSONObject sonobj = (JSONObject) it.next();
			HostGroup gr = new HostGroup(sonobj);
			boolean has = false;
			for (Iterator<Host> hostit = gr.getHosts().iterator(); hostit.hasNext();) {
				JSONObject hjson = (JSONObject) hostit.next();
				Host thost = new Host(hjson);
				if (host.getID().equals(thost.getID())) {
					groupslistAdded.add(gr);
					has = true;
				}
			}
			if (!has)
				groupslistAvail.add(gr);
		}

		listArrayAdapterAvail.notifyDataSetChanged();
		listArrayAdapterAdded.notifyDataSetChanged();

		for (Iterator<Template> it = templates.iterator(); it.hasNext();) {
			JSONObject sonobj = (JSONObject) it.next();
			Template tp = new Template(sonobj);
			templatelistAvail.add(tp);
		}
		
		if (host.getParentTemplates()!=null && host.getParentTemplates().size()>0)
		for (Iterator<Template> it = host.getParentTemplates().iterator(); it.hasNext();) {
			JSONObject sonobj = (JSONObject) it.next();
			Template gr = new Template(sonobj);
			templatelistAvail.remove(gr);
			templatelistAdded.add(gr);
		}
		templateArrayAdapterAvail.notifyDataSetChanged();
		templateArrayAdapterAdded.notifyDataSetChanged();
		
		try {
			JSONArray interfidobj = host.getInterfaces();		
			Iterator keys = interfidobj.iterator();
			while(keys.hasNext()){
					JSONObject sonobj = (JSONObject) keys.next();
					interfacearray.add(new HostInterface(sonobj));
			}
		} catch (ClassCastException e) {}
		catch (Exception e) {}
		zinterfaceadapter.notifyDataSetChanged();

		TextView tw = (TextView) mailpage.findViewById(R.id.hostCreateName);
		if (host.getName()!=null)
			tw.setText(host.getName());
		else
			tw.setText(host.getVisName());

		tw = (TextView) mailpage.findViewById(R.id.hostCreateHost);
		if (action.equals("edit"))
			tw.setText(host.getHost());
		else
			tw.setText(R.string.copy_host);
		
		CheckBox monstatus = (CheckBox)mailpage.findViewById(R.id.hostCreateMonitored);
		monstatus.setChecked(host.getEnStatus());
		/*
		Spinner lang = (Spinner) mailpage.findViewById(R.id.hostCreateMonitoredBy);
		String[] elements = getResources().getStringArray(R.array.HostMonitoredByVal);
		for (int i = 0; i < elements.length - 1; i++) {
			if (elements[i].equals(host.get))) {
				lang.setSelection(i);
				break;
			}
		} */
	}

	public void defnewhost() {
		for (Iterator<HostGroup> it = hostgroups.iterator(); it.hasNext();) {
			JSONObject sonobj = (JSONObject) it.next();
			HostGroup gr = new HostGroup(sonobj);
			groupslistAvail.add(gr);
		}
		listArrayAdapterAvail.notifyDataSetChanged();
		
		for (Iterator<Template> it = templates.iterator(); it.hasNext();) {
			JSONObject sonobj = (JSONObject) it.next();
			Template tp = new Template(sonobj);
			templatelistAvail.add(tp);
		}
		templateArrayAdapterAvail.notifyDataSetChanged();
		
		CheckBox monstatus = (CheckBox)mailpage.findViewById(R.id.hostCreateMonitored);
		monstatus.setChecked(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void create() {
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
		
		JSONArray grouparray = new JSONArray();
		for (Iterator<HostGroup> it1 = groupslistAdded.iterator(); it1.hasNext();) {
			JSONObject sonobj = (JSONObject) it1.next();
			HostGroup gr = new HostGroup(sonobj);
			JSONObject grobj = new JSONObject();
			grobj.put("groupid", gr.getID());
			grouparray.add(grobj);
		}
		if (grouparray.size()==0) {
			displayPopup(R.string.error,R.string.no_host_groups);
			return;
		}
		params.put("groups", grouparray);
		
		JSONArray templatearray = new JSONArray();
		for (Iterator<Template> it1 = templatelistAdded.iterator(); it1.hasNext();) {
			JSONObject sonobj = (JSONObject) it1.next();
			Template gr = new Template(sonobj);
			JSONObject grobj = new JSONObject();
			grobj.put("templateid", gr.getID());
			templatearray.add(grobj);
		}
		params.put("templates", templatearray);

		CheckBox autologin = (CheckBox) mailpage
				.findViewById(R.id.hostCreateMonitored);
		params.put("monitored", autologin.isChecked()?"1":"0");
		
		TextView tw = (TextView) mailpage.findViewById(R.id.hostCreateName);
		params.put("name", tw.getText().toString());
		
		tw = (TextView) mailpage.findViewById(R.id.hostCreateHost);
		params.put("host", tw.getText().toString());
		
		CheckBox monstatus = (CheckBox)mailpage.findViewById(R.id.hostCreateMonitored);
		params.put("status",monstatus.isChecked()?"1":"0");
    		
		JSONArray interfjsonobj = new JSONArray();
		Iterator keys = interfacearray.iterator();
		while(keys.hasNext()){
			interfjsonobj.add(keys.next());
		}
		params.put("interfaces", interfjsonobj);

		Host host = new Host(params);
		if (ApiVersion>1.3)
			executeop("create_2.0", host);
		else
			executeop("create_1.8", host);
	}

	@SuppressWarnings("unchecked")
	public void edit(Host host) {
		Host editedhost = host;
		JSONArray grouparray = new JSONArray();
		for (Iterator<HostGroup> it1 = groupslistAdded.iterator(); it1.hasNext();) {
			JSONObject sonobj = (JSONObject) it1.next();
			HostGroup gr = new HostGroup(sonobj);
			JSONObject grobj = new JSONObject();
			grobj.put("groupid", gr.getID());
			grouparray.add(grobj);
		}
		if (grouparray.size()==0) {
			displayPopup(R.string.error,R.string.no_host_groups);
			return;
		}
		editedhost.put("groups", grouparray);
		
		JSONArray templatearray = new JSONArray();
		for (Iterator<Template> it1 = templatelistAdded.iterator(); it1.hasNext();) {
			JSONObject sonobj = (JSONObject) it1.next();
			Template gr = new Template(sonobj);
			JSONObject grobj = new JSONObject();
			grobj.put("templateid", gr.getID());
			templatearray.add(grobj);
		}
		editedhost.put("templates", templatearray);

		TextView tw = (TextView) mailpage.findViewById(R.id.hostCreateName);
		editedhost.put("name", tw.getText().toString());
		
		tw = (TextView) mailpage.findViewById(R.id.hostCreateHost);
		editedhost.put("host", tw.getText().toString());

		CheckBox monstatus = (CheckBox)mailpage.findViewById(R.id.hostCreateMonitored);
		editedhost.put("status",monstatus.isChecked()?"0":"1");

		executeop("edit", editedhost);
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
					if (operation.equals("loading")) {
						msg.arg1 = MSG_TEMPLATES_RETRIEVED;
						msg.obj = tapi.get();
						exechandler.sendMessage(msg);
						try {
							String apiversion = hapi.getApiVersion();
							ApiVersion = Float.parseFloat(apiversion);
						} catch (Exception e) {}
					} else if (operation.equals("getgroups")) {
						msg.arg1 = MSG_GROUP_RETRIEVED;
						msg.obj = gapi.get();
						exechandler.sendMessage(msg);
					} else if (operation.equals("gethost")) { 
						msg.arg1 = MSG_HOST_RETRIEVED;
						msg.obj = hapi.getHostInfo((String) obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("create_2.0")) {
						msg.arg1 = MSG_EXEC_OP;
						msg.obj = hapi.create((Host) obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("create_1.8")) {
						msg.arg1 = MSG_EXEC_OP;
						msg.obj = hapi.create_old((Host) obj);
						exechandler.sendMessage(msg);
					}else if (operation.equals("edit")) {
						msg.arg1 = MSG_EXEC_OP;
						msg.obj = hapi.edit((Host) obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("delete")) {
						msg.arg1 = MSG_EXEC_OP;
						msg.obj = hapi.delete((Host) obj);
						exechandler.sendMessage(msg);
					} else if (operation.equals("delete_interface")) {
						/* NEED ADD TRY EXCPTIONS*/
						msg.arg1 = MSG_EXEC_INTERFACE_DELETE;
						try {
							JSONArray ret = iapi.delete((HostInterface) obj);
							JSONObject robj = (JSONObject) ret.get(0);
							JSONArray retinterfarr = (JSONArray) robj.get("interfaceids");
							msg.arg2 = Integer.parseInt((String) retinterfarr.get(0));
							msg.obj = obj;
							exechandler.sendMessage(msg);
						} catch (Exception e) {
							msg.arg1 = MSG_ERROR;
							msg.obj = e;
							exechandler.sendMessage(msg);
						}
					} else if (operation.equals("create_interface")) {
						/* NEED ADD TRY EXCPTIONS*/
						msg.arg1 = MSG_EXEC_INTERFACE_CREATE;
						try {
							JSONArray ret = iapi.create((HostInterface) obj);
							JSONObject robj = (JSONObject) ret.get(0);
							JSONArray retinterfarr = (JSONArray) robj.get("interfaceids");
							msg.arg2 = Integer.parseInt((String) retinterfarr.get(0));
							msg.obj = obj;
							exechandler.sendMessage(msg);
						} catch (Exception e) {
							msg.arg1 = MSG_ERROR;
							msg.obj = e;
							exechandler.sendMessage(msg);
						}
					} else if (operation.equals("edit_interface")) {
						/* NEED ADD TRY EXCPTIONS*/
						msg.arg1 = MSG_EXEC_INTERFACE_EDIT;
						try {
							JSONArray ret = iapi.edit((HostInterface) obj);
							JSONObject robj = (JSONObject) ret.get(0);
							JSONArray retinterfarr = (JSONArray) robj.get("interfaceids");
							msg.arg2 = Integer.parseInt((String) retinterfarr.get(0));
							msg.obj = obj;
							exechandler.sendMessage(msg);
						} catch (Exception e) {
							msg.arg1 = MSG_ERROR;
							msg.obj = e;
							exechandler.sendMessage(msg);
						}
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
	@SuppressLint({ "HandlerLeak" })
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
			case MSG_TEMPLATES_RETRIEVED:
				templates = (List<Template>) msg.obj;
				executeop("getgroups", null);
				break;
			case MSG_GROUP_RETRIEVED:
				hostgroups = (List<HostGroup>) msg.obj;
				if (action.equals("create")) {
					defnewhost();
					try {
						setProgressBarIndeterminateVisibility(false);
					} catch (Exception e) {};
				} else
					executeop("gethost", hostid);
				break;
			case MSG_HOST_RETRIEVED:
				JSONObject obj = (JSONObject) ((JSONArray) msg.obj).get(0);
				host = new Host(obj);
				try {
					setProgressBarIndeterminateVisibility(false);
				} catch (Exception e) {};
				loadhost(host);
				break;
			case MSG_EXEC_OP:
				Log.i(TAG, "ALL FINE ");
				finish();
				break; 
			case MSG_EXEC_INTERFACE_CREATE:
				HostInterface interf = (HostInterface) msg.obj;
				interf.setID(msg.arg2+"");
				interfacearray.add(interf);
		    	zinterfaceadapter.notifyDataSetChanged();
				break;
			case MSG_EXEC_INTERFACE_DELETE:
				interfacearray.remove((HostInterface) msg.obj);
				zinterfaceadapter.notifyDataSetChanged();
				break;
			case MSG_EXEC_INTERFACE_EDIT:
				try {
					HostInterface einterf = (HostInterface) msg.obj;
					if (msg.arg2 == Integer.parseInt(einterf.getID())) {
						interfacearray.add(einterf);
						zinterfaceadapter.notifyDataSetChanged();
					} else {
						displayPopup(R.string.error,R.string.wrong_interface_params);
					}
				} catch (Exception e) {
					displayPopup(R.string.error,e.getMessage());
				}
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
	public void displayPopup(String title, String message) {
		try {
			new AlertDialog.Builder(this).setMessage(message).setTitle(title)
					.show();
		} catch (Exception e) {
		}
	}
	
	public void displayPopup(int title, int message) {
		try {
			new AlertDialog.Builder(this).setMessage(getResources().getString(message)).setTitle(getResources().getString(title))
					.show();
		} catch (Exception e) {
		}
	}
	
	public void displayPopup(int title, String message) {
		try {
			new AlertDialog.Builder(this).setMessage(message).setTitle(getResources().getString(title)).show();
		} catch (Exception e) {
		}
		
	};

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