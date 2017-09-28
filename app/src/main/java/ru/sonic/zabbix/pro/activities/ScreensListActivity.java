package ru.sonic.zabbix.pro.activities;


import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.api.ScreenApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;

import ru.sonic.zabbix.pro.base.Screen;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * display all hosts
 * @author dassinion
 *
 */
public class ScreensListActivity extends DefaultZabbixListActivity {
	private ScreenApiHandler api;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new ScreenApiHandler(this,getSelectedServer());
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
		
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Screen screen=(Screen)(lv.getAdapter().getItem(position));
				Intent screenIntent = new Intent(getBaseContext(),ScreenActivity.class);
				screenIntent.putExtra("screenID", screen.getID());
				screenIntent.putExtra("hsize", screen.gethsize());
				screenIntent.putExtra("vsize", screen.getvsize());
				startActivity(screenIntent);
			}
		});
	}
	
	@Override
	protected List getData() throws ZabbixAPIException {
		//if (api==null)
		//	api = new ScreenApiHandler(this);
		return api.get();
	}

	/*
	@Override
	public void clearAuth() {
		api.clearAuth();
	}*/
}
