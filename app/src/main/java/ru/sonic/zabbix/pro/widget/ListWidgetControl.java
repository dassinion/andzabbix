package ru.sonic.zabbix.pro.widget;

import java.util.List;

import ru.sonic.zabbix.pro.R;
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
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ListWidgetControl extends Activity {
    public static final String PREFS_NAME = "ZabbixWidgetPrefs";
    public static final String PREFS_WIDGET_SERVER = "ServerNme-%d";
    public static final String PREFS_UPDATE_RATE_FIELD_PATTERN = "UpdateRate-%d";
    //private static final int PREFS_UPDATE_RATE_DEFAULT = 30;
    private static final String TAG = "ListWidgetControl";
    private static final String WIDGETBOUGHT = "ListWidgetBought";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    IabHelper mHelper;
    boolean widgetbought = false;
    static final String WIDGET_LIST_ITEM = "widget_list_item";
    static final int RC_REQUEST = 10001;
    static final boolean isProVersion = true;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.config_txt_widget);
        
        if (isProVersion) {
        	widgetbought = true;
        	updateUi();
        } else {
        	loadData();
        }
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkpFDX7yhXPRXJe5fzsophLTS94BEC5Ym7z0M01vXVvX/12/LLkeubQAPG5jDGlE33dvqnNAIlsP4zM4/UQY+A77bXSrGkJealKLGdz9ZaGOlwM5VQuLTEn7eI7reBkUAT46H8S0fK98H5jCekwkNjZXU5q/G0uL/MHEKbVI4o1SrDtF1Jx0mOwN5hBwdBIcKXbljI3S+dwesjIXasTzdChEkkwTp/IMuWXOZQZdJriOZycGSgXAhUX3FYBXwouvf8+P0As6IxcEqXCUKL44rfPfEUD+zHtC6nvcbOK48V4C86SGmuXXRMo4zHlYkfeZ33VddKqPlhUqocR3R1jNMGwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        
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

        final Spinner servers = (Spinner)findViewById(R.id.server_spinner);
        List<String> serversnames = getServersList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, serversnames); 
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servers.setAdapter(adapter);
        
        final Spinner updateRateEntry = (Spinner)findViewById(R.id.update_rate_entry_spinner);
		
        Button saveButton = (Button)findViewById(R.id.buttonStart);
        saveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int updateRateSeconds = Integer.parseInt(getResources().getStringArray(R.array.UpdateIntervals)[updateRateEntry.getSelectedItemPosition()]);
                String serverName = servers.getSelectedItem().toString();

                SharedPreferences config = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor configEditor = config.edit();
                configEditor.putInt(String.format(PREFS_UPDATE_RATE_FIELD_PATTERN, appWidgetId), updateRateSeconds);
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
                    widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(ListWidgetProvider.URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId)));
                    PendingIntent newPending = PendingIntent.getBroadcast(getApplicationContext(), 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

                    // schedule the new widget for updating
                    //Log.d(LOG_TAG, "updateRateSeconds: " + updateRateSeconds* 1000 * 60+"\nMin:"+updateRateSeconds);
                    AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRateSeconds * 1000 * 60, newPending);
                }

                // activity is now done
                finish();
            }
        });
        
    	if (!isProVersion) {
	        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
	            public void onIabSetupFinished(IabResult result) {
	               Log.d(TAG, "Setup finished.");
	
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
    	Log.d(TAG, "onClickWidget ZabbixWidgetPrefs");
    }
    
    IabHelper.QueryInventoryFinishedListener mGotWidgetyListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
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
        String payload = "fsdnvxfcvnjdfv_desckn34cr9ecn_csmcm239c7dnjdfv_3247hf390fm34n8n_4387dh3dg43f9doi_dj4dnfg4f34ifh3489"; 
        
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
        return true;
    }
    
    void setWaitScreen(boolean set) {
        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }
    
    public void updateUi() {
    	if (isProVersion) {
	        ((Button)findViewById(R.id.buttonStart)).setEnabled(true);
	        findViewById(R.id.server_spinner).setEnabled(true);
	        findViewById(R.id.update_rate_entry_spinner).setEnabled(true);
	        findViewById(R.id.widget_buy).setVisibility(View.GONE);
	        findViewById(R.id.need_buy_txt).setVisibility(View.GONE);
    	} else {
    		((Button)findViewById(R.id.buttonStart)).setEnabled(widgetbought ? true : false);
	        findViewById(R.id.server_spinner).setEnabled(widgetbought ? true : false);
	        findViewById(R.id.update_rate_entry_spinner).setEnabled(widgetbought ? true : false);
	        findViewById(R.id.widget_buy).setVisibility(widgetbought ? View.GONE : View.VISIBLE);
	        findViewById(R.id.need_buy_txt).setVisibility(widgetbought ? View.GONE : View.VISIBLE);
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