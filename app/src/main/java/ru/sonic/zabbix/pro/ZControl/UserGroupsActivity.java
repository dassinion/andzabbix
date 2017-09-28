package ru.sonic.zabbix.pro.ZControl;

import java.util.Hashtable;
import java.util.List;

import org.json.simple.JSONArray;


import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.api.UserGroupApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;

import ru.sonic.zabbix.pro.base.UserGroup;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserGroupsActivity extends DefaultZabbixListActivity {
	private UserGroupApiHandler api = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new UserGroupApiHandler(this,getSelectedServer());
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
                AdapterContextMenuInfo minfo = (AdapterContextMenuInfo) info;
                UserGroup obj=(UserGroup)(lv.getAdapter().getItem(minfo.position));
                if (obj.getbUsers_status())
                	menu.add(0, 4, 0, R.string.disable_group);
                else
                	menu.add(0, 4, 0, R.string.enable_group);
                if (obj.getbDebug_mode())
                	menu.add(0, 5, 0, R.string.disable_debug_mode);
                else
                	menu.add(0, 5, 0, R.string.enable_debug_mode);
                if (obj.getbGui_access())
                	menu.add(0, 6, 0, R.string.disable_gui_access);
                else
                	menu.add(0, 6, 0, R.string.enable_gui_access);
                if (obj.getApi_access() != null)
                	if (obj.getbApi_access())
                		menu.add(0, 7, 0, R.string.disable_api);
                	else
                		menu.add(0, 7, 0, R.string.enable_api);
            }
		});
	}
	
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new UserGroupApiHandler(this,getSelectedServer());
		return api.get();
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("create"))
			return api.create((UserGroup) obj);
		else if (operation.equals("enable_group")) {
			((UserGroup) obj).setStatus("0");
			return api.edit((UserGroup) obj);
		} else if (operation.equals("disable_group")) {
			((UserGroup) obj).setStatus("1");
			return api.edit((UserGroup) obj);
		} else if (operation.equals("enable_debug")) {
			((UserGroup) obj).setDebug("0");
			return api.edit((UserGroup) obj);
		} else if (operation.equals("disable_debug")) {
			((UserGroup) obj).setDebug("1");
			return api.edit((UserGroup) obj);
		} else if (operation.equals("enable_api")) {
			((UserGroup) obj).setApi("0");
			return api.edit((UserGroup) obj);
		} else if (operation.equals("disable_api")) {
			((UserGroup) obj).setApi("1");
			return api.edit((UserGroup) obj);
		} else if (operation.equals("enable_gui")) {
			((UserGroup) obj).setGuiAccess("0");
			return api.edit((UserGroup) obj);
		} else if (operation.equals("disable_gui")) {
			((UserGroup) obj).setGuiAccess("1");
			return api.edit((UserGroup) obj);
		} else if (operation.equals("delete")) {
			api.delete(((UserGroup) obj));
			return null;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.create_user_group);
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
    	Object obj=(Object)(lv.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
    	case 0:
    		showmore(obj);
    		break;
    	case 1:
    		edit(obj);
    		break;
    	case 2:
    		copy(obj);
    		break;
        case 3:
        	delete(obj);
			break;
        case 4:
        	if (((UserGroup) obj).getbUsers_status())
        		executeop("disable_group", obj);
        	else
        		executeop("enable_group", obj);
			break;
        case 5:
        	if (((UserGroup) obj).getbDebug_mode())
        		executeop("disable_debug", obj);
        	else
        		executeop("enable_debug", obj);
			break;
        case 6:
        	if (((UserGroup) obj).getbGui_access())
        		executeop("disable_gui", obj);
        	else
        		executeop("enable_gui", obj);
			break;
        case 7:
        	if (((UserGroup) obj).getbApi_access())
        		executeop("disable_api", obj);
        	else
        		executeop("enable_api", obj);
			break;
		default:
			break;
        }
    return true;
    }
	
	@Override
	public void create() {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.usergroup_create, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.create_user_group);
		alert.setView(layout);
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int whichButton) {
		    	EditText et = (EditText)layout.findViewById(R.id.usergroupname);
				Hashtable<Object, Object> params = new Hashtable<Object, Object>();
				params.put("name", et.getText().toString());
				UserGroup user = new UserGroup(params);
				executeop("create", user);
		    	dialog.dismiss();
			}
		});

		alert.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		final AlertDialog dialog = alert.create();
		dialog.show();
	}
	
	@Override
	public void showmore(Object usergroup) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.usergroup_info, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.usergroup_information);
		TextView et = (TextView)layout.findViewById(R.id.usergroup_name);
    	et.setText(((UserGroup) usergroup).getName());

    	ImageView iv = (ImageView)layout.findViewById(R.id.usergroup_debug);
    	iv.setImageResource(((UserGroup) usergroup).getDebugImg());
    	iv = (ImageView)layout.findViewById(R.id.usergroup_gui);
    	iv.setImageResource(((UserGroup) usergroup).getGuiImg());
    	iv = (ImageView)layout.findViewById(R.id.usergroup_status);
    	iv.setImageResource(((UserGroup) usergroup).getStatusImg());
    	
    	if (((UserGroup) usergroup).getApi_access()!=null) {
    		iv = (ImageView)layout.findViewById(R.id.usergroup_apiaccess);
    		iv.setImageResource(((UserGroup) usergroup).getApiImg());
    	} else {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.usergroup_apiaccess_ll);
    		ll.setVisibility(View.GONE);
    	}
    	
		alert.setView(layout)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
		
		alert.show();
	}
	
	@Override
	public void edit(Object obj) {
		//TODO interface of edit
		executeop("edit", obj);
	}
	
	@Override
	public void copy(Object obj) {
		//TODO interface of copy
		executeop("copy", obj);
	}
}
