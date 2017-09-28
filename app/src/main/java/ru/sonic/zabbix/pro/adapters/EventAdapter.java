package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.List;

import ru.sonic.zabbix.pro.base.Event;


class EventAdapterView extends LinearLayout {        
        public static final String LOG_TAG = "TriggerAdapterView";

        @SuppressWarnings("deprecation")
		public EventAdapterView(Context context,Event event ) {
            super(context);
            this.setBackgroundColor(Color.parseColor(event.getSeverity()));
            //this.setOrientation(HORIZONTAL);
            
            WindowManager mWinMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            int displayWidth = mWinMgr.getDefaultDisplay().getWidth();

            TextView description = new TextView(context);
            	description.setText(event.getDescription());
            	description.setTextColor(Color.BLACK);
            	description.setTextSize(16);

            TextView hostName = new TextView(context);
            	hostName.setText(event.getHost());
            	hostName.setTextColor(Color.BLACK);
            	hostName.setTextSize(14);
            	
            TextView age = new TextView(context);
                age.setText(event.getAgeTime());
                age.setTextColor(Color.DKGRAY);
                age.setTextSize(12);
                age.setPadding(0, 10, 0, 0);
                
            ImageView triggerStatusImage = new ImageView(context);
            	triggerStatusImage.setImageResource(event.getActiveImg());
            
            ImageView triggerAckImg = new ImageView(context);
            	triggerAckImg.setImageResource(event.getAckImg());
            	triggerAckImg.setPadding(0, 5, 0, 0);
            
//##############################################################################3                 
            TableLayout table = new TableLayout(context);
//##############################################################################3                
            	
            TableRow row1 = new TableRow(context);
            
            TableRow.LayoutParams hostNameparam = new TableRow.LayoutParams(displayWidth/2,LayoutParams.WRAP_CONTENT);
            hostNameparam.gravity = Gravity.LEFT;
            
            TableRow.LayoutParams ageparam = new TableRow.LayoutParams(displayWidth/3,LayoutParams.WRAP_CONTENT);
            ageparam.gravity = Gravity.RIGHT;

            row1.addView(hostName,hostNameparam);
            row1.addView(age,ageparam);
            
        	table.addView(row1,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
        	
//##############################################################################    	
            
            TableRow row2 = new TableRow(context); 
             
            LinearLayout descripionLayout = new LinearLayout(context);
            
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 2;
            descripionLayout.addView(description);
            descripionLayout.setLayoutParams(params);
        	row2.addView(descripionLayout);
        	
        	table.addView(row2);
        	
//#############################################################################
            
            LinearLayout triggerStatusLayout = new LinearLayout(context);
            triggerStatusLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
            triggerStatusLayout.setOrientation(VERTICAL);
        	
            triggerStatusLayout.addView(triggerAckImg);
            triggerStatusLayout.setGravity(Gravity.RIGHT);
            triggerStatusLayout.setPadding(0, 20, 0, 0);
        	
//#############################################################################
            
            addView(table);
            addView(triggerStatusLayout);
        }
}

public class EventAdapter extends BaseAdapter {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList ) { 
        this.context = context;
        this.eventList = eventList;
    }

    public int getCount() {                        
        return eventList.size();
    }

    public Object getItem(int position) {     
        return eventList.get(position);
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
    	Event event = (Event) getItem(position);
        return new EventAdapterView(this.context, event );
    }
}