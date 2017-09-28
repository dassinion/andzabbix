package ru.sonic.zabbix.pro.ZControl;

import java.util.Hashtable;
import java.util.List;

import org.json.simple.JSONArray;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.api.ScreenApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Screen;
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
import android.widget.AdapterView;
import android.widget.EditText;
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

			}
		});
		
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {    
                menu.setHeaderTitle(R.string.contentMenu);
                menu.add(0, 0, 0, R.string.more_info);
                menu.add(0, 1, 0, R.string.edit);
                menu.add(0, 2, 0, R.string.copy);
                menu.add(0, 3, 0, R.string.delete);
            }
		});
	}
	
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new ScreenApiHandler(this,getSelectedServer());
		return api.get();
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("create")) {
			return api.create((Screen) obj);
		} else if (operation.equals("delete")) {
			api.delete(((Screen) obj));
			return null;
		} else if (operation.equals("edit")) {
			api.edit(((Screen) obj));
			return null;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Create screen");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		create();
		return true;
	}
	
	@Override
	public void showmore(Object action) {
		String info = R.string.name+":	"+((Screen) action).getName();
		info += "\nH size:	"+((Screen) action).gethsize();
		info += "\nV size:	"+((Screen) action).getvsize();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(info)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	public void create() {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.create_screen);
			String text = "Params:";
			alert.setMessage(text);
			LayoutInflater inflater = LayoutInflater.from(this);
			final View layout = inflater.inflate(R.layout.screen_create, null);
			alert.setView(layout);

			alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					JSONArray screenitemsarray = new JSONArray();
					Hashtable<Object, Object> params = new Hashtable<Object, Object>();
					
					EditText et = (EditText)layout.findViewById(R.id.screenname);
					params.put("name",et.getText().toString());
					et = (EditText)layout.findViewById(R.id.screenHsize);
					params.put("hsize",et.getText().toString());
					et = (EditText)layout.findViewById(R.id.screenVsize);
					params.put("vsize",et.getText().toString());
					params.put("screenitems",screenitemsarray);
					Screen screen = new Screen(params);
					executeop("create", screen);
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
	public void edit(final Object obj) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.edit_screen);
			String text = "Params:";
			alert.setMessage(text);
			LayoutInflater inflater = LayoutInflater.from(this);
			final View layout = inflater.inflate(R.layout.screen_create, null);
			EditText et = (EditText)layout.findViewById(R.id.screenname);
			et.setText(((Screen) obj).getName());
			et = (EditText)layout.findViewById(R.id.screenHsize);
			et.setText(((Screen) obj).gethsize());
			et = (EditText)layout.findViewById(R.id.screenVsize);
			et.setText(((Screen) obj).getvsize());
			alert.setView(layout);

			alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					JSONArray screenitemsarray = new JSONArray();
					Hashtable<Object, Object> params = new Hashtable<Object, Object>();
					params.put("screenid",((Screen) obj).getID());
					EditText et = (EditText)layout.findViewById(R.id.screenname);
					params.put("name",et.getText().toString());
					et = (EditText)layout.findViewById(R.id.screenHsize);
					params.put("hsize",et.getText().toString());
					et = (EditText)layout.findViewById(R.id.screenVsize);
					params.put("vsize",et.getText().toString());
					//params.put("screenitems",screenitemsarray);
					Screen screen = new Screen(params);
					executeop("edit", screen);
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
}