package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

import ru.sonic.zabbix.pro.R;

class ServerAdapterView extends LinearLayout {        

        @SuppressWarnings("deprecation")
		public ServerAdapterView(Context context,String server ) {
            super(context);
            
            //this.setOrientation(HORIZONTAL);   
            //this.setBackgroundColor(Color.parseColor("#222222"));
            
            LinearLayout.LayoutParams hostParams = 
                new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT , LayoutParams.FILL_PARENT );
            hostParams.setMargins(20, 20, 1, 20);

            TextView hostControl = new TextView( context );
            //hostControl.setTextAppearance( context, R.style.ServerList );
            hostControl.setPadding(20, 24, 0, 24);
            hostControl.setText( server );
            addView( hostControl, hostParams);       
                       
            //LinearLayout.LayoutParams imgParams = 
            //    new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
            //imgParams.setMargins(1, 1, 1, 1);
            
            //addView( getImage(context,server.getAvailable()), imgParams);
            //addView( getImage(context,R.drawable.snmp_unknown), imgParams); 
            //addView( getImage(context,R.drawable.ipmi_unknown), imgParams);
        }
}

public class ServerAdapter extends BaseAdapter {
    private Context context;
    private List<String> serverList;

    public ServerAdapter(Context context, List<String> serverList ) { 
        this.context = context;
        this.serverList = serverList;
    }

    public int getCount() {                        
        return serverList.size();
    }

    public Object getItem(int position) {     
        return serverList.get(position);
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
        return new ServerAdapterView(this.context, serverList.get(position).toString());
    }

}