package ru.sonic.zabbix.pro.ZControl;

import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;

import ru.sonic.zabbix.pro.adapters.MaintencesAdapter;


import ru.sonic.zabbix.pro.api.MaintenceApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Maintence;

import android.os.Bundle;
import android.view.ContextMenu;

import android.view.Menu;

import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;

import android.widget.ListView;

import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MaintencesListActivity extends DefaultZabbixListActivity {
	private MaintenceApiHandler api = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new MaintenceApiHandler(this,getSelectedServer());
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
		
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);
		
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {   
                menu.setHeaderTitle(R.string.contentMenu);
                menu.add(0, CONTEXTMENU_SHOWINFO, 0, R.string.more_info);
                //menu.add(0, CONTEXTMENU_EDIT, 0, "Edit");
                //menu.add(0, CONTEXTMENU_COPY, 0, "Copy");
                menu.add(0, CONTEXTMENU_DELETE, 0, R.string.delete);
                AdapterContextMenuInfo minfo = (AdapterContextMenuInfo) info;
                Maintence obj=(Maintence)(lv.getAdapter().getItem(minfo.position));
                if (obj.getEnStatus())
                	menu.add(0, 4, 0, R.string.disable);
                else
                	menu.add(0, 5, 0, R.string.activate);
            }
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new MaintenceApiHandler(this,getSelectedServer());
		return api.get();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setListContent(List data) {
		if (data==null || data.size()==0){
			setListAdapter(null);
			Toast.makeText(getApplicationContext(),
					R.string.no_data_to_display, Toast.LENGTH_LONG).show();
			return;
			}
        @SuppressWarnings("unchecked")
		MaintencesAdapter hostAdapter = new MaintencesAdapter(this,data); 
        setListAdapter( hostAdapter );
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(0, 0, 0, "Create action");
		return super.onCreateOptionsMenu(menu);
	}
	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//########################################
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.create);
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.xml.action_create, null);
		alert.setView(layout);

		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Hashtable<Object, Object> params = new Hashtable<Object, Object>();
				EditText et = (EditText)layout.findViewById(R.id.name);
    			params.put("name",et.getText().toString());
    			params.put("eventsource","0");
    			params.put("evaltype","0");
    			params.put("status","0");
    			et = (EditText)layout.findViewById(R.id.step_duration);
    			params.put("esc_period",et.getText().toString());
    			et = (EditText)layout.findViewById(R.id.subj);
    			params.put("def_shortdata", et.getText().toString());
    			et = (EditText)layout.findViewById(R.id.msg);
    			params.put("def_longdata", et.getText().toString());
    			Maintence action = new Maintence(params);
    			executeop("create", action);
			}
		});

		alert.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// finish();
					}
				});

		alert.show();
		return true;
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("create"))
			return api.create((Maintence) obj);
		else if (operation.equals("enable")) {
			((Maintence) obj).setStatus("0");
			return api.edit((Maintence) obj);
		} else if (operation.equals("disable")) {
			((Maintence) obj).setStatus("1");
			return api.edit((Maintence) obj);
		} else if (operation.equals("delete")) {
			api.delete(((Maintence) obj));
			return null;
		} else {
			return null;
		}
	}
	/*
	@Override
	public void showmore(Object action) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.action_info, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.information);
		TextView et = (TextView)layout.findViewById(R.id.action_name);
	   	et.setText(((Maintence) action).getName());
	   	et = (TextView)layout.findViewById(R.id.action_deflong);
	   	et.setText(((Maintence) action).getDef_longdata());
	   	et = (TextView)layout.findViewById(R.id.action_defshort);
	   	et.setText(((Maintence) action).getDef_shortdata());
	   	et = (TextView)layout.findViewById(R.id.action_esc_period);
	   	et.setText(((Maintence) action).getEsc_period());
	   	et = (TextView)layout.findViewById(R.id.action_evaltype);
	   	et.setText(((Maintence) action).getEvaltype());
	   	et = (TextView)layout.findViewById(R.id.action_eventsource);
	   	et.setText(((Maintence) action).getEventsource());
	   	
	   	ImageView iv = (ImageView)layout.findViewById(R.id.action_status);
	   	iv.setImageResource(((Maintence) action).getStatusImg());
	   	
	   	alert.setView(layout)
	       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	           }
	       });
		
		alert.show();
	}
	*/
}
