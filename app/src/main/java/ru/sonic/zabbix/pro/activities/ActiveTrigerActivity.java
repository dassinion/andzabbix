package ru.sonic.zabbix.pro.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.TriggersAdapter;
import ru.sonic.zabbix.pro.api.EventApiHandler;
import ru.sonic.zabbix.pro.api.TriggerApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Event;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.base.Trigger;
import ru.sonic.zabbix.pro.config.ServerListActivity;
import ru.sonic.zabbix.pro.database.DBAdapter;
import ru.sonic.zabbix.pro.service.ZBXCheckService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * display active triggers in a listview
 * @author dassinion
 *
 */
public class ActiveTrigerActivity extends DefaultZabbixListActivity {
	protected static final int CONTEXTMENU_SHOWHtriggerPARAMS = 0;
	protected static final int CONTEXTMENU_DISABLE = 1;
	protected static final int CONTEXTMENU_DELETE = 2;
	protected static final int CONTEXTMENU_SHOWEVENTS = 3;
	protected static final int CONTEXTMENU_ACTIVATE = 4;
	protected static final int CONTEXTMENU_ACK = 5;

	private static final int WithLastEventUnacknowledged = 1;
	private static final int WithUnacknowledgedEvents = 2;
	private static final int WithAcknowledgedEvents = 3;
    protected static String TAG = "ActiveTrigerActivity";
	TimerTask mTimerTask;
	Timer t = new Timer();
	ActiveTrigerActivity ctx;
	private TriggerApiHandler api;
    TriggersAdapter triggerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        myList = (ListView) findViewById(android.R.id.list);
        data = new ArrayList<>();
		ctx = this;
		zabbixServers = getZabbixServers();
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String currlang = prefs.getString("language","default");
            if (currlang.equals("default")) {
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(defaultLocale);
                Configuration config = new Configuration();
                config.locale = defaultLocale;
                getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale(currlang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTitle = getTitle();

        if (firstStart() || getServersCount()==0) {
	    	Intent servlistActivity = new Intent(getBaseContext(),ServerListActivity.class);
	        startActivityForResult(servlistActivity,1);
		} else {
			setTitle(getResources().getString(R.string.active_trlist_title));

			final ListView lv=(ListView)findViewById(android.R.id.list);
			lv.setTextFilterEnabled(true);

			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					Trigger trigger =(Trigger)(lv.getAdapter().getItem(position));
					String hostid = trigger.getHostID();
					String hostname = trigger.getHost();
					Intent GraphListIntent = new Intent(getBaseContext(),GraphsListActivity.class);
					GraphListIntent.putExtra("hostid", hostid);
					GraphListIntent.putExtra("hostName", hostname);
					startActivity(GraphListIntent);
				}
			});

			lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
	            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
	                menu.setHeaderTitle(R.string.trigger_options);
	                menu.add(0, CONTEXTMENU_SHOWHtriggerPARAMS, 0, R.string.more_info);
	                menu.add(0, CONTEXTMENU_SHOWEVENTS, 0, R.string.show_events);
	                menu.add(0, CONTEXTMENU_DISABLE, 0, R.string.disable);
	                menu.add(0, CONTEXTMENU_DELETE, 0, R.string.delete);
	                menu.add(0, CONTEXTMENU_ACTIVATE, 0, R.string.activate);
	                menu.add(0, CONTEXTMENU_ACK, 0, R.string.ack);
   			  	            }
			});

			if (isautoref()) {
				doTimerTask(getAutorefPeriod());
			}
		}
		service_check();
	}

	protected void setListAdapter(android.widget.Adapter dataArray) {
		myList.setAdapter((ListAdapter) dataArray);
	}

	private List<Trigger> trigger_sort_by_status(List<Trigger> for_sort) {
		List<Trigger> ret = new ArrayList<Trigger>();
        Integer k = 0;
        for (k=0;k<for_sort.size();k++) {
            Trigger trig = for_sort.get(k);
            if (Integer.parseInt(trig.getActive()) == 1) {
                ret.add(trig);
            }
        }

        for (k=0;k<for_sort.size();k++) {
            Trigger trig = for_sort.get(k);
            if (Integer.parseInt(trig.getActive()) != 1) {
                ret.add(trig);
            }
        }

		return ret;
	}

	public void addListContent(List nlistitems) {
		if (nlistitems==null || nlistitems.size()==0){
			return;
		} else {
            data.addAll(nlistitems);
            if (triggerAdapter == null) {
                triggerAdapter = new TriggersAdapter(this, data);
                setListAdapter(triggerAdapter);
            }
            triggerAdapter.notifyDataSetChanged();
        }
	}

	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("create"))
			return api.create((Trigger) obj);
		else if (operation.equals("enable")) {
			((Trigger) obj).setStatus("0");
			return api.edit((Trigger) obj);
		} else if (operation.equals("disable")) {
			((Trigger) obj).setStatus("1");
			return api.edit((Trigger) obj);
		} else if (operation.equals("delete")) {
			api.delete(((Trigger) obj));
			return null;
		} else if (operation.equals("ack")) {
			ack_trigger(obj);
			return null;
		} else {
			return null;
		}
	}

	//TODO FIX MULTISERVER SUPPORT! ACK IS NOT WORK WITH SERVER NULL
	//TODO NEED TO ADD SERVER OBJECT
	private void ack_trigger(Object obj) {
		EventApiHandler eapi = new EventApiHandler(this,null);
		try {
			List<Event> events = eapi.get(((Trigger)obj).getID());
			if (events.size()>0) {
				Event ev0 = events.get(0);
				eapi.ackEvent(ev0.getID(), ((Trigger) obj).getAckMessage());
			} else
				throw new ZabbixAPIException("Events not found");
		} catch (ZabbixAPIException e) {
			e.printStackTrace();
		}
	}

	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	ListView lv=(ListView)findViewById(android.R.id.list);
    	Trigger obj=(Trigger)(lv.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
        	case CONTEXTMENU_SHOWHtriggerPARAMS:
        		showmore(obj);
			break;
        	case CONTEXTMENU_DISABLE:
        		disableDialog(obj);
        	break;
        	case CONTEXTMENU_DELETE:
        		deleteDialog(obj);
        	break;
        	case CONTEXTMENU_SHOWEVENTS:
				Intent events = new Intent(getBaseContext(),EventListActivity.class);
				events.putExtra("triggerId", obj.getID());
				events.putExtra("triggerDesc", obj.getDescription());
				startActivity(events);
        	break;
        	case CONTEXTMENU_ACTIVATE:
        		executeop("enable", obj);
    		break;
        	case CONTEXTMENU_ACK:
        		showAckAlertDialog(obj);
    		break;
    	}
        return true;
    }

	public void showAckAlertDialog(final Trigger trig) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText message = new EditText(context);
		message.setHint(getResources().getString(R.string.comments));
		builder.setMessage(getResources().getString(R.string.ack_latest_event) + trig.getID() + "?")
        .setCancelable(false)
	    .setView(message)
        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
	    {
            public void onClick(DialogInterface dialog, int id) {
            	trig.setAckMessage(message.getText().toString());
            	executeop("ack", trig);
				Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.event_acknowledged), Toast.LENGTH_LONG).show();
            }
        })
		.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

		AlertDialog alert = builder.create();
		alert.show();
	}


	public void disableDialog(final Trigger obj) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(R.string.disable_trigger) + obj.getDescription() + "?")
        .setCancelable(false)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	executeop("disable", obj);
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

	public void deleteDialog (final Trigger obj) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(getResources().getString(R.string.delete_trigger) + obj.getDescription() + "?")
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

	public boolean showunaskonly(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        return prefs.getBoolean("showunaskonly",false);
	}

	public boolean firstStart(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("firstStart",true);
	}

	public boolean isautoref(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("use_auto_reftriggers", false);
	}

	public int getAutorefPeriod(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(prefs.getString("autoref_period", "0"));
	}

	public boolean is_trigger_sort_by_status(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean filter_sort = prefs.getBoolean("sort_triggers", true);
        return filter_sort;
	}

   	@Override
 	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 	  super.onActivityResult(requestCode, resultCode, data);
	  	SharedPreferences config = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	    SharedPreferences.Editor configEditor = config.edit();
	    configEditor.putBoolean("firstStart", false);
	    configEditor.commit();
		//refreshData();
 	}

   	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	stopTask();
	    }
	    return super.onKeyDown(keyCode, event);
	}

   	public void doTimerTask(int period){
    	mTimerTask = new TimerTask() {
    	        public void run() {
    	                handler.post(new Runnable() {
    	                        public void run() {
    	                        	ctx.refreshData();
    	                        }
    	               });
    	        }};
    	t.schedule(mTimerTask, 30000, period * 1000);  //
    }

	public void stopTask(){
        if(mTimerTask!=null){
    	    mTimerTask.cancel();
    	}
    }

	public void showmore(Object trigger) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.trigger_info, null);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.trigger_information);
		TextView et = (TextView)layout.findViewById(R.id.trigger_description);
    	et.setText(((Trigger) trigger).getDescription());
    	et = (TextView)layout.findViewById(R.id.trigger_age);
    	et.setText(((Trigger) trigger).getAgeTime());
    	et = (TextView)layout.findViewById(R.id.trigger_priority);
    	et.setText(((Trigger) trigger).getPriorityString());
    	et = (TextView)layout.findViewById(R.id.trigger_running);
    	et.setText(((Trigger) trigger).getStatusYN());
    	et = (TextView)layout.findViewById(R.id.trigger_hostname);
    	et.setText(((Trigger) trigger).getHost());
    	et = (TextView)layout.findViewById(R.id.trigger_ask);
    	et.setText(((Trigger) trigger).getAckString());

    	if (((Trigger) trigger).getComments().equals("")) {
    		LinearLayout ll = (LinearLayout)layout.findViewById(R.id.trigger_comments_ll);
    		ll.setVisibility(View.GONE);
    	} else {
        	et = (TextView)layout.findViewById(R.id.trigger_comments);
        	et.setText(((Trigger) trigger).getComments());
    	}
    	ImageView iv = (ImageView)layout.findViewById(R.id.trigger_status_img);
    	iv.setImageResource(((Trigger) trigger).getValueImg());

		alert.setView(layout)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

		alert.show();
	}

    public void service_check(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("service_enable", false)) {
            startService(new Intent(this, ZBXCheckService.class));
        }
    }

    public boolean isCheckInternet() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("internet_check_fix",true);
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
     * start thread to refresh the data from the server
     */
    @Override
    public void refreshData() {
		if (isDebug) Log.d(TAG, "refreshData...");
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni!=null && ni.isAvailable() && ni.isConnected()) || !isCheckInternet()) {
			swipeRefreshLayout.setRefreshing(true);
            background = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
						if (is_support_multiserver()) {
							List<Server> srvlist = getZabbixServers();
							int n;
							//String activeServer =
							for (n=0;n<srvlist.size();n++) {
								Server srvname = srvlist.get(n);
								gettingTriggersFromServer(srvname);
							}
						} else {
							Server srvname = getSelectedServer();
							gettingTriggersFromServer(srvname);
						}
                    } catch (ZabbixAPIException e) {
                        if (isDebug) Log.e(TAG, "RPC Call failed: " + e);
                        Message msg=new Message();
                        msg.arg1=MSG_ERROR;
                        msg.obj=e;
                        handler.sendMessage(msg);
                    }
                }
            });
            background.start();
        } else {
			swipeRefreshLayout.setRefreshing(false);
            displayPopup(R.string.error, R.string.check_internet_connection);
        }
    }

    private void gettingTriggersFromServer(Server zsrv) throws ZabbixAPIException {
		api = new TriggerApiHandler(this,zsrv);
		if (isDebug) Log.d(TAG, "Server for searching: " + zsrv.getName());
        api.setActivetServerID(zsrv);
        if (showunaskonly()) {
            List<Trigger> last_unack = new ArrayList<Trigger>();
            last_unack = api.get(WithLastEventUnacknowledged);
            Message msg01=new Message();
            if (is_trigger_sort_by_status()) {
                msg01.arg1=MSG_DATA_RETRIEVED;
                msg01.obj=trigger_sort_by_status(last_unack);
                handler.sendMessage(msg01);
            } else {
                msg01.arg1=MSG_DATA_RETRIEVED;
                msg01.obj=last_unack;
                handler.sendMessage(msg01);
            }
        } else {
            Message msg02=new Message();
            List<Trigger> last_unack = new ArrayList<Trigger>();
            List<Trigger> all_unack = new ArrayList<Trigger>();
            List<Trigger> ret = new ArrayList<Trigger>();
            last_unack = api.get(WithLastEventUnacknowledged);
            msg02.arg1=MSG_DATA_RETRIEVED;
            msg02.obj=last_unack;
            handler.sendMessage(msg02);

            all_unack = api.get(WithUnacknowledgedEvents);
            int n,k;
            for (n=0;n<all_unack.size();n++) {
                boolean acknow = false;
                for (k=0;k<last_unack.size();k++) {
                    if (all_unack.get(n).getID().equals(last_unack.get(k).getID())) {
                        acknow = true;
                    }
                }
                if (!acknow)
                    ret.add(all_unack.get(n));
            }
            Message msg03=new Message();
            msg03.arg1=MSG_DATA_RETRIEVED;
            msg03.obj=ret;
            handler.sendMessage(msg03);

            Message msg04=new Message();
            msg04.arg1=MSG_DATA_RETRIEVED;
            msg04.arg2=9;
            msg04.obj=api.get(WithAcknowledgedEvents);
            handler.sendMessage(msg04);

        }
    }

    /**
     * the retriever thread cannot modify the gui after it's done
     * Therefore it justs sends the information (data or error) to the handler which again does the gui stuff
     */

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case MSG_ERROR:
                    try {
                        displayPopup(getResources().getString(R.string.error), ((ZabbixAPIException)msg.obj).getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
					completeRefreshing();
                    break;
                case MSG_DATA_RETRIEVED:
					addListContent((List<String>) msg.obj);
                    if (msg.arg2 == 9) {
						completeRefreshing();
                    }
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

    /**
     * start thread to refresh the data from the server
     * @throws ZabbixAPIException
     */
    public void executeop(final String operation, final Object obj) {
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

	@Override
	public void do_clear_list() {
		if (data!=null) {
			data.clear();
			if (triggerAdapter == null) {
				triggerAdapter = new TriggersAdapter(this, data);
				setListAdapter(triggerAdapter);
			}
			triggerAdapter.notifyDataSetChanged();
		}
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

	public Integer getServersCount() {
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		List<String> servers = db.selectServerNames();
		return servers.size();
	}
}