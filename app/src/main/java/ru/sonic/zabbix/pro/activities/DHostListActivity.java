package ru.sonic.zabbix.pro.activities;

import java.util.Iterator;
import java.util.List;

import ru.sonic.zabbix.pro.adapters.DHostAdapter;
import ru.sonic.zabbix.pro.api.HostApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.DHost;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * display all Dhosts
 * @author dassinion
 *
 */
public class DHostListActivity extends DefaultZabbixListActivity {
	protected static final int CONTEXTMENU_SHOWHOSTPREF = 0;
	protected static final int CONTEXTMENU_SHOWITEMS = 1;
	protected static final int CONTEXTMENU_SHOWHOSTGRAPHS = 2;
	protected static final int CONTEXTMENU_ACTIVATEHOST = 3; 
	protected static final int CONTEXTMENU_DISABLEHOST = 4; 
	private HostApiHandler api = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new HostApiHandler(this,getSelectedServer());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		//if (api==null)
		//	api = new HostApiHandler(this);
		List<DHost> dhosts = api.getDHosts();
		for (Iterator<DHost> host = dhosts.iterator(); host.hasNext();) {
			String hostid = host.next().getID();
			Log.e(TAG, "Hostid: "+hostid);
		}
		return null;
	}

	@Override
	public void setListContent(@SuppressWarnings("rawtypes") List data) {
        @SuppressWarnings("unchecked")
		DHostAdapter hostAdapter = new DHostAdapter(this,data ); 
        setListAdapter( hostAdapter );
		//Log.e(TAG, "setListContent");
		
		if (data.size()==0){
			Toast.makeText(getApplicationContext(),
					"No data to display", Toast.LENGTH_LONG).show();
			}
	}
}
