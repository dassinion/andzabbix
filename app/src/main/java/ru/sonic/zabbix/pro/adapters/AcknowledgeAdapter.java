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

import ru.sonic.zabbix.pro.base.Acknowledge;
import ru.sonic.zabbix.pro.base.Event;


class AcknowledgeAdapterView extends LinearLayout {        
        public static final String LOG_TAG = "AcknowledgeAdapterView";

        @SuppressWarnings("deprecation")
		public AcknowledgeAdapterView(Context context,Acknowledge acknowledge ) {
            super(context);

            TextView clock = new TextView(context);
            clock.setText(acknowledge.getClockString());
            clock.setTextColor(Color.WHITE);
            clock.setTextSize(14);

            TextView message = new TextView(context);
            message.setText(acknowledge.getmessage());
            message.setTextColor(Color.WHITE);
            message.setTextSize(14);
            message.setPadding(10, 0, 0, 0);
            	
            TextView alias = new TextView(context);
            alias.setText(acknowledge.getalias()+":");
            alias.setTextColor(Color.WHITE);
            alias.setTextSize(14);
            alias.setPadding(10, 0, 0, 0);
            
//##############################################################################3                 
            TableLayout table = new TableLayout(context);
//##############################################################################3                
            	
            TableRow row1 = new TableRow(context);
            
            TableRow.LayoutParams hostNameparam = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            hostNameparam.gravity = Gravity.LEFT;
            
            TableRow.LayoutParams ageparam = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            ageparam.gravity = Gravity.LEFT;

            row1.addView(clock,hostNameparam);
            row1.addView(alias,hostNameparam);
            row1.addView(message,hostNameparam);
            
        	table.addView(row1,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
        	
//#############################################################################
            
            addView(table);
        }
}

public class AcknowledgeAdapter extends BaseAdapter {

    private Context context;
    private List<Acknowledge> eventList;

    public AcknowledgeAdapter(Context context, List<Acknowledge> eventList ) { 
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
    	Acknowledge event = (Acknowledge) getItem(position);
        return new AcknowledgeAdapterView(this.context, event );
    }
}