package ru.sonic.zabbix.pro.ZControl;


import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.api.ScreenApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;

import android.app.Activity;
import android.app.AlertDialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
/**
 * display active triggers in a listview
 * 
 * @author dassinion
 * 
 */
public class ScreenActivity extends Activity {
	private static final String TAG = "ScreenActivity";
	private static final int MSG_DATA_RETRIEVED = 0;
	private static final int MSG_ERROR = 1;
	protected ScreenApiHandler api = null;
	private String itemID = "";
	String baseurl;
	//RelativeLayout mainPanel;
	private String screenurl = "/screens.php?elementid=";
	WebView screenView;
	Thread background;
	int mwidth,mheight;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.screen);
		setProgressBarIndeterminateVisibility(true);
		api = new ScreenApiHandler(this,null);
		//mainPanel = (RelativeLayout) findViewById(R.id.graphll);
		screenView = (WebView)findViewById(R.id.screenView);
		
		try {
			itemID = (String) getIntent().getExtras().get("screenID");
		} catch (Exception e) {}

		//graphWidth = getDisplayWidth();
		try {
			baseurl = api.getAPIURL().split("/api_jsonrpc.php")[0];
		} catch (ZabbixAPIException e) {
			e.printStackTrace();
		}
		try {
			refreshData();
		} catch (Exception e) {}
	}

	public void drawImage(Bitmap img) {
		//mapView.setImageBitmap(img);
	}

	public void displayErrorPopup(int message) {
		try {
			new AlertDialog.Builder(this).setMessage(getResources().getString(message)).setTitle(R.string.error).show();
		} catch (Exception e) {
			
		}
	}
	
	public void displayErrorPopup(String message) {
		try {
			new AlertDialog.Builder(this).setMessage(message).setTitle(R.string.error).show();
		} catch (Exception e) {
			
		}
	}

	public void refreshData() throws ZabbixAPIException {
		setProgressBarIndeterminateVisibility(true);
		loadurl();
		background = new Thread(new Runnable(){
			@Override
		    public void run() {
				/*
				Message msg = new Message();
				try {
					msg.obj = api.getScreen(baseurl+screenurl+itemID);
					msg.arg1 = MSG_DATA_RETRIEVED;
					handler.sendMessage(msg);
					//screenView.loadUrl(baseurl+screenurl+itemID);
				} catch (Exception e) {};
				*/
			}
		});
		background.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				setProgressBarIndeterminateVisibility(false);
			} catch (Exception e) {};
			switch (msg.arg1) {
			case MSG_ERROR:
				displayErrorPopup(((ZabbixAPIException) msg.obj).getMessage());
				break;
			case MSG_DATA_RETRIEVED:
				if (msg.obj!=null)
					screenView.loadData((String) msg.obj, "text/html", "utf-8");
				else 
					displayErrorPopup(R.string.cant_get_graph_image);
				break;
			default:
				break;
			}
		}
	};
	
	public void loadurl() throws ZabbixAPIException {
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeSessionCookie();
	    String cookieString = "zbx_sessionid="+api.getAPIAuth();
	    cookieManager.setCookie(baseurl, cookieString);
	    CookieSyncManager.getInstance().sync();
		screenView.getSettings().setJavaScriptEnabled(true);
		screenView.loadUrl(baseurl+screenurl+itemID);
	}
	
   	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Log.d(TAG,"Trying interrupt thread");
	    	background.interrupt();
	    	try {
	    		setProgressBarIndeterminateVisibility(false);
			} catch (Exception e) {};
	    }
	    return super.onKeyDown(keyCode, event);
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
}