package ru.sonic.zabbix.pro.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.DiffAdapter;
import ru.sonic.zabbix.pro.api.MapApiHandler;
import ru.sonic.zabbix.pro.api.ScreenApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Screen;
import ru.sonic.zabbix.pro.base.Screenitem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * display map
 * 
 * @author dassinion
 * 
 */
public class ScreenActivity extends Activity {
	private static final String TAG = "ScreenActivity";
	protected ScreenApiHandler api = null;
	private String screenID = "";
	private String hsize;
	private String vsize;
	private Thread background;
	ProgressBar pb;
	ViewPager viewPager;
    ProgressDialog progDialog;
	int maxBarValue = 1;
    int typeBar = 1;                     // Determines type progress bar: 0 = spinner, 1 = horizontal

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (api == null)
			api = new ScreenApiHandler(this,null);
		
		try {
			hsize = (String) getIntent().getExtras().get("hsize");
			vsize = (String) getIntent().getExtras().get("vsize");
			int x = Integer.parseInt(hsize);
			int y = Integer.parseInt(vsize);
			maxBarValue = x*y;
		} catch (Exception e) {}

		showDialog(typeBar);
		refreshData();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<View> getData() throws ZabbixAPIException, UnsupportedEncodingException, IOException {
		//if (api==null)
		//	api = new ScreenApiHandler(this);
		int total = 0;
		
		try {
			screenID = (String) getIntent().getExtras().get("screenID");
			if (screenID == null || screenID.length()==0) 
				throw new ZabbixAPIException(getResources().getString(R.string.cant_find_screen_id));

			
			Log.d(TAG,"ScreenID: "+screenID);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		//LayoutInflater inflater = LayoutInflater.from(this);
		List<View> pages = new ArrayList<View>();
		
		List<Screen> screens = api.getScreenItems(screenID);
		Screen currscreen = screens.get(0);
		JSONArray itemlist = currscreen.getScreenitems();

		//TODO REPLACE NULL WITH SERVER
		MapApiHandler mapi = new MapApiHandler(this,null);
		String baseurl = mapi.getAPIURL().split("/api_jsonrpc.php")[0] + "/chart2.php?graphid=";

		for (Object item: itemlist) {
			RelativeLayout layout = new RelativeLayout(this);
			Screenitem itemparams = new Screenitem((JSONObject) item);
			//screentypes.add(itemparams.getResourceID()+ " : " + itemparams.getResourceType() + " - " + getResources().getString(itemparams.getResourceTypeString()));
			int resType = Integer.parseInt(itemparams.getResourceType());
			TextView tw = new TextView(getBaseContext());
			switch (resType) {
			case 0:
				BitMapView gView = new BitMapView(this,mapi.getMapImage(baseurl+itemparams.getResourceID()));
				layout.addView(gView);
				break;
			default:
				tw.setText("Unknown");
				layout.addView(tw);
				break;
			}
			
			pages.add(layout);
			
			total ++;
			Message msg = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("total", total);
            msg.setData(b);
            msg.arg1 = 2;
            handler.sendMessage(msg);
		}
		return pages;
	}

	@SuppressLint("HandlerLeak")
	public void refreshData() {
		//pb = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
		//RelativeLayout fv = new RelativeLayout(this);
		//fv.setGravity(Gravity.CENTER);
		//fv.addView(pb);
		//setContentView(fv);
		background = new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					msg.obj = getData();
					msg.arg1 = 0;
					handler.sendMessage(msg);
				} catch (ZabbixAPIException e) {
					Log.e(TAG, "rpc call failed: " + e);
					msg.arg1 = 1;
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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case 1:
				// TODO ERROR
				//if (pb != null)
				//	pb.setVisibility(View.GONE);
				break;
			case 0:
				//if (pb != null)
				//	pb.setVisibility(View.GONE);
				DiffAdapter pagerAdapter = new DiffAdapter((List<View>) msg.obj);
				viewPager = new ViewPager(getBaseContext());
				viewPager.setAdapter(pagerAdapter);
				viewPager.setCurrentItem(0);
				setContentView(viewPager);
			case 2:
				int total = msg.getData().getInt("total");
                progDialog.setProgress(total);
                if (total <= 0){
                    dismissDialog(typeBar);
                }
				break;
			default:
				break;
			}
		}
	};
	
	@Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case 0:                      // Spinner
            progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setMessage("Loading...");
            return progDialog;
        case 1:                      // Horizontal
            progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progDialog.setMax(maxBarValue);
            progDialog.setMessage("Loading...");
            return progDialog;
        default:
            return null;
        }
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			background.interrupt();
		}
		return super.onKeyDown(keyCode, event);
	}

	class BitMapView extends View {
		Bitmap mBitmap = null;

		public BitMapView(Context context, Bitmap bm) {
			super(context);
			mBitmap = bm;
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			Paint paint = new Paint();
			paint.setFilterBitmap(true);
			//double aspectRatio = ((double) mBitmap.getWidth()) / mBitmap.getHeight();
			//Rect dest = new Rect(0,0,this.getWidth(),(int) (this.getHeight() / aspectRatio));
			//canvas.drawBitmap(mBitmap, null, dest, paint);
			float scaleWidth = ((float) this.getWidth()) / mBitmap.getHeight();
		    float scaleHeight = ((float) this.getHeight()) / mBitmap.getWidth();
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(mBitmap, 0, 0,mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
			canvas.drawBitmap(resizedBitmap, new Matrix(), paint);
			//canvas.drawBitmap(resizedBitmap, null, dest, paint);
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
}