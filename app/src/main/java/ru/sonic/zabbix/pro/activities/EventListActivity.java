package ru.sonic.zabbix.pro.activities;

import java.util.List;

import org.json.simple.JSONArray;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.AcknowledgeAdapter;
import ru.sonic.zabbix.pro.adapters.EventAdapter;
import ru.sonic.zabbix.pro.api.EventApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import ru.sonic.zabbix.pro.base.Event;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * display active triggers in a listview
 * @author dassinion
 *
 */ 
public class EventListActivity extends DefaultZabbixListActivity {
	protected static final int CONTEXTMENU_DELETEEVENT = 0;
	protected static final int CONTEXTMENU_ACK = 1;
	protected static final int CONTEXTMENU_COMMENTS = 2;
	private EventApiHandler api;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = new EventApiHandler(this,getSelectedServer());
		//setPrefCurrentServer();
		try {
			Bundle extras  = getIntent().getExtras();
			String triggerDesc  = extras.getString("triggerDesc");
			String serverName  = extras.getString("serverName");
			setTitle("Trigger: "+triggerDesc);
		} catch (Exception e) {	}
		//actionBar.hideAction(ACTIONBARITEM_SELECTSRV);
			
		final ListView lv=(ListView)findViewById(android.R.id.list);
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
				Event event=(Event)(lv.getAdapter().getItem(position));
				if (event.hasAcknowledges())
					showAckComments(event);
				//showAckAlertDialog(event);
				/*
				Intent ActivTriggerIntent = new Intent(getBaseContext(),
						ActiveTriggerInfoActivity.class);
				ActivTriggerIntent.putExtra("triggerID", trigger.getID());
				startActivity(ActivTriggerIntent); */			
			}
		});  
			
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
	           public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {    
	               menu.setHeaderTitle("Trigger options");  
	               menu.add(0, CONTEXTMENU_DELETEEVENT, 0, "Delete");
	               menu.add(0, CONTEXTMENU_ACK, 0, "Acknowledge");
	               menu.add(0, CONTEXTMENU_COMMENTS, 0, "Comments");
	           }
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new EventApiHandler(this,getSelectedServer());
		try {
			Bundle extras  = getIntent().getExtras();
			String triggerId  = extras.getString("triggerId");
			if (triggerId == null) {triggerId = "0";}
			return api.get(triggerId);
		} catch (Exception e) {
			return api.get("0");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setListContent(@SuppressWarnings("rawtypes") List data) {
		if (data.size()==0){
			Toast.makeText(getApplicationContext(),
					"No data to display", Toast.LENGTH_LONG).show();
			return;
			}
        EventAdapter eventAdapter = new EventAdapter(this,data);
        setListAdapter( eventAdapter );
	} 
	
	@Override
	public JSONArray exec(String operation, Object obj) throws ZabbixAPIException {
		if (operation.equals("ack")) {
			String eventId = ((Event) obj).getID();
			String message = ((Event) obj).getAckMessage();
			api.ackEvent(eventId, message);
			return null;
		} else if (operation.equals("delete")) {
			String eventId = ((Event) obj).getID();
			api.delEvent(eventId);
			return null;
		} else {
			return null;
		}
	}	
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
    	ListView lv=(ListView)findViewById(android.R.id.list);
    	Event event=(Event)(lv.getAdapter().getItem(menuInfo.position));
    	switch (item.getItemId()) {
        	case CONTEXTMENU_DELETEEVENT:
        		showDelAlertDialog(event);
				break;
        	case CONTEXTMENU_ACK:
        		showAckAlertDialog(event);
        		break;
        	case CONTEXTMENU_COMMENTS:
        		if (event.hasAcknowledges())
					showAckComments(event);
        		break;
    	}
    return true;
    } 
	
	public void showAckAlertDialog(final Event event) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText message = new EditText(context);
		message.setHint("comment");
		builder.setMessage("Ack event "+ event.getID() + "?")
        .setCancelable(false)
	    .setView(message)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	    {
            public void onClick(DialogInterface dialog, int id) {
				//api.ackEvent(eventId,message.getText().toString());
            	event.setAckMessage(message.getText().toString());
				executeop("ack", event);
				Toast.makeText(getApplicationContext(),
								"Event acknowleged", Toast.LENGTH_LONG).show();
				//refreshData();
            }
        })
		
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
		});

		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void showDelAlertDialog(final Event event) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage("Delete event "+ event.getID() + "?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	    {
            public void onClick(DialogInterface dialog, int id) {
				//String LastEventId = eventIDs.get(eventIDs.size()-1);
				//api.delEvent(event);
				executeop("delete", event);
				Toast.makeText(getApplicationContext(),"Event deleted", Toast.LENGTH_LONG).show();
				//refreshData();
            }
        })
		
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
		});

		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void showAckComments(final Event event) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.event_comments, null);
		ListView commentslist = (ListView)layout.findViewById(R.id.event_comments_list);
		AcknowledgeAdapter ackadapter = new AcknowledgeAdapter(context, event.getAcknowledgesList());
		commentslist.setAdapter(ackadapter);
		builder.setView(layout);
		
		builder.setTitle("Ack comments")
        .setCancelable(false)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
				// nothing
            }
        });
		AlertDialog alert = builder.create();
		alert.show();
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