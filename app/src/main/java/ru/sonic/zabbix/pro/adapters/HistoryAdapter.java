package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.List;

import ru.sonic.zabbix.pro.base.History;


class HistoryAdapterView extends LinearLayout {        
        public static final String LOG_TAG = "TriggerAdapterView";

        @SuppressWarnings("deprecation")
		public HistoryAdapterView(Context context,History hist) {
            super(context);
            //this.setBackgroundColor(Color.parseColor(event.getSeverity()));
            //this.setOrientation(HORIZONTAL);
            
            WindowManager mWinMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            int displayWidth = mWinMgr.getDefaultDisplay().getWidth();

            TextView time = new TextView(context);
            time.setText(hist.getClockString());
            time.setTextColor(Color.WHITE);
            time.setTextSize(16);
            	
            TextView value = new TextView(context);
            value.setText(hist.getValue());
            value.setTextColor(Color.WHITE);
            value.setTextSize(12);
            value.setPadding(0, 10, 0, 0);
            
//##############################################################################3                 
            TableLayout table = new TableLayout(context);
            TableRow row1 = new TableRow(context);
            TableRow.LayoutParams hostNameparam = new TableRow.LayoutParams(displayWidth/2,LayoutParams.WRAP_CONTENT);
            hostNameparam.gravity = Gravity.LEFT;
            TableRow.LayoutParams ageparam = new TableRow.LayoutParams(displayWidth/2 - 10,LayoutParams.WRAP_CONTENT);
            ageparam.gravity = Gravity.RIGHT;
            row1.addView(time,hostNameparam);
            row1.addView(value,ageparam);
        	table.addView(row1,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
//#############################################################################3
            addView(table);
        }
}

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private List<History> histtList;

    public HistoryAdapter(Context context, List<History> histtList ) { 
        this.context = context;
        this.histtList = histtList;
    }

    public int getCount() {                        
        return histtList.size();
    }

    public Object getItem(int position) {     
        return histtList.get(position);
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
    	History hist = (History) getItem(position);
        return new HistoryAdapterView(this.context, hist);
    }
}