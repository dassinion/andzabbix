package ru.sonic.zabbix.pro.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

;
import ru.sonic.zabbix.pro.R;

import ru.sonic.zabbix.pro.api.MapApiHandler;
import ru.sonic.zabbix.pro.api.ScriptApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.Maps;
import ru.sonic.zabbix.pro.base.Script;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * display map
 * @author dassinion
 * 
 */
public class MapActivity extends Activity {
	private static final String TAG = "MapActivity";
	private static final int MSG_DATA_RETRIEVED = 0;
	private static final int MSG_MAPDATA_RETRIEVED = 2;
	private static final int MSG_SCRIPT_EXECUTED = 3;
	private static final int MSG_ERROR = 1;
	protected MapApiHandler api = null;
	protected ScriptApiHandler sapi = null;
	private String itemID = "";
	private String baseurl;
	private String mapurl = "/map.php?sysmapid=";
	private Thread background,scriptexec;
	//private ZoomControls zoomMapControls;
	private SampleView mapView;
	private static int displayWidth = 0;
	private static int displayHeight = 0;
	ProgressBar pb;
	static Context ctx;
	private static float startX = 0; // track x from one ACTION_MOVE to the next
	private static float startY = 0; // track y from one ACTION_MOVE to the next
	private List<Script> scriptslist;
	private static List<Maps> mapslist;
	//protected ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.ctx = this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		//TODO MAPS IS NOT WORKING!!!!!!!!!!!!!!!!!
		//PLEASE ADD SERVER INSTEAD OF NULL
		api = new MapApiHandler(this,null);
		sapi = new ScriptApiHandler(this,null);
		displayWidth = getDisplayWidth();
		displayHeight = getDisplayHeigh();
		scriptslist = new ArrayList<Script>();
		mapslist = new ArrayList<Maps>();
		setContentView(R.layout.map);
		//actionBar = (ActionBar) findViewById(R.id.actionbar);
		/*
		class MenuAction extends AbstractAction {
            public MenuAction() {
                super(android.R.drawable.ic_menu_agenda);
            } 
            @Override
            public void performAction(View view) {
            	getWindow().getDecorView().findViewById(android.R.id.content).showContextMenu();
            }
        }
        actionBar.addAction(new MenuAction(),0);
        */
		try {
			itemID = (String) getIntent().getExtras().get("mapID");
			// mwidth = (Integer) getIntent().getExtras().get("width");
			// mheight = (Integer) getIntent().getExtras().get("height");
		} catch (Exception e) {
			try {
				throw new ZabbixAPIException(getResources().getString(R.string.cant_find_map_id));
			} catch (ZabbixAPIException e1) {
			}
		}

		try {
			baseurl = api.getAPIURL().split("/api_jsonrpc.php")[0];
		} catch (ZabbixAPIException e) {
			try {
				throw new ZabbixAPIException(getResources().getString(R.string.cant_parse_api_url));
			} catch (ZabbixAPIException e1) {
			}
		}
		refreshData();
	}

	public void drawImage(Bitmap img) {
		Log.i(TAG, "drawImage");
		//RelativeLayout layout = new RelativeLayout(this);
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.maplayout);
		//RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
		//		LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		mapView = new SampleView(this);
		mapView.setImageBitmap(img);
		layout.addView(mapView);
		/*
		zoomMapControls = new ZoomControls(this);
		zoomMapControls.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				mapView.zoomin();
			}
		});
		zoomMapControls.setOnZoomOutClickListener(new OnClickListener() {
			public void onClick(View v) {
				mapView.zoomout();
			}
		});
		ZoomControls.LayoutParams zparams = new ZoomControls.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addView(zoomMapControls, zparams);
		*/
		//setContentView(layout);
	}

	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		return display.getWidth();
	}

	@SuppressWarnings("deprecation")
	private int getDisplayHeigh() {
		Display display = getWindowManager().getDefaultDisplay();
		return display.getHeight();
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void displayErrorPopup(String message) {
		try {
			new AlertDialog.Builder(this).setMessage(message).setTitle(getResources().getString(R.string.error)).show();
		} catch (Exception e) {}
	}
	
	/**
	 * display an error poup
	 * @param message
	 */
	public void displayPopup(String title, String message) {
		try {
			final AlertDialog errorDialog = new AlertDialog.Builder(this).setMessage(message).setTitle(title).create();
			errorDialog.show();
			
			android.os.Handler messagHandler = null;
			Message msg = new Message();
			
			messagHandler = new android.os.Handler() {
		        public void handleMessage(android.os.Message msg) {
		            switch (msg.what) {
		                case 1:
		                	errorDialog.dismiss();
		                    break;
		            }
		        };
		    };

		    msg.what = 1;
		    messagHandler.sendMessageDelayed(msg, 10000);
		} catch (Exception e) {}
	}

	@SuppressLint("HandlerLeak")
	public void refreshData() {
		pb = (ProgressBar)findViewById(R.id.mapprogressBar);
		pb.setVisibility(View.VISIBLE);
		
		background = new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					scriptslist.addAll(sapi.get());
					Bitmap img = api.getMapImage(baseurl + mapurl + itemID);
					msg.obj = img;
					msg.arg1 = MSG_DATA_RETRIEVED;
					handler.sendMessage(msg);
					
					Message msg2 = new Message();
					List<Maps> maps = api.get(itemID);
					mapslist.addAll(maps);
					msg2.arg1 = MSG_MAPDATA_RETRIEVED;
					handler.sendMessage(msg2);
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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				super.handleMessage(msg);
				if (pb != null)
					pb.setVisibility(View.GONE);
				setProgressBarIndeterminateVisibility(false);
				switch (msg.arg1) {
				case MSG_ERROR:
					displayErrorPopup(((ZabbixAPIException) msg.obj).getMessage());
					break;
				case MSG_DATA_RETRIEVED:
					if (msg.obj != null) {
						drawImage((Bitmap) msg.obj);
					} else
						displayErrorPopup(getResources().getString(R.string.cant_get_graph_image));
					break;
				case MSG_MAPDATA_RETRIEVED:
					//actionBar.setTitle(mapslist.get(0).getName());
					registerForContextMenu(getWindow().getDecorView().findViewById(android.R.id.content));
					break;
				case MSG_SCRIPT_EXECUTED:
					JSONArray jarr = (JSONArray) msg.obj;
					if (jarr.size()>0 && jarr.get(0)!="") {
						JSONObject respobj = (JSONObject) jarr.get(0);
						String respstat = respobj.get("response").toString();
						String respval = respobj.get("value").toString();
						displayPopup("Status: "+respstat,respval);
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			background.interrupt();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		try {
			if (mapslist.size()>0) {
				JSONArray selements = mapslist.get(0).getselements();
				int k=1;
				for (Iterator<JSONObject> it = selements.iterator(); it.hasNext();) {
					JSONObject obj = it.next();
					SubMenu sm = menu.addSubMenu (Menu.FIRST, k, 1, obj.get("label").toString());
					int n=0;
					for (Script script: scriptslist) {
						sm.add(Menu.FIRST, k*100+n, k*100+n, script.getName());  
						//sm.add(Menu.FIRST, 101, 101, "SubMenu 2");  
						//sm.add(Menu.FIRST, 102, 102, "SubMenu 3");
						n++;
					}
					//menu.add(0, k, 0, obj.get("label").toString());
					k++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
		//displayErrorPopup("Selected: "+item.getItemId()+"item. This is a "+mapslist.get(0).getselements().get(item.getItemId()));
		if (item.getItemId()>99) {
			int objnum = item.getItemId()/100;
			//Log.d(TAG,"Menu item selected: "+(objnum-1));
			final int scriptnum = item.getItemId() - (objnum*100);
			//Log.d(TAG,"scriptnum selected: "+scriptnum);
			
			//elementid
			String element = mapslist.get(0).getselements().get(objnum-1).toString();
			JSONObject elementjson = api.parseObject(element);
			final String elementid = elementjson.get("elementid").toString();
			Log.d(TAG,"Object: "+elementid);
			Log.d(TAG,"Script: "+scriptslist.get(scriptnum).getName());
			setProgressBarIndeterminateVisibility(true);
			scriptexec = new Thread(new Runnable() {
				@Override
				public void run() {
					Message msg = new Message();
					try {
						msg.obj = sapi.execute(scriptslist.get(scriptnum), elementid);
						msg.arg1 = MSG_SCRIPT_EXECUTED;
						handler.sendMessage(msg);
					} catch (ZabbixAPIException e) {
						Log.e(TAG, "rpc call failed: " + e);
						msg.arg1 = MSG_ERROR;
						msg.obj = e;
						handler.sendMessage(msg);
						return;
					}
				}
			});
			scriptexec.start();
		}
    	return true;
    } 
	
	private static class SampleView extends View {
		private static Bitmap bmLargeImage; // bitmap large enough to be
											// scrolled
		private static Rect displayRect = null; // rect we display to
		private Rect scrollRect = null; // rect we scroll over our bitmap with
		private int scrollRectX = 0; // current left location of scroll rect
		private int scrollRectY = 0; // current top location of scroll rect
		private float scrollByX = 0; // x amount to scroll by
		private float scrollByY = 0; // y amount to scroll by
		private static int mapWidth = 0;
		private static int mapHeight = 0;
		private float mScaleFactor = 1f;

		public SampleView(Context context) {
			super(context);
			// Log.d("Constructor: ","displayWidth: "+displayWidth+", displayHeight:"+displayHeight);
			// Destination rect for our main canvas draw. It never changes.
			displayRect = new Rect(0, 0, displayWidth, displayHeight);
			// Scroll rect: this will be used to 'scroll around' over the
			// bitmap in memory. Initialize as above.
			scrollRect = new Rect(0, 0, displayWidth, displayHeight);
		}
		
		/*
		public void zoomin() {
			mScaleFactor *= 0.75f;
			invalidate(); // force a redraw
		}

		public void zoomout() {
			mScaleFactor = 1.5f;
			invalidate(); // force a redraw
		}
		*/
		
		public void setImageBitmap(Bitmap img) {
			// Load a large bitmap into an offscreen area of memory.
			bmLargeImage = img;
			mapWidth = bmLargeImage.getWidth() + 10;
			mapHeight = bmLargeImage.getHeight() + 120;
			Log.d("IMAGE: ", "Width: " + bmLargeImage.getWidth() + ", Height:"
					+ bmLargeImage.getHeight());
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			//Log.d(TAG,"Action: "+event.getAction());
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Remember our initial down event location.
				startX = event.getRawX();
				startY = event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float x = event.getRawX();
				float y = event.getRawY();
				// Calculate move update. This will happen many times
				// during the course of a single movement gesture.
				scrollByX = x - startX; // move update x increment
				scrollByY = y - startY; // move update y increment
				float abs_x = (scrollByX < 0) ? -scrollByX : scrollByX;
				float abs_y = (scrollByY < 0) ? -scrollByY : scrollByY;
				if (abs_x<10 && abs_y<10) {
					// nothing
				} else {
					startX = x; // reset initial values to latest
					startY = y;
					invalidate(); // force a redraw
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			return true; // done with this event so consume it
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(Canvas canvas) {
			int newScrollRectX = scrollRectX - (int) scrollByX;
			int newScrollRectY = scrollRectY - (int) scrollByY;
			// Log.d("DRAWING: ","X: "+newScrollRectX+", Y:"+newScrollRectY);
			// Don't scroll off the left or right edges of the bitmap.
			if (mapWidth - displayWidth > 0) {
				if (newScrollRectX < -10)
					newScrollRectX = -10;
				else if (newScrollRectX > (mapWidth - displayWidth))
					newScrollRectX = (mapWidth - displayWidth);
			} else {
				newScrollRectX = 0;
			}

			// Don't scroll off the top or bottom edges of the bitmap.
			if (mapHeight - displayHeight > 0) {
				if (newScrollRectY < -10)
					newScrollRectY = -10;
				else if (newScrollRectY > (mapHeight - displayHeight))
					newScrollRectY = (mapHeight - displayHeight);
			} else {
				newScrollRectY = 0;
			}

			// We have our updated scroll rect coordinates, set them and draw.
			scrollRect.set(newScrollRectX, newScrollRectY, newScrollRectX+ displayWidth, newScrollRectY + displayHeight);
			Paint paint = new Paint();
			canvas.scale(mScaleFactor, mScaleFactor);
			canvas.drawBitmap(bmLargeImage, scrollRect, displayRect, paint);

			/*
			try {
				if (mapslist.size()>0) {
					JSONArray selements = mapslist.get(0).getselements();
					Log.d(TAG,"Map list elements: "+selements);
					for (Iterator<JSONObject> it = selements.iterator(); it.hasNext();) {
						JSONObject obj = it.next();
						float objstartx = Float.parseFloat(obj.get("x").toString());
						float objstarty = Float.parseFloat(obj.get("y").toString());
						float endobjX = objstartx + Float.parseFloat(obj.get("width").toString());
						float endobjY = objstartx + Float.parseFloat(obj.get("height").toString());
						canvas.drawRect(objstartx-newScrollRectX,objstarty-newScrollRectY, endobjX-newScrollRectX,endobjY-newScrollRectY, paint);		
						}
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			// Reset current scroll coordinates to reflect the latest updates,
			// so we can repeat this update process.
			scrollRectX = newScrollRectX;
			scrollRectY = newScrollRectY;
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