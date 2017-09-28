package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
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

import ru.sonic.zabbix.pro.base.Trigger;

class TriggerAdapterView extends LinearLayout {        
        public static final String LOG_TAG = "TriggerAdapterView";
        Context ctx;

        public TriggerAdapterView(Context context,Trigger trigger) {
            super(context);
            GradientDrawable shape =  new GradientDrawable();
            shape.setCornerRadius( 20 );
            ctx = this.getContext();
            shape.setColor(Color.parseColor(trigger.getSeverityColor()));
            //this.setOrientation(HORIZONTAL);
            this.setBackground(shape);

            addView(getTable(trigger));
            addView(getTriggerStatuslayout(trigger));
        }
        
        @SuppressWarnings("deprecation")
		private LinearLayout getTriggerStatuslayout(Trigger trigger) {
        	LinearLayout triggerStatusLayout = new LinearLayout(ctx);
        	try {
	            
	            triggerStatusLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
	            triggerStatusLayout.setGravity(Gravity.CENTER);
	            triggerStatusLayout.setOrientation(VERTICAL);
	        	
	            ImageView triggerAckImg = new ImageView(ctx);
	        	triggerAckImg.setImageResource(trigger.getAckImg());
	        	triggerAckImg.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ));
	        	triggerAckImg.setPadding(0, 5, 0, 0);
	        	
	            ImageView triggerStatusImage = new ImageView(ctx);
	        	triggerStatusImage.setImageResource(trigger.getActiveImg());
	        	triggerStatusImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ));
	        	
	            triggerStatusLayout.addView(triggerStatusImage);
	            if (!showask()) {
	            	triggerStatusLayout.addView(triggerAckImg);
	            	//triggerStatusLayout.setGravity(Gravity.RIGHT);
	                triggerStatusLayout.setPadding(0, 10, 0, 0);
	            } else {
	            	//triggerStatusLayout.setGravity(Gravity.RIGHT);
	                triggerStatusLayout.setPadding(0, 20, 0, 10);
	            }
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
            
            return triggerStatusLayout;
        }
        
        @SuppressWarnings("deprecation")
		private TableLayout getTable(Trigger trigger) {
            WindowManager mWinMgr = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
            int displayWidth = mWinMgr.getDefaultDisplay().getWidth();

            TextView description = new TextView(ctx);
            	description.setText(trigger.getDescription());
            	description.setTextColor(Color.BLACK);
            	description.setTextSize(16);

            TextView hostName = new TextView(ctx);
            	hostName.setText(trigger.getHost());
            	hostName.setTextColor(Color.BLACK);
            	hostName.setTextSize(14);
            	
            TextView age = new TextView(ctx);
                age.setText(trigger.getAgeTime());
                age.setTextColor(Color.DKGRAY);
                age.setTextSize(12);
                age.setPadding(0, 10, 0, 0);

            TextView servername = new TextView(ctx);
                servername.setText(trigger.getServerID());
                servername.setTextColor(Color.BLACK);
                servername.setTextSize(16);

//##############################################################################3                 
            TableLayout table = new TableLayout(ctx);
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 5, 5, 5);

//##############################################################################3                
            	
            TableRow row1 = new TableRow(ctx);
            
            if (!showage()) {
                TableRow.LayoutParams hostNameparam = new TableRow.LayoutParams(displayWidth/2,LayoutParams.WRAP_CONTENT);
                hostNameparam.gravity = Gravity.LEFT;

                row1.addView(hostName,hostNameparam);
                TableRow.LayoutParams ageparam = new TableRow.LayoutParams(displayWidth/3,LayoutParams.WRAP_CONTENT);
                ageparam.gravity = Gravity.RIGHT;
            	row1.addView(age,ageparam);
            } else {
                TableRow.LayoutParams hostNameparam = new TableRow.LayoutParams(displayWidth-40,LayoutParams.WRAP_CONTENT);
                hostNameparam.gravity = Gravity.LEFT;

                row1.addView(hostName,hostNameparam);
            }

        	table.addView(row1,layoutParams);
        	
//##############################################################################3        	
            
            TableRow row2 = new TableRow(ctx); 
             
            LinearLayout descripionLayout = new LinearLayout(ctx);
            
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 2;
            descripionLayout.addView(description);
            descripionLayout.setLayoutParams(params);
        	row2.addView(descripionLayout);
        	
        	table.addView(row2,layoutParams);

            if (showserver()) {
                TableRow.LayoutParams paramszserver = new TableRow.LayoutParams();
                paramszserver.span = 2;
                paramszserver.gravity = Gravity.RIGHT;
                TableRow row3 = new TableRow(ctx);
                LinearLayout descripionLayout3 = new LinearLayout(ctx);
                descripionLayout3.addView(servername);
                descripionLayout3.setLayoutParams(paramszserver);
                row3.addView(descripionLayout3);
                table.addView(row3, layoutParams);
            }

            return table;
        }
        
    	private boolean showask() {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            return prefs.getBoolean("hide_ask",false);
		}

		private boolean showage() {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            return prefs.getBoolean("hide_age",false);
		}

        private boolean showserver() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            return prefs.getBoolean("multiserver_support",false);
        }
}

public class TriggersAdapter extends BaseAdapter {

    private Context context;
    private List<Trigger> triggerList;

    public TriggersAdapter(Context context, List<Trigger> triggerList) {
        this.context = context;
        this.triggerList = triggerList;
    }

    public int getCount() {                        
        return triggerList.size();
    }

    public Object getItem(int position) {     
        return triggerList.get(position);
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
    	Trigger trigger = (Trigger) getItem(position);
        View triggerView = new TriggerAdapterView(this.context, trigger);;
        return triggerView;
    }
}