package ru.sonic.zabbix.pro.ZControl;

import java.util.Hashtable;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.api.TemplateApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Template;
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
import android.widget.ListView;

public class TemplatesActivity extends DefaultZabbixListActivity {
	private TemplateApiHandler api = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new TemplateApiHandler(this,getSelectedServer());
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
            }
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new TemplateApiHandler(this,getSelectedServer());
		return api.get();
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("create"))
			return api.create((Template) obj);
		else if (operation.equals("delete")) {
			api.delete(((Template) obj));
			return null;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(0, 0, 0, "Create template");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		create();
		return true;
	}
	
	@Override
	public void showmore(Object template) {
		String info = R.string.name+":	"+((Template) template).getName();
		//info += "\nDns:	"+((Template) template).getdns();
		info += "\nStatus:	"+((Template) template).getStatusString();
		//info += "\nError:	"+((Template) template).geterror();
		//info += "\nAvailable:	"+((Template) template).get);
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
		alert.setTitle("Create template");
		String text = "Params:";
		alert.setMessage(text);
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.templates_create, null);
		alert.setView(layout);

		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Hashtable<Object, Object> params = new Hashtable<Object, Object>();
				JSONObject groupjson = new JSONObject();
				JSONArray grouparray = new JSONArray();
				if (groupjson.size()>0)
					grouparray.add(groupjson);
				
				JSONObject templatesjson = new JSONObject();
				JSONArray templatesarray = new JSONArray();
				if (templatesjson.size()>0)
					templatesarray.add(templatesjson);
				
				params.put("groups", grouparray);
				params.put("templates", templatesarray);
				params.put("host", "Template andzabbix");
				Template template = new Template(params);
				executeop("create", template);
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