package ru.sonic.zabbix.pro.activities;

import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.GraphListAdapter;
import ru.sonic.zabbix.pro.api.GraphApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Graph;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.Server;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * display all hosts
 * @author dassinion
 *
 */
public class GraphsListActivity extends DefaultZabbixListActivity {
	private static final String TAG = "GraphsListActivity";
	protected static final int CONTEXTMENU_ADDTOFAVORITE = 0;
	private String hostname;
	private Server zabbixServer;
	private GraphApiHandler api;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"OnCreat Activity");
		Bundle extras = getIntent().getExtras();
		hostname = extras.getString("hostName");
		String servername = extras.getString("servername");
		setTitle(getResources().getString(R.string.host)+": "+hostname);
		db.open();
		zabbixServer = db.selectServer(servername);
		if (api==null)
			api = new GraphApiHandler(this,zabbixServer);

		//Button selectServer = (Button)findViewById(R.id.selectServer);
		//selectServer.setVisibility(View.GONE);
		
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Graph graph=(Graph)(lv.getAdapter().getItem(position));
				Intent graphIntent = new Intent(getBaseContext(),ChartActivity.class);
				graphIntent.putExtra("graphID", graph.getID());
				graphIntent.putExtra("chart_type", 0);
				startActivity(graphIntent);
			}
		});
		
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {    
                menu.setHeaderTitle(R.string.contentMenu);  
                menu.add(0, CONTEXTMENU_ADDTOFAVORITE, 0, R.string.add_to_favorite);
            }
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new GraphApiHandler(this,zabbixServer);
		String hostID = "";
		try {
			Bundle extras = getIntent().getExtras();
			hostID  = extras.getString("hostid");
			if (hostID == null) {hostID = "";}
		} catch (Exception e) {
			hostID = "";
		}
		Log.d(TAG,"getData HostID: "+hostID);
		return api.get(hostID);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setListContent(@SuppressWarnings("rawtypes") List data) {
		if (data.size()==0){
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.no_data_to_display), Toast.LENGTH_LONG).show();
			return;
			}
		GraphListAdapter graphListAdapter = new GraphListAdapter(this,data);
        setListAdapter( graphListAdapter );
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	ListView lv=(ListView)findViewById(android.R.id.list);
    	Graph graph=(Graph)(lv.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
        	case CONTEXTMENU_ADDTOFAVORITE:
        		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        		String srvname = prefs.getString("currentServer",getResources().getString(R.string.not_selected));
				db.open();
				db.insertFavoriteGraph(graph, hostname, srvname);
        	break;
    	}
    return true;
    } 
	
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