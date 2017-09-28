package ru.sonic.zabbix.pro.activities;

import java.util.List;

import ru.sonic.zabbix.pro.api.MapApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Maps;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * display all hosts
 * @author dassinion
 *
 */
public class MapsListActivity extends DefaultZabbixListActivity {
	private MapApiHandler api;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new MapApiHandler(this,getSelectedServer());
		//Button selectServer = (Button)findViewById(R.id.selectServer);
		//selectServer.setVisibility(View.GONE);
		
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Maps map=(Maps)(lv.getAdapter().getItem(position));
				Intent mapIntent = new Intent(getBaseContext(),MapActivity.class);
				mapIntent.putExtra("mapID", map.getID());
				//mapIntent.putExtra("width", map.getwidth());
				//mapIntent.putExtra("height", map.getheight());
				startActivity(mapIntent);
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		api = new MapApiHandler(this,getSelectedServer());
		return api.get();
	}

	/*
	@Override
	public void clearAuth() {
		api.clearAuth();
	}*/
}
