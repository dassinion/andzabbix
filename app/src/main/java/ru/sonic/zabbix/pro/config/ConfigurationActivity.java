package ru.sonic.zabbix.pro.config;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.service.ZBXCheckService;

import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;

/**
 * main window, display available tasks
 * @author dassinion
 */
public class ConfigurationActivity extends ActionBarActivity {
	/** Called when the activity is first created. */
	private static final int SERVERCONFIG=0;
	private static final int INTERFACECONFIG=1;
	private static final int CHECKSCONFIG=2;
	private static final int SERVICE=3;
	private static final int OTHER=4;
	private static final int BUY_PRO=105;
	private static final int SUPPORT=5;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);

		ListView lv=(ListView)findViewById(R.id.configlistview);
		lv.setOnItemClickListener(mainMenuItemListener);
	}
	
	private OnItemClickListener mainMenuItemListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case SERVERCONFIG:
                /*if (!is_support_multiserver()) {
                    Intent serverEditActivity = new Intent(getBaseContext(), ServerActivity.class);
                    //serverEditActivity.putExtra("Servername", lv.getAdapter().getItem(position).toString());
                    serverEditActivity.putExtra("action", "edit");
                    startActivityForResult(serverEditActivity, 1);
                } else {*/
                    Intent ServerListActivity = new Intent(getBaseContext(),ServerListActivity.class);
                    startActivity(ServerListActivity);
                //}
				break;
				
			case INTERFACECONFIG:
				Intent generalConfig = new Intent(getBaseContext(),GeneralPrefActivity.class);
				startActivity(generalConfig);
				break;
				
			case CHECKSCONFIG:
				Intent checkspref = new Intent(getBaseContext(),ChecksPrefActivity.class);
				startActivity(checkspref);
				break;
				
			case SERVICE:
				Intent service = new Intent(getBaseContext(),ServicePrefActivity.class);
                startActivityForResult(service, 2);
				break;
				
			case OTHER:
				Intent other = new Intent(getBaseContext(),OtherPrefActivity.class);
				startActivity(other);
				break;
			case BUY_PRO:
				Intent buyintent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ru.sonic.zabbix.pro"));
                startActivity(buyintent);
                break;
			case SUPPORT:
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@deprosoft.com"});
				intent.putExtra(Intent.EXTRA_SUBJECT, "AndZabbix Pro support");
				intent.putExtra(Intent.EXTRA_TEXT, "");
				startActivity(Intent.createChooser(intent, "Send email."));
				break;
			default:
				Toast.makeText(getApplicationContext(),
						"Not yet implemented", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // complete set
        SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor configEditor = config.edit();
        configEditor.putBoolean("firstStart",false);
        configEditor.commit();

        if (requestCode==2) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getBoolean("service_enable", false)) {
                startService(new Intent(this, ZBXCheckService.class));
            }
        }
    }

    public boolean is_support_multiserver(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("multiserver_support",true);
    }
}
