package ru.sonic.zabbix.pro.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.api.GraphApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.RangeGraph;
import ru.sonic.zabbix.pro.base.RangeGraph.onSeekBarChangeListener;
import ru.sonic.zabbix.pro.base.Server;
import ru.sonic.zabbix.pro.database.DBAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

/**
 * display active triggers in a listview
 * 
 * @author dassinion
 * 
 */
public class ChartActivity extends Activity {
	private static final String TAG = "ZabbixCharts";
	private static final int MSG_DATA_RETRIEVED = 0;
	private static final int MSG_ERROR = 1;
	private String itemID = "";
	private String imageurl;
	RelativeLayout mainPanel;
	private int graphWidth = 0;
	private int period = 3600;
	private long istime = 0;
	private String baseurl = "";
	private String chart_type_items = "/chart.php?itemids=";
    private String chart_type_item = "/chart.php?itemid=";
	private String chart_type_graph = "/chart2.php?graphid=";
	private String charturl = chart_type_graph;
	//ImageView imageView;
	RangeGraph imageGraph;
	Thread background;
	private GraphApiHandler api = null;
	ProgressBar pb;
	private Server zabbixServer;
	protected DBAdapter db = new DBAdapter(this);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.graph);
		setProgressBarIndeterminateVisibility(true);

		mainPanel = (RelativeLayout) findViewById(R.id.graphll);
		imageGraph = (RangeGraph)findViewById(R.id.graphView1);
		pb = (ProgressBar)findViewById(R.id.progressBar1);
		pb.setVisibility(View.GONE);
		try {
			Bundle extras = getIntent().getExtras();
			itemID = (String) extras.get("graphID");
			String servername = extras.getString("servername");
			db.open();
			zabbixServer = db.selectServer(servername);
			api = new GraphApiHandler(this,zabbixServer);
            String apiVersion = api.getApiVersion();
			int chart_type = (Integer) extras.get("chart_type");
            Log.i(TAG,"get chart_type: "+chart_type);
			if (chart_type==0) {
				charturl = chart_type_graph;
			} else {
                if (apiVersion.contains("2.4.")) {
                    charturl = chart_type_items;
                } else if ((apiVersion.contains("2.2.")) || (apiVersion.contains("2.0.")) || (apiVersion.contains("1.8."))) {
                    charturl = chart_type_item;
                } else {
                    charturl = chart_type_items;
                }
			}
		} catch (Exception e) {}

		graphWidth = getDisplayWidth();
		try {
			baseurl = api.getAPIURL().split("/api_jsonrpc.php")[0];
		} catch (ZabbixAPIException e) {
			e.printStackTrace();
		}
		refreshData();

		ZoomControls zoomGrapgControls = (ZoomControls) findViewById(R.id.zoomGrapgControls);

		zoomGrapgControls.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (istime==0) {
					long endtime = System.currentTimeMillis()/1000;
					istime = endtime - period;
					period = period / 2;
				} else {
					period = period / 2;
					//istime = istime + period/2;
				}
				istime = istime + period/2;
				if (period<3600)
					period=3600;
				refreshData();
			}
		});
		zoomGrapgControls.setOnZoomOutClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (istime==0) {
					long endtime = System.currentTimeMillis()/1000;
					istime = endtime - period;
					istime = istime - period/2;
					period = period * 2;
				} else {
					istime = istime - period/2;
					period = period * 2;
					if (istime+period>System.currentTimeMillis()/1000)
						istime = 0;
				}
				refreshData();
			}
		});
	
		imageGraph.setOnSeekBarChangeListener(new onSeekBarChangeListener() {
			@Override
			public void onSeekBarValueChanged(int start, int end) {
				//Log.i(TAG,"Start: "+start+", End: "+end);
				long endtime = System.currentTimeMillis()/1000;
				
				if (istime+period>endtime)
					istime=0;
				
				if (istime!=0)
					endtime = istime + period;
				
				long starttime = endtime - period;
				int perproc = (int) ((endtime - starttime)/100);
				istime = (perproc * start) + starttime;
				//Log.i(TAG,"Sstime: "+istime);
				period = (int) ((perproc * end) + starttime - istime);
				
				if (period<3600)
					period=3600;
				//Log.i(TAG,"new Period: "+period);
				refreshData();
			}
		});
	}
	
	private String makeFullUrl (int gwidth, long gstime, int gperiod) {
		String fullURL = "";
        String chartGrapgID = charturl + itemID;
        //Log.d(TAG,"makeFullUrl chartGrapgID: "+chartGrapgID);
		String widthurl =  "&width="+ gwidth;
		String stimeurl = "";
		if (gstime!=0) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			stimeurl = "&stime=" + dateFormat.format(gstime*1000);
		}
		String imageurlperiod = "&period=" + gperiod;
		fullURL = baseurl + chartGrapgID + widthurl + stimeurl + imageurlperiod;
		//Log.d(TAG,"URL: "+fullURL);
		return fullURL;
	}

	public void drawImage(Bitmap img) {
		imageGraph.setImageBitmap(img);
	}

	private int getDisplayWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		return width;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void displayErrorPopup(String message) {
		try {
			new AlertDialog.Builder(this).setMessage(message).setTitle("Error").show();
		} catch (Exception e) {
			
		}
	}

	@SuppressLint({ "HandlerLeak", "HandlerLeak", "HandlerLeak" })
	public void refreshData() {
		pb.setVisibility(View.VISIBLE);
		if (istime+period > System.currentTimeMillis()/1000) {
			istime = 0;
		}
		
		background = new Thread(new Runnable(){
			@SuppressLint("HandlerLeak")
			@Override
		    public void run() {
				Message msg = new Message();
				try {
					imageurl = makeFullUrl(graphWidth,istime,period);
					Bitmap img = api.getGraphImage(imageurl);
					msg.obj = img;
					msg.arg1 = MSG_DATA_RETRIEVED;
					handler.sendMessage(msg);
				} catch (ZabbixAPIException e) {
					Log.e(TAG, "rpc call failed: " + e);
					msg.arg1 = MSG_ERROR;
					msg.obj = e;
					handler.sendMessage(msg);
					return;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		background.start();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				if (pb != null)
					pb.setVisibility(View.GONE);
			} catch (Exception e) {};
			switch (msg.arg1) {
			case MSG_ERROR:
				displayErrorPopup(((ZabbixAPIException) msg.obj).getMessage());
				break;
			case MSG_DATA_RETRIEVED:
				if (msg.obj!=null)
					drawImage((Bitmap) msg.obj);
				else 
					displayErrorPopup(getResources().getString(R.string.cant_get_graph_image));
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "1h");
		menu.add(0, 1, 0, "2h");
		menu.add(0, 2, 0, "Зh");
		menu.add(0, 3, 0, "6h");
		menu.add(0, 4, 0, "12h");
		menu.add(0, 5, 0, "1d");
		menu.add(0, 6, 0, "1w");
		menu.add(0, 7, 0, "2w");
		menu.add(0, 8, 0, "1m");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Log.d(TAG, "Menu item Id: " + item.getItemId());
		switch (item.getItemId()) {
		case 0:
			period = 3600;
			refreshData();
			break;
		case 1:
			period = 7200;
			refreshData();
			break;
		case 2:
			period = 10800;
			refreshData();
			break;
		case 3:
			period = 21600;
			refreshData();
			break;
		case 4:
			period = 43200;
			refreshData();
			break;
		case 5:
			period = 86400;
			refreshData();
			break;
		case 6:
			period = 604800;
			refreshData();
			break;
		case 7:
			period = 1209600;
			refreshData();
			break;
		case 8:
			period = 2419200;
			refreshData();
			break;
		}
		return true;
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