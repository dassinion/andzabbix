package ru.sonic.zabbix.pro.activities;

import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.ItemsAdapter;
import ru.sonic.zabbix.pro.api.ItemApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Item;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * display all items for a host in a table
 * 
 */
public class ItemListActivity extends DefaultZabbixListActivity {
	private String hostid = "";
	protected static String TAG = "ItemListActivity";
	private ItemApiHandler api;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new ItemApiHandler(this,getSelectedServer());
		
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
		
		setTitle(getResources().getString(R.string.host)+": "+ getIntent().getExtras().get("hostName").toString());

		final ListView lv = (ListView) findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Item item = (Item) lv.getAdapter().getItem(position);
				if (item.getValueType().equals("0")
						|| item.getValueType().equals("3")) {
					Intent itemActivity = new Intent(getBaseContext(),
							ChartActivity.class);
					itemActivity.putExtra("graphID", item.getID());
					itemActivity.putExtra("chart_type", 1);
					startActivity(itemActivity);
				} else { //if (item.getValueType().equals("1") || item.getValueType().equals("2")) {
					Intent itemActivity = new Intent(getBaseContext(),ItemHistActivity.class);
					itemActivity.putExtra("itemid", item.getID());
					itemActivity.putExtra("valuetype", item.getValueType());
					startActivity(itemActivity);
				}
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		api = new ItemApiHandler(this,getSelectedServer());
		try {
			hostid = (String) getIntent().getExtras().get("hostid");
			if (hostid == null || hostid.length()==0) 
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return api.get(hostid);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setListContent(List data) {
		// Get the TableLayout
		if (data == null || data.size() == 0) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_data_to_display),
					Toast.LENGTH_LONG).show();
			return;
		}
		ItemsAdapter adapter = new ItemsAdapter(this, data);
		setListAdapter(adapter);
	}
}