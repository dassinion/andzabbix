package ru.sonic.zabbix.pro.activities;

import java.util.List;
import java.util.Vector;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.ZControl.ZControlActivity;
import ru.sonic.zabbix.pro.adapters.DefaultAdapter;
import ru.sonic.zabbix.pro.adapters.SliderAdapter;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.api.ZabbixAPIHandler;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.config.ConfigurationActivity;
import ru.sonic.zabbix.pro.database.DBAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * base listview with common code to retrieve a list of zabbix objects and display them
 * @author dassinion
 *
 */
public abstract class DefaultZabbixListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
	protected static String TAG = "DefaultZabbixListActivity";
	protected Context context = this;
	protected DefaultAdapter Adapter;
	protected DBAdapter db = new DBAdapter(this);
	protected Thread background;
	protected List data;
	protected DrawerLayout mDrawerLayout;
	protected ListView mDrawerMenu;
	protected ActionBarDrawerToggle mDrawerToggle;
	protected RelativeLayout mDrawerLeftLayout;
	protected ArrayAdapter<String> left_spinner_adapter;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected CharSequence mTitle;
	protected List<Server> zabbixServers;
	public ZabbixAPIHandler api;
	protected AlertDialog errorDialog;

	protected static final int CONTEXTMENU_SHOWINFO = 0;
	protected static final int CONTEXTMENU_EDIT = 1;
	protected static final int CONTEXTMENU_COPY = 2;
	protected static final int CONTEXTMENU_DELETE = 3;

	protected static final int MSG_DATA_RETRIEVED = 0;
	protected static final int MSG_ERROR = 1;
	protected static final int MSG_PULLREFRESH = 2;
	protected static final int MSG_DATA_CLEAR = 3;

	protected static final int REFRESH = 0;
	protected static final int NORMAL = 1;

    protected boolean mIsPullRefreshing = false;
	protected int draverSlideState = 0;
	public boolean isDebug;
 	ListView myList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_list);
		isDebug = true;
		myList = (ListView)findViewById(android.R.id.list);
		db.open();
        mTitle = getTitle();
		initialize_pull_to_refresh();
		restoreActionBar();
        initDrawerSlider();
		do_refresh();
	}

	public void initDrawerSlider() {
		if (isDebug) Log.d(TAG, "Initialize DrawerSlider");
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerMenu = (ListView) findViewById(R.id.left_drawer);
		mDrawerLeftLayout = (RelativeLayout) findViewById(R.id.left_driver_rlayout);
		//Log.d(TAG, "Init driver");
		if (mDrawerLayout == null) {
			Log.e(TAG, "Driver is NULL!!!!");
			return;
		}

		Spinner driver_spinner_zservers = (Spinner) findViewById(R.id.left_drawer_server_spinner);
		driver_spinner_zservers.setVisibility(View.VISIBLE);
		db.open();
		List<String> list = db.selectServerNames();
		left_spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		left_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		driver_spinner_zservers.setAdapter(left_spinner_adapter);
		//driver_spinner_zservers.setSelection(getPrefCurrentServer());
		String srvInPrefs = getPrefCurrentServer();
		int k = 0;
		for (String srvnm : list) {
			if (srvnm.equals(srvInPrefs)) {
				driver_spinner_zservers.setSelection(k);
			}
			k++;
		}

		SliderAdapter adapter = new SliderAdapter(getBaseContext(), getResources().getStringArray(R.array.SliderMenu));
		mDrawerMenu.setAdapter(adapter);

		mDrawerMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View item, int position, long id) {
				if (isDebug) Log.d(TAG, "position: " + position+", id: "+id);
				if (mDrawerLayout != null)
					mDrawerLayout.closeDrawers();

				switch (position) {
					case 0:
						Intent settingsActivity = new Intent(getBaseContext(), ActiveTrigerActivity.class);
						settingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(settingsActivity);
						break;
					case 1:
						settingsActivity = new Intent(getBaseContext(), ZControlActivity.class);
						settingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(settingsActivity);
						break;
					case 2:
						settingsActivity = new Intent(getBaseContext(), ConfigurationActivity.class);
						settingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(settingsActivity);
						break;
					case 3:
						settingsActivity = new Intent(getBaseContext(), HostListActivity.class);
						settingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(settingsActivity);
						break;
					case 4:
						settingsActivity = new Intent(getBaseContext(), GraphHostsActivity.class);
						settingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(settingsActivity);
						break;
					case 5:
						settingsActivity = new Intent(getBaseContext(), HostGroupActivity.class);
						settingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(settingsActivity);
						break;
					case 6:
						settingsActivity = new Intent(getBaseContext(), MapsListActivity.class);
						settingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(settingsActivity);
						break;
					case 7:
						settingsActivity = new Intent(getBaseContext(), FavoriteGraphsListActivity.class);
						settingsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(settingsActivity);
						break;
					default:
						break;
				}
			}
		});
		// Создадим drawer toggle для управления индикатором сверху
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.opened, R.string.closed) {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				if (slideOffset == 0 && draverSlideState == 1) {
					// drawer closed
					draverSlideState = 0;
					invalidateOptionsMenu();
				} else if (slideOffset != 0 && draverSlideState == 0) {
					// started opening
					draverSlideState = 1;
					invalidateOptionsMenu();
				}
				super.onDrawerSlide(drawerView, slideOffset);
			}
		};

		driver_spinner_zservers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if (mDrawerLayout != null)
					if (draverSlideState == 1) {
						mDrawerLayout.closeDrawers();
						Log.d(TAG, "Selected server:" + left_spinner_adapter.getItem(position));
						String server = left_spinner_adapter.getItem(position);
						//Server selectedZabbixServer = selectServer(server);
						setPrefCurrentServer(server);
						//api.setActivetServerID(selectedZabbixServer);
						//setProgressBarIndeterminateVisibility(true);
						Log.d(TAG, "onItemSelected Refresh server:" + server);
						do_refresh();
					}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}
		});

		// Назначим его drawer-у как слушателя
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// Для красоты добавим тень с той же гравитацией
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	}

	@Override
	public void onRefresh() {
		//if (isDebug) Log.d(TAG, "Swipe onRefresh action");
		do_refresh();
	}

	protected void initialize_pull_to_refresh() {
		if (isDebug) Log.d(TAG, "initialize_pull_to_refresh ");
        /*Swipe refresh*/
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
		swipeRefreshLayout.setOnRefreshListener(this);

		myList.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				boolean enable = false;
				if(myList != null && myList.getChildCount() > 0){
				//if(myList != null){
					// check if the first item of the list is visible
					boolean firstItemVisible = myList.getFirstVisiblePosition() == 0;
					// check if the top of the first item is visible
					boolean topOfFirstItemVisible = myList.getChildAt(0).getTop() == 0;
					// enabling or disabling the refresh layout
					enable = firstItemVisible && topOfFirstItemVisible;
				} else if (myList != null && myList.getChildCount() == 0) {
					enable = true;
				}
				swipeRefreshLayout.setEnabled(enable);
			}
		});
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDrawerLayout.isDrawerOpen(mDrawerLeftLayout)) {
            //restoreActionBar();
			//getMenuInflater().inflate(R.menu.menu, menu);
			//return true;
		}
        return super.onCreateOptionsMenu(menu);
    }

	/**
	 * fill the list
	 * @param data
	 */
	public void setListContent(List<?> data) {
		if (data==null || data.size()==0){
			Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_data_to_display), Toast.LENGTH_LONG).show();
			setListAdapter(null);
			return;
			}
        Adapter = new DefaultAdapter(this,data);
        setListAdapter( Adapter );
	}

	protected void setListAdapter(Adapter dataArray) {
		myList.setAdapter((ListAdapter) dataArray);
	}
	
	/**
	 * set current server
	 * @param 
	 */

    public String getPrefCurrentServer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String srvname = prefs.getString("currentServer", getResources().getString(R.string.not_selected));
        return srvname;
    }

	public Server getSelectedServer() {
		String srvname = getPrefCurrentServer();
		return db.selectServer(srvname);
	}

	public void setPrefCurrentServer(String server) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("currentServer", server);
		editor.commit();
	}

	public boolean isCheckInternet() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getBoolean("internet_check_fix",true);
	}

    public boolean is_support_multiserver(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("multiserver_support",false);
    }

	/**
	 * get and return the data objects to display
	 * subclasses should override this
	 * @return
	 * @throws ZabbixAPIException
	 */
	protected List<String> getData() throws ZabbixAPIException{
		return new Vector<String>();
	}
	
	/**
	 * start thread to refresh the data from the server
	 */
	public void refreshData() {
   		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
   		NetworkInfo ni = cm.getActiveNetworkInfo();
   		if ((ni!=null && ni.isAvailable() && ni.isConnected()) || !isCheckInternet()) {
			swipeRefreshLayout.setRefreshing(true);
			if (isDebug) Log.d(TAG, "Refreshing data...");
   			background = new Thread(new Runnable(){
   				@Override
   				public void run() {
				Message msg=new Message();
				try {
					List<?> data=getData();
					msg.arg1=MSG_DATA_RETRIEVED;
					msg.obj=data;
					handler.sendMessage(msg);
				} catch (ZabbixAPIException e) {
					if (isDebug) Log.e(TAG, "RPC Call failed: " + e);
					msg.arg1=MSG_ERROR;
					msg.obj=e;
					handler.sendMessage(msg);
					}
   				}
   			});
   			background.start();
		} else {
			swipeRefreshLayout.setRefreshing(false);
			displayPopup (R.string.error, R.string.check_internet_connection);
		}
	}

	/**
	 * the retriever thread cannot modify the gui after it's done
	 * Therefore it justs sends the information (data or error) to the handler which again does the gui stuff
	 */
	public Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (isDebug) Log.d(TAG,"Handle message: "+msg.arg1);
			switch (msg.arg1) {
			case MSG_ERROR:
				try {
					displayPopup(getResources().getString(R.string.error), ((ZabbixAPIException) msg.obj).getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
				completeRefreshing();
				break;
			case MSG_DATA_RETRIEVED:
				setListContent((List<String>)msg.obj);
				completeRefreshing();
				break;
			case MSG_PULLREFRESH:
                switch (msg.what) {
					case REFRESH:
						break;
                    case NORMAL:
                        break;
                }
            break;
				default:
		    break;
			}
		}
	};

	public void do_clear_list(){};

	public void do_refresh() {
		if (isDebug) Log.d(TAG,"startRefreshing");
		mIsPullRefreshing = true;
		swipeRefreshLayout.setEnabled(true);
		do_clear_list();
		refreshData();
	}

	public void completeRefreshing() {
		//handler.sendMessage(handler.obtainMessage(NORMAL, MSG_PULLREFRESH));
		if (isDebug) Log.d(TAG,"completeReflfreshing");
		mIsPullRefreshing = false;
		myList.invalidateViews();
		swipeRefreshLayout.setRefreshing(false);
	}

	/**
	 * @return array of Zabbix servers
	 */
	public List<Server> getZabbixServers() {
		DBAdapter db = new DBAdapter(context);
		if (db.open()!=null) {
			List<Server> zabbixServers = db.getAllServers();
			db.close();
			//if (isDebug) Log.d(TAG, "getStringPref return value: ");
			return zabbixServers;
		} else {
			//throw new ZabbixAPIException(R.string.unknown_database_error + "");
			return null;
		}

	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(mTitle);
	}

	/**
	 * start thread to refresh the data from the server
	 * @throws ZabbixAPIException 
	 */
	public void executeop(final String operation, final Object obj) {
		//setPrefCurrentServer();
   		Thread background = new Thread(new Runnable(){
   			@Override
   			public void run() {
   				Message msg=new Message();
   				try {
   					msg.arg1=MSG_DATA_RETRIEVED;
   					msg.obj=exec(operation,obj);
   					exechandler.sendMessage(msg);
				} catch (ZabbixAPIException e) {
					if (isDebug) Log.e(TAG, "RPC Call failed: " + e);
					msg.arg1=MSG_ERROR;
					msg.obj=e;
					exechandler.sendMessage(msg);
					}
   				}
   			});
   		background.start();
	}
	
	public Object exec(String operation, Object obj) throws ZabbixAPIException {
		return null;
	}
	
	/**
	 * the retriever thread cannot modify the gui after it's done
	 * Therefore it justs sends the information (data or error) to the handler which again does the gui stuff
	 */

	private Handler exechandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case MSG_ERROR:
				try {
					displayPopup(getResources().getString(R.string.error), ((ZabbixAPIException)msg.obj).getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case MSG_DATA_RETRIEVED:
				if (isDebug) Log.i(TAG, "ALL FINE ");
				refreshData();
				break;
			default:
				break;
			}
		}
	};
	
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
        	disable(obj);
			break;
        case 5:
        	enable(obj);
			break;
		default:
			break;
        }
     return true;
     }
	
	public void create() {
		displayPopup ("Sorry", "In the development");
	}
	
	public void showmore(Object obj) {
		displayPopup ("Sorry", "In the development");
	}
	
	public void edit(Object obj) {
		displayPopup ("Sorry", "In the development");
	}
	
	public void copy(Object obj) {
		displayPopup ("Sorry", "In the development");
	}

	public void delete(final Object obj) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.are_you_sure)
		.setTitle(R.string.delete)
        .setCancelable(false)
        .setInverseBackgroundForced(true)
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
	
	public void disable(Object obj) {
		executeop("disable", obj);
	}
	
	public void enable(Object obj) {
		executeop("enable", obj);
	}
	
	public void clearAuth(){
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	//Log.d(TAG, "onKeyDown KEYCODE_BACK");
	    	Message msg=new Message();
			msg.arg1=2;
			handler.sendMessage(msg);
	    }
	    return super.onKeyDown(keyCode, event);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mDrawerLayout.isDrawerOpen(mDrawerLeftLayout)) {
                    mDrawerLayout.closeDrawer(mDrawerLeftLayout);
                }
                else {
                    mDrawerLayout.openDrawer(mDrawerLeftLayout);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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

	/**
	 * display an error poup
	 *
	 * @param message
	 */
	public void displayPopup(String title, String message) {
		try {
			errorDialog = new AlertDialog.Builder(this)
					.setMessage(message)
					.setTitle(title)
					.create();
			errorDialog.show();

			Handler messagHandler = null;
			Message msg = new Message();

			messagHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case 1:
							if ((errorDialog != null) && errorDialog.isShowing())
								errorDialog.dismiss();
							break;
					}
				}
			};
			msg.what = 1;
			messagHandler.sendMessageDelayed(msg, 10000);
		} catch (Exception e) {
		}
	}

	public void displayPopup(int title, int message) {
		try {
			errorDialog = new AlertDialog.Builder(this)
					.setMessage(getResources()
							.getString(message))
					.setTitle(getResources()
							.getString(title))
					.show();
			Handler messagHandler = null;
			Message msg = new Message();

			messagHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case 1:
							if ((errorDialog != null) && errorDialog.isShowing())
								errorDialog.dismiss();
							break;
					}
				}

				;
			};
			msg.what = 1;
			messagHandler.sendMessageDelayed(msg, 10000);
		} catch (Exception e) {
		}
	}

	public Server selectServer(String serverName) {
		db.open();
		return db.selectServer(serverName);
	}

	@Override
	public void onPause() {
		super.onPause();
		if ((errorDialog != null) && errorDialog.isShowing())
			errorDialog.dismiss();
		errorDialog = null;
	}
}
