package ru.sonic.zabbix.pro.ZControl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
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
@SuppressWarnings("rawtypes")
public class HostControlActivity extends DefaultZabbixListActivity {
	protected static final String TAG = "HostControlActivity";
	private HostApiHandler api;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new HostApiHandler(this,getSelectedServer());
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
		
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {    
                menu.setHeaderTitle(R.string.contentMenu);  
                menu.add(0, 0, 0, R.string.more_info);
                menu.add(0, 1, 0, R.string.edit);
                menu.add(0, 2, 0, R.string.copy_host);
                menu.add(0, 3, 0, R.string.delete);
                AdapterContextMenuInfo minfo = (AdapterContextMenuInfo) info;
                Host obj=(Host)(lv.getAdapter().getItem(minfo.position));
                if (obj.getEnStatus())
                	menu.add(0, 4, 0, R.string.disable);
                else
                	menu.add(0, 5, 0, R.string.activate);
            }
		});
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new HostApiHandler(this,getSelectedServer());
		return api.get("");
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("edit"))
			return api.edit((Host) obj);
		else if (operation.equals("copy"))
			return api.create((Host) obj);
		else if (operation.equals("delete"))
			return api.delete((Host) obj);
		else if (operation.equals("create")) 
			return api.create((Host) obj);
		else if (operation.equals("disable")) {
			((Host) obj).setStatus("1");
			return api.set_status((Host) obj);
		} else if (operation.equals("enable")) {
			((Host) obj).setStatus("0");
			return api.set_status((Host) obj);
		} else {
			return null;
		}
	}

	@Override
	public void setListContent(List data) {
		if (data==null || data.size()==0){
			setListAdapter(null);
			Toast.makeText(getApplicationContext(),
					R.string.no_data_to_display, Toast.LENGTH_LONG).show();
			return;
			}
        @SuppressWarnings("unchecked")
		HostAdapter hostAdapter = new HostAdapter(this,data); 
        setListAdapter( hostAdapter );
	}
	
	@Override
	public void showmore(Object host) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.host_info, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.host_information);
		TextView et = (TextView)layout.findViewById(R.id.host_alias);
    	et.setText(((Host) host).getHost());
    	et = (TextView)layout.findViewById(R.id.host_name);
    	et.setText(((Host) host).getName());
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
		menu.add(0, 0, 0, R.string.create);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent hostintent = new Intent(getBaseContext(),HostCreateActivity.class);
		hostintent.putExtra("action", "create");
		startActivityForResult(hostintent,1);
		return true;
	}
	
	@Override
	public void edit(Object obj) {
		Intent hostintent = new Intent(getBaseContext(),HostCreateActivity.class);
		hostintent.putExtra("hostid", ((Host) obj).getID());
		hostintent.putExtra("action", "edit");
		startActivityForResult(hostintent,1);
	}
	
	@Override
	public void copy(Object obj) {
		Intent hostintent = new Intent(getBaseContext(),HostCreateActivity.class);
		hostintent.putExtra("hostid", ((Host) obj).getID());
		hostintent.putExtra("action", "copy");
		startActivityForResult(hostintent,1);
	}
	
  	@Override
 	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 	  super.onActivityResult(requestCode, resultCode, data);
		refreshData();
 	}

	/*
	@Override
	public void clearAuth() {
		api.clearAuth();
	}*/
}
