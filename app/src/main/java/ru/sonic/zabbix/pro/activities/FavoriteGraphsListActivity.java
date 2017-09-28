package ru.sonic.zabbix.pro.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.FavoriteGraphListAdapter;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Graph;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * display all hosts
 * @author dassinion
 *
 */
public class FavoriteGraphsListActivity extends DefaultZabbixListActivity {
//	private static final String TAG = "GraphsListActivity";
	protected static final int CONTEXTMENU_DELETEFROMFAVORITE = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db.open();
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);

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
                menu.add(0, CONTEXTMENU_DELETEFROMFAVORITE, 0, R.string.del_from_favorite);
            }
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		List<Graph> lgraph = new ArrayList<Graph>();
		try {
			if (db.open()==null)
				db.open();
				lgraph.addAll(db.getFavoriteGraphs());
				db.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return lgraph;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setListContent(@SuppressWarnings("rawtypes") List data) {
		if (data.size()==0){
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.no_data_to_display), Toast.LENGTH_LONG).show();
			return;
			}
		FavoriteGraphListAdapter graphListAdapter = new FavoriteGraphListAdapter(this,data);
        setListAdapter( graphListAdapter );
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	ListView lv=(ListView)findViewById(android.R.id.list);
    	Graph graph=(Graph)(lv.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
        	case CONTEXTMENU_DELETEFROMFAVORITE:
				db.open();
				db.delFavoriteGraph(graph);
				db.close();
				refreshData();
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