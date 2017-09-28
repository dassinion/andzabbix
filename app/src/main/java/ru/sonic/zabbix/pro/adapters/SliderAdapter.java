package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.base.Host;


@SuppressWarnings("deprecation")
class SliderAdapterView extends LinearLayout {

        public SliderAdapterView(Context context,int position, String host ) {
            super(context);
            
            //this.setOrientation(HORIZONTAL);   
            this.setBackgroundColor(Color.DKGRAY);
            
            WindowManager mWinMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            int displayWidth = mWinMgr.getDefaultDisplay().getWidth();
            
            TextView hostControl = new TextView( context );
            //hostControl.setTextAppearance( context, R.style.HostList );
            hostControl.setPadding(40, 30, 10, 30);
            hostControl.setTextColor(Color.WHITE);
            hostControl.setTextSize(16);
            hostControl.setTypeface(null, Typeface.BOLD);
            hostControl.setText(host);
            /*
            LayoutParams imgParams =
                    new LayoutParams(40, 40 );
            imgParams.setMargins(10, 30, 5, 30);
            switch (position) {
                case 0:
                    addView(getImage(context, R.drawable.attention), imgParams);
                    break;
                case 1:
                    addView(getImage(context, R.drawable.icon), imgParams);
                    break;
                case 2:
                    addView(getImage(context, R.drawable.config_bb), imgParams);
                    break;
                case 3:
                    addView(getImage(context, R.drawable.ic_drawer), imgParams);
                    break;
                case 4:
                    addView(getImage(context, R.drawable.chart), imgParams);
                    break;
                case 5:
                    addView(getImage(context, R.drawable.unknown_icon), imgParams);
                    break;
                case 6:
                    addView(getImage(context, R.drawable.map), imgParams);
                    break;
                case 7:
                    addView(getImage(context, R.drawable.graph), imgParams);
                    break;
            }
            */
            LayoutParams hostParams = new LayoutParams(displayWidth-128, LayoutParams.WRAP_CONTENT );
	        hostParams.setMargins(1, 1, 1, 1);
	        addView( hostControl, hostParams);
        }
        
        public ImageView getImage(Context context, int id) {
            ImageView image = new ImageView(context);
            image.setImageResource(id);
            image.setAdjustViewBounds(true);
            image.setPadding(2, 1, 2, 1);
            image.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            return image;
        }
}

public class SliderAdapter extends BaseAdapter {

    private Context context;
    private String[] hostList;

    public SliderAdapter(Context context, String[] hostList ) {
        this.context = context;
        this.hostList = hostList;
    }

    public int getCount() {                        
        return hostList.length;
    }

    public Object getItem(int position) {     
        return hostList[position];
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
    	String host = hostList[position];
        return new SliderAdapterView(this.context, position, host);
    }

}