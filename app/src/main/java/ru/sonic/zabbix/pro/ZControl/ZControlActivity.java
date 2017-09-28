package ru.sonic.zabbix.pro.ZControl;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * main window, display available tasks
 * @author dassinion
 */
public class ZControlActivity extends ActionBarActivity {
	/** Called when the activity is first created. */
	private static final int HOSTS=0;
	private static final int TEMPLATES=1;
	private static final int USERS=2;
	private static final int USERGROUPS=3;
	private static final int MEDIATYPES=4;
	private static final int HOSTGROUPS=5;
	private static final int ACTIONS=6;
	private static final int SCREENTS=7;
	private static final int SCRIPTS=8;
	private static final int PROXIES=9;
	private static final int MAINTENCES=10;
	private static final int APPLICATIONS=11;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zcontrol);
		ListView lv=(ListView)findViewById(R.id.configlistview);

		lv.setOnItemClickListener(mainMenuItemListener);
	}
	
	private OnItemClickListener mainMenuItemListener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case ACTIONS:
				Intent ActionsListActivity = new Intent(getBaseContext(),
						ActionsListActivity.class);
				startActivity(ActionsListActivity);
				break;
			case PROXIES:
				Intent ServerListActivity = new Intent(getBaseContext(),
						ProxiesActivity.class);
				startActivity(ServerListActivity);
				break;
			case SCREENTS:
				Intent checkspref = new Intent(getBaseContext(),
						ScreensListActivity.class);
				startActivity(checkspref);
				break;
			case SCRIPTS:
				Intent service = new Intent(getBaseContext(),
						ScriptsActivity.class);
				startActivity(service);
				break;
			case USERS:
				Intent other = new Intent(getBaseContext(),
						UsersActivity.class);
				startActivity(other);
				break;
			case APPLICATIONS:
				Intent applications = new Intent(getBaseContext(),
						ApplicationsActivity.class);
				startActivity(applications);
                break;
			case TEMPLATES:
				Intent templates = new Intent(getBaseContext(),
						TemplatesActivity.class);
				startActivity(templates);
                break;
			case USERGROUPS:
				Intent usgroup = new Intent(getBaseContext(),
						UserGroupsActivity.class);
				startActivity(usgroup);
                break;
			case HOSTS:
				Intent hostcontrol = new Intent(getBaseContext(),
						HostControlActivity.class);
				startActivity(hostcontrol);
                break;
			case MEDIATYPES:
				Intent mtypecontrol = new Intent(getBaseContext(),
						MediatypeControlActivity.class);
				startActivity(mtypecontrol);
                break;
			case HOSTGROUPS:
				Intent hgcontrol = new Intent(getBaseContext(),
						HostGroupsControl.class);
				startActivity(hgcontrol);
                break;
			case MAINTENCES:
				Intent maintencecontrol = new Intent(getBaseContext(),
						MaintencesListActivity.class);
				startActivity(maintencecontrol);
                break;
			default:
				Toast.makeText(getApplicationContext(), getResources().getString(
						R.string.not_yet_implemented) , Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	
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