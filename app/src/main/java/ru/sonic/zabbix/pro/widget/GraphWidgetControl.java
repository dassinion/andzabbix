package ru.sonic.zabbix.pro.widget;

import java.util.ArrayList;
import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.api.GraphApiHandler;
import ru.sonic.zabbix.pro.api.HostApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Graph;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.billing.IabHelper;
import ru.sonic.zabbix.pro.billing.IabResult;
import ru.sonic.zabbix.pro.billing.Inventory;
import ru.sonic.zabbix.pro.billing.Purchase;
import ru.sonic.zabbix.pro.database.DBAdapter;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class GraphWidgetControl extends Activity {
    public static final String PREFS_NAME = "ZabbixWidgetPrefs";
    public static final String PREFS_WIDGET_SERVER = "ServerNme-%d";
    public static final String PREFS_UPDATE_RATE_FIELD_PATTERN = "UpdateRate-%d";
    public static final String WIDGET_TYPE_PATTERN = "WidgetType-%d";
    public static final String WIDGET_GRAPH_ID = "WidgetGraphID-%d";
//    private static final int PREFS_UPDATE_RATE_DEFAULT = 30;
    private static final String TAG = "GraphWidgetControl";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Activity ctsx;
    private String hostID;
    List<Host> hosts;
    List<Graph> grpahs;
    IabHelper mHelper;
    private static final String WIDGETBOUGHT = "GraphWidgetBought";
    boolean widgetbought = false;
    static final String WIDGET_LIST_ITEM = "widget_graph_item";
    static final int RC_REQUEST = 10001;
    static final boolean isProVersion = true;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.config_graph_widget);
        ctsx = this;
        if (isProVersion) {
        	widgetbought = true;
        	updateUi();
        } else {
        	loadData();
        }
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkpFDX7yhXPRXJe5fzsophLTS94BEC5Ym7z0M01vXVvX/12/LLkeubQAPG5jDGlE33dvqnNAIlsP4zM4/UQY+A77bXSrGkJealKLGdz9ZaGOlwM5VQuLTEn7eI7reBkUAT46H8S0fK98H5jCekwkNjZXU5q/G0uL/MHEKbVI4o1SrDtF1Jx0mOwN5hBwdBIcKXbljI3S+dwesjIXasTzdChEkkwTp/IMuWXOZQZdJriOZycGSgXAhUX3FYBXwouvf8+P0As6IxcEqXCUKL44rfPfEUD+zHtC6nvcbOK48V4C86SGmuXXRMo4zHlYkfeZ33VddKqPlhUqocR3R1jNMGwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        // get any data we were launched with
        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Intent cancelResultValue = new Intent();
            cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_CANCELED, cancelResultValue);
        } else {
            finish();
        }

        Spinner servers = (Spinner)findViewById(R.id.widget_graph_server_spinner);
        List<String> serversnames = getServersList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, serversnames); 
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servers.setAdapter(adapter);
        
        servers.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
					//String server = (String) parent.getSelectedItem();
					threadHostsStart();
			}
	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
        
        Spinner hostspin = (Spinner)findViewById(R.id.widget_graph_host_spinner);
        hostspin.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
					//String host = (String) parent.getSelectedItem();
				int selectedhostpos =  parent.getSelectedItemPosition();
				if (hosts!=null && hosts.size()>0) {
					hostID = hosts.get(selectedhostpos).getID();
					threadGraphsStart();
				}
			}
	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
        Button saveButton = (Button)findViewById(R.id.widget_graph_save_btn);
        saveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
	            	Spinner updateRateEntry = (Spinner)findViewById(R.id.widget_graph_update_rate_entry_spinner);
	                int updateRateSeconds = Integer.parseInt(getResources().getStringArray(R.array.UpdateIntervals)[updateRateEntry.getSelectedItemPosition()]);
	                //Log.d(TAG, "updateRateSeconds: " + updateRateSeconds);
	                Spinner servers = (Spinner)findViewById(R.id.widget_graph_server_spinner);
	                Spinner graphspinner = (Spinner)findViewById(R.id.widget_graph_graph_spinner);
	                if (servers.getAdapter().getCount() > 0 && graphspinner.getAdapter().getCount() > 0) {
	                	String serverName = servers.getSelectedItem().toString();
	                	//Log.d(TAG, "serverName: " + serverName);
	                
		                int selectedGraph = graphspinner.getSelectedItemPosition();
		                //Log.d(TAG, "selectedGraph: " + selectedGraph);
		                String graphID = grpahs.get(selectedGraph).getID();
		                //Log.d(TAG, "graphID: " + graphID);
		                
		                SharedPreferences config = getSharedPreferences(PREFS_NAME, 0);
		                SharedPreferences.Editor configEditor = config.edit();
		                configEditor.putInt(String.format(PREFS_UPDATE_RATE_FIELD_PATTERN, appWidgetId), updateRateSeconds);
		                configEditor.putString(String.format(WIDGET_GRAPH_ID, appWidgetId), graphID);
		                configEditor.putString(String.format(PREFS_WIDGET_SERVER, appWidgetId), serverName);
		                configEditor.commit();
		
		                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
		
		                    // tell the app widget manager that we're now configured
		                    Intent resultValue = new Intent();
		                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		                    setResult(RESULT_OK, resultValue);
		
		                    Intent widgetUpdate = new Intent();
		                    widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		                    widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });
		
		                    // make this pending intent unique
		                    widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(GraphWidgetProvider.URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId)));
		                    PendingIntent newPending = PendingIntent.getBroadcast(getApplicationContext(), 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
		
		                    // schedule the new widget for updating
		                    //Log.d(LOG_TAG, "updateRateSeconds: " + updateRateSeconds* 1000 * 60+"\nMin:"+updateRateSeconds);
		                    AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		                    alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRateSeconds * 1000 * 60, newPending);
		                }
	                }
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
                finish();
            }
        });
        
       	if (!isProVersion) {
	        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
	            public void onIabSetupFinished(IabResult result) {
	               // Log.d(TAG, "Setup finished.");
	
	                if (!result.isSuccess()) {
	                    return;
	                }
	
	                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
	                //Log.d(TAG, "Setup successful. Querying bought widget.");
	                mHelper.queryInventoryAsync(mGotWidgetyListener);
	            }
	        });
    	}
    }
    
    public List<String> getServersList() {
		DBAdapter db = new DBAdapter(this);
		db.open();
		List<String> Servers = db.selectServerNames();
		db.close();
		return Servers;
	}
        
    public void onClickWidget(View view) {
    	//Log.d(LOG_TAG, "onClickWidget ZabbixWidgetPrefs");
    }
    
    public void threadHostsStart() {
    	Thread background = new Thread(new Runnable(){
			@Override
			public void run() {
				Message msg=new Message();
				try {
					HostApiHandler hostsapi = new HostApiHandler(ctsx,null);
			    	List<String> hostnames = new ArrayList<String>();
					hosts = hostsapi.get("");
					for (Host host: hosts) {
						hostnames.add(host.getName());
					}
					msg.arg1=0;
					msg.obj=hostnames;;
					hostHandler.sendMessage(msg);
				} catch (ZabbixAPIException e) {
					msg.arg1=1;
					msg.obj=e;
					hostHandler.sendMessage(msg);
				}
			}
		});
		background.start();
    }
    
    private Handler hostHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case 1:
				//
				break;
			case 0:
				Spinner hostsspinner = (Spinner)findViewById(R.id.widget_graph_host_spinner);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, (List<String>) msg.obj); 
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        hostsspinner.setAdapter(adapter);
				break;
			default:
				break;
			}
		}
	};
	
	public void threadGraphsStart() {
    	Thread background = new Thread(new Runnable(){
			@Override
			public void run() {
				Message msg=new Message();
				try {
					GraphApiHandler api = new GraphApiHandler(ctsx,null);
					List<String> graphnames = new ArrayList<String>();
					grpahs = api.get(hostID);
					for (Graph graph: grpahs) {
						graphnames.add(graph.getName());
					}
					msg.arg1=0;
					msg.obj=graphnames;;
					graphsHandler.sendMessage(msg);
				} catch (ZabbixAPIException e) {
					msg.arg1=1;
					msg.obj=e;
					graphsHandler.sendMessage(msg);
				}
			}
		});
		background.start();
    }
    
    private Handler graphsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case 1:
				//
				break;
			case 0:
				Spinner graphspinner = (Spinner)findViewById(R.id.widget_graph_graph_spinner);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, (List<String>) msg.obj); 
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        graphspinner.setAdapter(adapter);
				break;
			default:
				break;
			}
		}
	};
	
	IabHelper.QueryInventoryFinishedListener mGotWidgetyListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            //Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                //complain("Failed to query inventory: " + result);
                return;
            }

            //Log.d(TAG, "Query inventory was successful.");
            
            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(WIDGET_LIST_ITEM);
            widgetbought = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "Widget is " + (widgetbought ? "BOUGHT" : "NOT BOUGHT"));

            updateUi();
            setWaitScreen(false);
            //Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    
    public void onBuyWidgetButtonClicked(View arg0) {
        if (widgetbought) {
            complain("No need! You're already bought widget.");
            return;
        }
        setWaitScreen(true);
        String payload = "sfpvi_jd347h4f87ff_fji8fhefbsvw45osdig_fjefh34hfkhfduihfgmcviuhf_aslpsw3kc3c4mcba303484_djk348h34111"; 
        
        mHelper.launchPurchaseFlow(this, WIDGET_LIST_ITEM, RC_REQUEST, mPurchaseFinishedListener, payload);
    }
    
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(WIDGET_LIST_ITEM)) {
                // bought the premium upgrade!
                //Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you!");
                widgetbought = true;
                updateUi();
                setWaitScreen(false);
            }
        }
    };
    
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        //TODO need to check pay
        return true;
    }
    
    void setWaitScreen(boolean set) {
        findViewById(R.id.screen_main_graph).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait_graph).setVisibility(set ? View.VISIBLE : View.GONE);
    }
    
    public void updateUi() {
    	if (isProVersion) {
	        ((Button)findViewById(R.id.widget_graph_save_btn)).setEnabled(true);
	        findViewById(R.id.widget_graph_server_spinner).setEnabled(true);
	        findViewById(R.id.widget_graph_update_rate_entry_spinner).setEnabled(true);
	        findViewById(R.id.widget_graph_host_spinner).setEnabled(true);
	        findViewById(R.id.widget_graph_graph_spinner).setEnabled(true);
	        findViewById(R.id.widget_graph_buy).setVisibility(View.GONE);
	        findViewById(R.id.need_buy_graph_txt).setVisibility(View.GONE);
    	} else {
    		((Button)findViewById(R.id.widget_graph_save_btn)).setEnabled(widgetbought ? true : false);
	        findViewById(R.id.widget_graph_server_spinner).setEnabled(widgetbought ? true : false);
	        findViewById(R.id.widget_graph_update_rate_entry_spinner).setEnabled(widgetbought ? true : false);
	        findViewById(R.id.widget_graph_host_spinner).setEnabled(widgetbought ? true : false);
	        findViewById(R.id.widget_graph_graph_spinner).setEnabled(widgetbought ? true : false);
	        findViewById(R.id.widget_graph_buy).setVisibility(widgetbought ? View.GONE : View.VISIBLE);
	        findViewById(R.id.need_buy_graph_txt).setVisibility(widgetbought ? View.GONE : View.VISIBLE);
    	}
    }
    
    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }
    
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
    
    void saveData() {
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putBoolean(WIDGETBOUGHT, widgetbought);
        spe.commit();
    }
    
    void loadData() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        widgetbought = sp.getBoolean(WIDGETBOUGHT, false);
    }
}