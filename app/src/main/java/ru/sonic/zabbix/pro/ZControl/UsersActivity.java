package ru.sonic.zabbix.pro.ZControl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.adapters.UserGroupAdapter;
import ru.sonic.zabbix.pro.api.UserApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.User;
import ru.sonic.zabbix.pro.base.UserGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UsersActivity extends DefaultZabbixListActivity {
	private UserApiHandler api = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new UserApiHandler(this,getSelectedServer());
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
		
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);
		
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
			api = new UserApiHandler(this,getSelectedServer());
		return api.get();
	}
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("delete")) {
			api.delete(((User) obj));
			return null;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.create_user);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		create();
		return true;
	}
	
	@Override
	public void create() {
		Intent userIntent = new Intent(getBaseContext(),UserCreateActivity.class);
		startActivityForResult(userIntent,1);
	}
	
	@Override
	public void showmore(Object user) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.user_info, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.user_information);
		TextView et = (TextView)layout.findViewById(R.id.user_alias);
	   	et.setText(((User) user).getalias());
	   	et = (TextView)layout.findViewById(R.id.user_autologintime);
	   	et.setText(((User) user).getautologin());
	   	et = (TextView)layout.findViewById(R.id.user_autologout);
	   	et.setText(((User) user).getautologout());
	   	et = (TextView)layout.findViewById(R.id.user_name);
	   	et.setText(((User) user).getName());
	   	et = (TextView)layout.findViewById(R.id.user_surname);
	   	et.setText(((User) user).getsurname());
	   	et = (TextView)layout.findViewById(R.id.user_refresh);
	   	et.setText(((User) user).getrefresh());
	   	et = (TextView)layout.findViewById(R.id.user_language);
	   	et.setText(((User) user).getlang());
	   	et = (TextView)layout.findViewById(R.id.user_type);
	   	et.setText(((User) user).getTypeString());
	   	
	   	if (!((User) user).geturl().equals("")) {
	   		et = (TextView)layout.findViewById(R.id.user_url);
	   		et.setText(((User) user).geturl());
	   	} else {
	   		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.user_url_ll);
	   		ll.setVisibility(View.GONE);
	   	}
	   	
	   	if (!((User) user).getbautologin()) {
	   		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.user_autologintime_ll);
	   		ll.setVisibility(View.GONE);
	   	} else {
	   		et = (TextView)layout.findViewById(R.id.user_autologintime);
	   		et.setText(((User) user).getautologin());
	   	}
	   	
	   	ImageView iv = (ImageView)layout.findViewById(R.id.user_autologin);
	   	iv.setImageResource(((User) user).getautologinImg());
		
	   	ListView groups = (ListView)layout.findViewById(R.id.user_groups);
	   	ArrayList<UserGroup> usergrpsarray = new ArrayList<UserGroup>();
		JSONArray usrgrpsjarray = ((User) user).getGroups();	
		Iterator keys = usrgrpsjarray.iterator();
		while(keys.hasNext()){
			JSONObject usrgrpsobj = (JSONObject) keys.next();
			UserGroup gr = new UserGroup(usrgrpsobj);
			usergrpsarray.add(gr);
		}
		
	   	UserGroupAdapter usrgrpsadapter = new UserGroupAdapter(this, usergrpsarray);
	   	groups.setAdapter(usrgrpsadapter);
	   	
		alert.setView(layout)
	       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	           }
	       });
		
		alert.show();
	}
	
	@Override
	public void edit(Object obj) {
		Intent userIntent = new Intent(getBaseContext(),UserCreateActivity.class);
		userIntent.putExtra("userid", ((User) obj).getID());
		userIntent.putExtra("action", "edit");
		startActivityForResult(userIntent,1);
	}
	
	@Override
	public void copy(Object obj) {
		Intent userIntent = new Intent(getBaseContext(),UserCreateActivity.class);
		userIntent.putExtra("userid", ((User) obj).getID());
		userIntent.putExtra("action", "copy");
		startActivityForResult(userIntent,1);
	}
	
  	@Override
 	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 	  super.onActivityResult(requestCode, resultCode, data);
		refreshData();
 	}
}
