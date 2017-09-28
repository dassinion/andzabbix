package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

class MediaAdapterView extends LinearLayout {        
    public static final String LOG_TAG = "DefaultAdapterView";
    @SuppressWarnings("deprecation")
	public MediaAdapterView(Context context,Object object ) {
        super(context);
        LinearLayout.LayoutParams defaultParams = 
            new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        defaultParams.setMargins(1, 1, 1, 1);
        String desc = object.toString();
        TextView defaultControl = new TextView( context );
        defaultControl.setPadding(0, 15, 0, 15);
        defaultControl.setText( desc );
        addView( defaultControl, defaultParams);  
    }
}

public class MediaAdapter extends BaseAdapter {

    private Context context;
    private List<?> list;

    public MediaAdapter(Context context, List<?> list ) { 
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
    	Object object = list.get(position);
        return new MediaAdapterView(this.context, object );
    }

}