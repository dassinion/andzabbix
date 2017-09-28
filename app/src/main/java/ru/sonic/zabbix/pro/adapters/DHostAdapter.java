package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.base.DHost;

@SuppressWarnings("deprecation")
class DHostAdapterView extends LinearLayout {        

		public DHostAdapterView(Context context,DHost host ) {
            super(context);
            
            //this.setOrientation(HORIZONTAL);   
            //this.setBackgroundColor(Color.parseColor("#222222"));
            
            WindowManager mWinMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            int displayWidth = mWinMgr.getDefaultDisplay().getWidth();
            
            TextView hostControl = new TextView( context );
           // hostControl.setTextAppearance( context, R.style.HostList );
            hostControl.setPadding(0, 20, 0, 20);
            hostControl.setTextColor(host.getStatusColor());
            hostControl.setText(host.getName());
            
	        LinearLayout.LayoutParams hostParams = 
	            new LinearLayout.LayoutParams(displayWidth-128, LayoutParams.WRAP_CONTENT );
	        hostParams.setMargins(1, 1, 1, 1);
	        addView( hostControl, hostParams);

            LinearLayout.LayoutParams imgParams = 
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
            imgParams.setMargins(1, 1, 1, 1);
            
            addView( getImage(context,host.getAvailableImg()), imgParams); 
            addView( getImage(context,host.getSnmpAvailableImg()), imgParams); 
            addView( getImage(context,host.getIPMIavailableImg()), imgParams); 
            addView( getImage(context,host.getJmxAvailableImg()), imgParams);
        }
        
		public ImageView getImage(Context context, int id) {
            ImageView image = new ImageView(context);
            image.setImageResource(id);
            image.setAdjustViewBounds(true);
            image.setPadding(0, 20, 0, 20);
            image.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            return image;
        }
}

public class DHostAdapter extends BaseAdapter {

    private Context context;
    private List<DHost> hostList;

    public DHostAdapter(Context context, List<DHost> hostList ) { 
        this.context = context;
        this.hostList = hostList;
    }

    public int getCount() {                        
        return hostList.size();
    }

    public Object getItem(int position) {     
        return hostList.get(position);
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
    	DHost host = hostList.get(position);
        return new DHostAdapterView(this.context, host );
    }

}