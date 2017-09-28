package ru.sonic.zabbix.pro.activities;

import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.HistoryAdapter;
import ru.sonic.zabbix.pro.api.HistoryApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;

import android.os.Bundle;
import android.widget.Toast;

/**
 * display all items for a host in a table
 * 
 * @author dassinion
 * 
 */
public class ItemHistActivity extends DefaultZabbixListActivity {
	private String itemid = "";
	private String valuetype = "";
	protected static String TAG = "ItemHistActivity";
	private HistoryApiHandler api = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new HistoryApiHandler(this,getSelectedServer());
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		api = new HistoryApiHandler(this,getSelectedServer());
		try {
			itemid = (String) getIntent().getExtras().get("itemid");
			valuetype = (String) getIntent().getExtras().get("valuetype");
			if (itemid == null || itemid.length()==0) 
				return null;
		} catch (Exception e) {
			return null;
		}
		return api.get(itemid,valuetype);
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
		HistoryAdapter adapter = new HistoryAdapter(this, data);
		setListAdapter(adapter);
	}
}
