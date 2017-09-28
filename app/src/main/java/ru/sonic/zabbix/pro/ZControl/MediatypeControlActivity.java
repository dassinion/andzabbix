package ru.sonic.zabbix.pro.ZControl;
import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.adapters.MediatypeAdapter;
import ru.sonic.zabbix.pro.api.MediatypeApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import android.os.Bundle;

import android.widget.Toast;

/**
 * display all mediatypes
 * @author dassinion
 *
 */
public class MediatypeControlActivity extends DefaultZabbixListActivity {
	private MediatypeApiHandler api = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new MediatypeApiHandler(this,getSelectedServer());
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new MediatypeApiHandler(this,getSelectedServer());
		return api.get();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setListContent(List data) {
		if (data == null || data.size() == 0) {
			Toast.makeText(getApplicationContext(), R.string.no_data_to_display,
					Toast.LENGTH_LONG).show();
			return;
		}
		MediatypeAdapter adapter = new MediatypeAdapter(this, data);
		setListAdapter(adapter);
	}
}
