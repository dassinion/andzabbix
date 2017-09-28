package ru.sonic.zabbix.pro.ZControl;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.api.HostGroupApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;

/**
 * display host groups
 * @author dassinion
 *
 */
public class HostGroupsControl extends DefaultZabbixListActivity {
	//private static final String TAG = "ZabbixAPIHostGroupActivity";
	private HostGroupApiHandler api;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new HostGroupApiHandler(this,getSelectedServer());
		return api.get();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new HostGroupApiHandler(this,getSelectedServer());
		ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//ListView lv=(ListView)findViewById(android.R.id.list);
				//HostGroup group=(HostGroup)(lv.getAdapter().getItem(position));		
			}

		});
	}
}
