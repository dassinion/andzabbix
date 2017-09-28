package ru.sonic.zabbix.pro.ZControl;

import java.util.Hashtable;

import java.util.List;

import org.json.simple.JSONArray;


import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;

import ru.sonic.zabbix.pro.api.ApplicationApiHandler;

import ru.sonic.zabbix.pro.api.ZabbixAPIException;

import ru.sonic.zabbix.pro.base.Application;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
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
public class ApplicationsActivity extends DefaultZabbixListActivity {
	private ApplicationApiHandler api = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new ApplicationApiHandler(this,getSelectedServer());
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
                menu.add(0, CONTEXTMENU_SHOWINFO, 0, R.string.more_info);
                //menu.add(0, CONTEXTMENU_EDIT, 0, "Edit");
                //menu.add(0, CONTEXTMENU_COPY, 0, "Copy");
                menu.add(0, CONTEXTMENU_DELETE, 0, R.string.delete);
            }
		});
	}
	
	@Override
	protected List getData() throws ZabbixAPIException {
		//if (api==null)
		//	api = new ApplicationApiHandler(this);
		return api.get();
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("create"))
			return api.create((Application) obj);
		//else if (operation.equals("enable")) {
			//((Application) obj).setStatus("0");
			//return api.edit((Application) obj);
		//} else if (operation.equals("disable")) {
			//((Application) obj).setStatus("1");
			//return api.edit((Application) obj);
		else if (operation.equals("delete")) {
			api.delete((Application) obj);
			return null;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.create);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		create();
		return true;
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	ListView lv=(ListView)findViewById(android.R.id.list);
    	Application obj=(Application)(lv.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
        	case CONTEXTMENU_SHOWINFO:
        		showmore(obj);
				break;
        	case CONTEXTMENU_DELETE:
        		executeop("delete", obj);
        		break;
        	case CONTEXTMENU_EDIT:
        		executeop("edit", obj);
        		break;
        	case CONTEXTMENU_COPY:
        		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
        		params.put("name","andzabbix test");
        		obj = new Application(params);
        		executeop("create", obj);
				break;
        	default:
    		break;
    	}
    return true;
    } 
	
	public void delete(final Application obj) {
		final String applID = obj.getID();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delete_application +" "+ obj.getName() + "?")
        .setCancelable(false)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	executeop("delete", obj);
            }
        })
		
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
		});
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void showEditAlertDialog(Application appl) {
		Toast.makeText(getApplicationContext(),
				"Coming soon", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void showmore(Object action) {
		String info = R.string.name + "	"+((Application) action).getName()+"\n";
		info += R.string.host + ":	"+((Application) action).getHost();
		info += "\nID:	"+((Application) action).getID()+"\n";
		info += R.string.templates + "id:	"+((Application) action).getTemplateid();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(info)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
		
		AlertDialog alert = builder.create();
		alert.show();
	}
}
