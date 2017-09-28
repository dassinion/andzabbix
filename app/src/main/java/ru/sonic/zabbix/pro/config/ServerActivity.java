package ru.sonic.zabbix.pro.config;

import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.database.DBAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ServerActivity extends Activity {
	int ERROR;
	DBAdapter db = new DBAdapter(this);
	String user,pass,timeout,url,srvname,action,oldname,base_login,base_pass;
	boolean base_use;
	EditText prefServerName,prefServerUrl,prefServerUser,prefServerPass,base_auth_user,base_auth_pass;
	Spinner prefConnTimeout;
	
	CheckBox use_base_auth;

   @SuppressWarnings("unchecked")
@Override
   protected void onCreate(final Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   Log.d(this.getClass().getName(),"Start activity ");
	   Bundle extras = getIntent().getExtras();
       db.open();
       if (is_support_multiserver()) {
           srvname = extras.getString("Servername");
       } else {
           srvname=db.getFirstServerName();
       }
       //srvname = "Any name";
	   action = extras.getString("action");
	   setContentView(R.layout.serverpref_layout);
	   setTitle(getResources().getString(R.string.server_configuration));
	   
	   Toast.makeText(getApplicationContext(), getResources().getString(R.string.press_back_to_save_chanes), Toast.LENGTH_SHORT).show();
	   
	   Log.d(this.getClass().getName(),"Get servername: " + srvname);
        if (action.equals("new")) {
			srvname = "Zabbix server";
			url = "http://example.com/zabbix/api_jsonrpc.php";
			user = "user";
			pass = "";
			timeout = "10";
		} else {
			Server zabbixServer = db.selectServer(srvname);
			db.close();

			oldname = srvname;
			srvname = zabbixServer.getName();
			url = zabbixServer.geturl();
			user = zabbixServer.getlogin();
			pass = zabbixServer.getpass();
			timeout = zabbixServer.gettimeout();
			base_use = zabbixServer.getbaseauth_use();
			base_login = zabbixServer.getbaseauth_login();
			base_pass = zabbixServer.getbaseauth_pass();
		}
		
		use_base_auth =  (CheckBox)findViewById(R.id.use_base_auth);
		prefServerName = (EditText)findViewById(R.id.prefServerName);
		prefServerUrl = (EditText)findViewById(R.id.prefServerUrl);
		prefServerUser = (EditText)findViewById(R.id.prefServerUser);
		prefServerPass = (EditText)findViewById(R.id.prefServerPass);
		prefConnTimeout = (Spinner)findViewById(R.id.prefConnTimeout);
		base_auth_user = (EditText)findViewById(R.id.base_auth_user);
		base_auth_pass = (EditText)findViewById(R.id.base_auth_pass);
		
		ArrayAdapter<String> myAdap = (ArrayAdapter<String>) prefConnTimeout.getAdapter();
		int spinnerPosition = myAdap.getPosition(timeout);
		
		prefServerName.setText(srvname);
		prefServerUrl.setText(url);
		prefServerUser.setText(user);
		prefServerPass.setText(pass);
		prefConnTimeout.setSelection(spinnerPosition);
		base_auth_user.setText(base_login);
		base_auth_pass.setText(base_pass);
		
        if (base_use) {
        	use_base_auth.setChecked(true);
        	check_base_auth(true);
        } else {
        	use_base_auth.setChecked(false);
        	check_base_auth(false);
        }
        
        use_base_auth.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                	check_base_auth(true);
                else
                	check_base_auth(false);
            }
        });
    }
   
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        //moveTaskToBack(true);
	        //return true;
	    	url = prefServerUrl.getText().toString();
            base_login = base_auth_user.getText().toString();
            if (base_use && base_auth_pass.getText().toString().equals("")) {
                WrongBaseAuthPassAlertDialog();
            }
    		if (url.endsWith("api_jsonrpc.php"))
    			saveServerToDB(url);
    		else
    			WrongUrlAlertDialog(url);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("currentServer", prefServerName.getText().toString());
            editor.commit();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private void WrongUrlAlertDialog(final String url) {
		AlertDialog.Builder ad;
		
		ad = new AlertDialog.Builder(this);
		ad.setTitle(R.string.attention);
		ad.setMessage(R.string.wrong_url_attention_message);
		ad.setPositiveButton(R.string.yes, new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				saveServerToDB(url);
			}
		});
		ad.setNegativeButton(R.string.no, new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				saveServerToDB(url+"/api_jsonrpc.php");
			}
		});
		
		ad.setCancelable(true);
		ad.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				
			}
		});
		
		ad.show();
	}

    private void WrongBaseAuthPassAlertDialog() {
        AlertDialog.Builder ad;

        ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.attention);
        ad.setMessage(R.string.base_auth_warning_message);
        ad.setPositiveButton(R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                // nothing
            }
        });

        ad.setCancelable(true);
        ad.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

            }
        });

        ad.show();
    }
	
	private void saveServerToDB(String url) {
		srvname = prefServerName.getText().toString().replace("'","");
    	user = prefServerUser.getText().toString();
    	pass = prefServerPass.getText().toString();
    	timeout = prefConnTimeout.getSelectedItem().toString();
    	base_use = use_base_auth.isChecked();
    	int base_use_flag = 0;
    	if (base_use) 
    		base_use_flag = 1;
    	base_login = base_auth_user.getText().toString();
        base_pass = base_auth_pass.getText().toString();
        //Log.d(this.getClass().getName(), "Selected Item: "+srvname);
    	db.open();
    	if (action.equals("new")) {
    		List<String> slist = db.selectServerNames();
    		for (String serverInList : slist) {
    			if (serverInList.equals(srvname)) { ERROR = 1;}
    		}
    		if (ERROR!=1) {
    		db.insertServer(srvname,url,user,pass,timeout,base_use_flag, base_login, base_pass);
    		} else {
    			Log.e(this.getClass().getName(), getResources().getString(R.string.server_already_exist));
    		}
    	} else if (action.equals("edit")) {
    		db.updateServer(oldname,srvname,url,user,pass,timeout,base_use_flag, base_login, base_pass);
    	}

    	db.close();
		Log.d(this.getClass().getName(), "Data inserted");
    	finish();
	}
	
	public void check_base_auth(boolean checked) {
		TextView base_auth_user_txt = (TextView)findViewById(R.id.base_auth_user_txt);
		TextView base_auth_pass_txt = (TextView)findViewById(R.id.base_auth_pass_txt);
		if (!checked) {
			base_auth_user.setVisibility(View.GONE);
			base_auth_pass.setVisibility(View.GONE);
			base_auth_user_txt.setVisibility(View.GONE);
			base_auth_pass_txt.setVisibility(View.GONE);
		} else {
			base_auth_user.setVisibility(View.VISIBLE);
			base_auth_pass.setVisibility(View.VISIBLE);
			base_auth_user_txt.setVisibility(View.VISIBLE);
			base_auth_pass_txt.setVisibility(View.VISIBLE);
		}
	}

    public boolean is_support_multiserver(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("multiserver_support",true);
    }
}
