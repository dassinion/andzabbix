package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

import ru.sonic.zabbix.pro.base.HostInterface;

class ZInterfaceAdapterView extends LinearLayout {        
    public static final String LOG_TAG = "DefaultAdapterView";
    @SuppressWarnings("deprecation")
	public ZInterfaceAdapterView(Context context,HostInterface object ) {
        super(context);
        LinearLayout.LayoutParams iterfacenameParams = 
            new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        iterfacenameParams.setMargins(1, 1, 1, 1);
        TextView iterfacename = new TextView( context );
        iterfacename.setPadding(0, 15, 0, 15);
        if (object.getbMain())
        	iterfacename.setTextColor(Color.GREEN);
        else
        	iterfacename.setTextColor(Color.GRAY);
        iterfacename.setText( object.toString() );
        
        /*
        LinearLayout.LayoutParams defaultParams = 
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        defaultParams.setMargins(1, 1, 1, 1);
        defaultParams.gravity = Gravity.RIGHT;
        TextView definterface = new TextView( context );
        definterface.setPadding(0, 15, 0, 15);
        definterface.setText( object.getbMain()?"Default\n":"");
        addView( definterface, defaultParams);
        */
        addView( iterfacename, iterfacenameParams);  
    }
}

public class HostInterfaceAdapter extends BaseAdapter {

    private Context context;
    private List<HostInterface> list;

    public HostInterfaceAdapter(Context context, List<HostInterface> list ) { 
        this.context = context;
        this.list = list;
    }

    public int getCount() {                        
        return list.size();
    }

    public Object getItem(int position) {     
        return list.get(position);
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
    	HostInterface object = list.get(position);
        return new ZInterfaceAdapterView(this.context, object );
    }

}