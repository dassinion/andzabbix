package ru.sonic.zabbix.pro.ZControl;

import java.util.Hashtable;
import java.util.List;

import org.json.simple.JSONArray;


import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.api.ScriptApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Script;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ScriptsActivity extends DefaultZabbixListActivity {
	private ScriptApiHandler api = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new ScriptApiHandler(this,getSelectedServer());
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
		
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);
		
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {    
                menu.setHeaderTitle(R.string.contentMenu);
                menu.add(0, 0, 0, R.string.more_info);
                //menu.add(0, 1, 0, "Edit");
                //menu.add(0, 2, 0, "Copy");
                menu.add(0, 3, 0, R.string.delete);
            }
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new ScriptApiHandler(this,getSelectedServer());
		return api.get();
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("create"))
			return api.create((Script) obj);
		else if (operation.equals("delete")) {
			api.delete(((Script) obj));
			return null;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.create_scrept);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		create();
		return true;
	}
	
	@Override
	public void create() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.create_scrept);
		String text = "Params:";
		alert.setMessage(text);
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.script_create, null);
		alert.setView(layout);

		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Hashtable<Object, Object> params = new Hashtable<Object, Object>();
				EditText et = (EditText)layout.findViewById(R.id.scriptname);
				params.put("name", et.getText().toString());				
				et = (EditText)layout.findViewById(R.id.scriptcommand);
				params.put("command", et.getText().toString());
				Spinner types = (Spinner) layout.findViewById(R.id.scripttype);
				int typepos = types.getSelectedItemPosition();
				params.put("type",getResources().getStringArray(R.array.ScriptTypesVal)[typepos]);
				Script script = new Script(params);
				
				executeop("create", script);
			}
		});

		alert.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// finish();
					}
				});

		alert.show();
	}
	
	@Override
	public void showmore(Object script) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.script_info, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.script_info);
		TextView et = (TextView)layout.findViewById(R.id.script_name);
	   	et.setText(((Script) script).getName());
	   	et = (TextView)layout.findViewById(R.id.script_command);
	   	et.setText(((Script) script).getCommand());
	   	et = (TextView)layout.findViewById(R.id.script_description);
	   	et.setText(((Script) script).getDescription());
	   	et = (TextView)layout.findViewById(R.id.script_execon);
	   	et.setText(((Script) script).getExecuteOnString());
	   	et = (TextView)layout.findViewById(R.id.script_type);
	   	et.setText(((Script) script).getTypeString());
	   	et = (TextView)layout.findViewById(R.id.script_usergroup);
	   	et.setText(((Script) script).getusrgrpid());
	   	et = (TextView)layout.findViewById(R.id.script_hostaccess);
	   	et.setText(((Script) script).getHost_access());
	   	
	   	alert.setView(layout)
	       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	           }
	       });
		
		alert.show();
	}
}
