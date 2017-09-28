package ru.sonic.zabbix.pro.activities;

import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.api.HostGroupApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.HostGroup;


/**
 * display host groups
 * @author dassinion
 *
 */
public class HostGroupActivity extends DefaultZabbixListActivity {
	//private static final String TAG = "ZabbixAPIHostGroupActivity";
	private HostGroupApiHandler api;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
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
				ListView lv=(ListView)findViewById(android.R.id.list);
				HostGroup group=(HostGroup)(lv.getAdapter().getItem(position));
				Intent GroupIntent = new Intent(getBaseContext(),
						HostListActivity.class);
				GroupIntent.putExtra("groupID", group.getID());
				startActivity(GroupIntent);			
			}

		});
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
