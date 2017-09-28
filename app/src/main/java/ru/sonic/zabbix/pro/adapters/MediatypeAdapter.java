package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

import ru.sonic.zabbix.pro.base.Mediatype;

class MediatypeAdapterView extends LinearLayout {        
    public static final String LOG_TAG = "DefaultAdapterView";
    @SuppressWarnings("deprecation")
	public MediatypeAdapterView(Context context,Mediatype object ) {
        super(context);
        LinearLayout.LayoutParams defaultParams = 
            new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        defaultParams.setMargins(1, 1, 1, 1);
        String desc = object.toString()+"\n	Type: "+object.getTypeString()+"\n	"+object.getDetails();
        TextView description = new TextView( context );
        description.setPadding(0, 15, 0, 15);
        description.setText( desc );
        addView( description, defaultParams);
    }
}

public class MediatypeAdapter extends BaseAdapter {

    private Context context;
    private List<Mediatype> list;

    public MediatypeAdapter(Context context, List<Mediatype> list ) { 
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
    	Mediatype object = list.get(position);
        return new MediatypeAdapterView(this.context, object );
    }

}