package ru.sonic.zabbix.pro.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.HostAdapter;
import ru.sonic.zabbix.pro.adapters.HostInterfaceAdapter;
import ru.sonic.zabbix.pro.api.HostApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.HostInterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * display all hosts
 * @author dassinond
 *
 */
public class HostListActivity extends DefaultZabbixListActivity {
	protected static final int CONTEXTMENU_SHOWHOSTPREF = 0;
	protected static final int CONTEXTMENU_SHOWITEMS = 1;
	protected static final int CONTEXTMENU_SHOWHOSTGRAPHS = 2;
	protected static final int CONTEXTMENU_ACTIVATEHOST = 3; 
	protected static final int CONTEXTMENU_DISABLEHOST = 4;
	private HostApiHandler api;
	HostAdapter hostAdapter;
	List<Host> data, filteredData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new HostApiHandler(this,getSelectedServer());
		data = new ArrayList<Host>();
		filteredData = new ArrayList<Host>();
		hostAdapter = new HostAdapter(this,filteredData);
		setListAdapter( hostAdapter );
		/*
		class FilterAction extends AbstractAction {
            public FilterAction() {
                super(R.drawable.filter);
            } 
            @Override
            public void performAction(View view) {
            	Intent hostPrefs = new Intent(getBaseContext(),FilterActivity.class);
        		startActivity(hostPrefs);
            }
        }
        //actionBar.addAction(new FilterAction(),ACTIONBARITEM_FILTER);
        */
        
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Host host=(Host)(lv.getAdapter().getItem(position));
				Intent itemlistintent = new Intent(getBaseContext(),
						ItemListActivity.class);
				itemlistintent.putExtra("hostid", host.getID());
				itemlistintent.putExtra("hostName", host.getVisName());
				startActivity(itemlistintent);
			}

		});
		
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {    
                menu.setHeaderTitle(R.string.contentMenu);  
                menu.add(0, CONTEXTMENU_SHOWHOSTPREF, 0, R.string.host_info);
                menu.add(0, CONTEXTMENU_SHOWITEMS, 0, R.string.show_items);
                menu.add(0, CONTEXTMENU_SHOWHOSTGRAPHS, 0, R.string.show_graphs);
                AdapterContextMenuInfo minfo = (AdapterContextMenuInfo) info;
                Host obj=(Host)(lv.getAdapter().getItem(minfo.position));
                if (obj.getEnStatus())
                	menu.add(0, CONTEXTMENU_DISABLEHOST, 0, R.string.disable);
                else
                	menu.add(0, CONTEXTMENU_ACTIVATEHOST, 0, R.string.activate);
            }
		});
		
        final EditText etfilter = (EditText)findViewById(R.id.edtttext_host_filter);
        etfilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            	filteredData.clear();
            	if (data!=null)
	            	for (Host host : data) {
	            	    if (host.getName().toUpperCase().contains(arg0.toString().toUpperCase())) {
	            	    	filteredData.add(host);
	            	    }
	            	}
            	hostAdapter.notifyDataSetChanged();
            }

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
        });
        /*
		class TextFilter extends AbstractAction {
            public TextFilter() {
                super(R.drawable.filter_txt);
            } 
            @Override
            public void performAction(View view) {
            	LinearLayout rl = (LinearLayout)findViewById(R.id.filter_ll);
            	if (rl.getVisibility()==View.GONE) {
            		rl.setVisibility(View.VISIBLE);
            		etfilter.requestFocus();
            	} else {
            		rl.setVisibility(View.GONE);
            		filteredData.clear();
            		filteredData.addAll(data);
            		hostAdapter.notifyDataSetChanged();
            	}
            }
        }
        //actionBar.addAction(new TextFilter(),ACTIONBARITEM_TEXTEDITFILTER);
        */
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		api = new HostApiHandler(this,getSelectedServer());
		String groupID = "";
		try {
			Bundle extras = getIntent().getExtras();
			groupID  = extras.getString("groupID");
			if (groupID == null) {groupID = "";}
		} catch (Exception e) {groupID = "";}
		return api.get(groupID);
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("create"))
			return api.create((Host) obj);
		else if (operation.equals("enable")) {
			((Host) obj).setStatus("0");
			return api.set_status((Host) obj);
		} else if (operation.equals("disable")) {
			((Host) obj).setStatus("1");
			return api.set_status((Host) obj);
		} else if (operation.equals("delete")) {
			api.delete(((Host) obj));
			return null;
		} else {
			return null;
		}
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	ListView lv=(ListView)findViewById(android.R.id.list);
    	Host host=(Host)(lv.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
        	case CONTEXTMENU_SHOWITEMS:
				Intent itemlistintent = new Intent(getBaseContext(),ItemListActivity.class);
				itemlistintent.putExtra("hostid", host.getID());
				itemlistintent.putExtra("hostName", host.getVisName());
				startActivity(itemlistintent);
        	break;
        	case CONTEXTMENU_SHOWHOSTPREF:
        		showmore(host);
        	break;
           	case CONTEXTMENU_SHOWHOSTGRAPHS:
				Intent graphs = new Intent(getBaseContext(),GraphsListActivity.class);
				graphs.putExtra("hostid", host.getID());
				graphs.putExtra("hostName", host.getVisName());
				startActivity(graphs);
        	break;
           	case CONTEXTMENU_ACTIVATEHOST:
           		executeop("enable", host);
        	break;  
           	case CONTEXTMENU_DISABLEHOST:
           		executeop("disable", host);
            break;
    	}
    return true;
    } 

	@Override
	public void setListContent(@SuppressWarnings("rawtypes") List newdata) {
		data.clear();
		data.addAll(newdata);
		if (data==null || data.size()==0){
			Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_data_to_display), Toast.LENGTH_LONG).show();
			setListAdapter(null);
			return;
			}
		//hostAdapter = new HostAdapter(this,data); 
        //setListAdapter( hostAdapter );
		filteredData.clear();
		filteredData.addAll(newdata);
		hostAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void showmore(Object host) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.host_info, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.host_information);
		
		TextView et = (TextView)layout.findViewById(R.id.host_alias);
		
		if (((Host) host).getName()==null || ((Host) host).getName().equals("")) {
	    	et.setText(((Host) host).getHost());
	    	et = (TextView)layout.findViewById(R.id.host_name);
	    	et.setText(((Host) host).getHost());
		} else {
	    	et.setText(((Host) host).getHost());
	    	et = (TextView)layout.findViewById(R.id.host_name);
	    	et.setText(((Host) host).getName());
		}
    	
    	et = (TextView)layout.findViewById(R.id.host_error);
    	et.setText(((Host) host).getError());
    	et = (TextView)layout.findViewById(R.id.host_errorfrom);
    	et.setText(((Host) host).getErrors_fromDate());
    	
    	if (!((Host) host).getEnStatus()) {
    		et = (TextView)layout.findViewById(R.id.host_disableuntil);
    		et.setText(((Host) host).getDisable_untilDate());
    	} else {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.host_disableuntil_ll);
    		ll.setVisibility(View.GONE);
    	}
    	
    	if (((Host) host).getLastaccess().equals("0")) {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.host_lastaccess_ll);
    		ll.setVisibility(View.GONE);
    	} else {
    		et = (TextView)layout.findViewById(R.id.host_lastaccess);
    		et.setText(((Host) host).getLastaccess());
    	}
    	
    	et = (TextView)layout.findViewById(R.id.host_maintencestatus);
    	et.setText(((Host) host).getMaintenance_status());
    	et = (TextView)layout.findViewById(R.id.host_snmp_error);
    	et.setText(((Host) host).getSnmp_error());
    	ImageView iv = (ImageView)layout.findViewById(R.id.host_monitored);
    	iv.setImageResource(((Host) host).getStatusImg());
    	iv = (ImageView)layout.findViewById(R.id.host_available);
    	iv.setImageResource(((Host) host).getAvailableImg());
    	iv = (ImageView)layout.findViewById(R.id.host_snmp_available);
    	iv.setImageResource(((Host) host).getSnmpAvailableImg());
    	
    	if (!((Host) host).getIsUnAvailable()) {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.host_error_layout);
    		ll.setVisibility(View.GONE);
    	}

    	if (!((Host) host).getIsSnmpUnAvailable()) {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.host_snmp_layout);
    		ll.setVisibility(View.GONE);
    	}
    	
    	if (!((Host) host).getIsJMXUnAvailable()) {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.host_jmx_layout);
    		ll.setVisibility(View.GONE);
    	}
    	
    	if (!((Host) host).getIsIPMIUnAvailable()) {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.host_ipmi_layout);
    		ll.setVisibility(View.GONE);
    	}
    	
    	if (((Host) host).getMaintenance_status().equals("0")) {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.host_maintence_ll);
    		ll.setVisibility(View.GONE);
    	}
    	
    	ListView interfaces = (ListView)layout.findViewById(R.id.interfaces);
    	ArrayList<HostInterface> interfacearray = new ArrayList<HostInterface>();
    	HostInterface interf;
		JSONArray interfidobj = ((Host) host).getInterfaces();
		Iterator keys = interfidobj.iterator();
		while(keys.hasNext()){
			JSONObject interfjsonobj = (JSONObject) keys.next();
		    interf = new HostInterface(interfjsonobj);
		    interfacearray.add(interf);
		}
    	HostInterfaceAdapter zinterfaceadapter = new HostInterfaceAdapter(this, interfacearray);
    	interfaces.setAdapter(zinterfaceadapter);
    	
		alert.setView(layout)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
		
		alert.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.filters_and_sorting);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent hostPrefs = new Intent(getBaseContext(),FilterActivity.class);
		startActivity(hostPrefs);
		return true;
	}

	/*
	@Override
	public void clearAuth() {
		api.clearAuth();
	}*/
	
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