package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.List;

import ru.sonic.zabbix.pro.base.Action;
import ru.sonic.zabbix.pro.base.Maintence;

@SuppressWarnings("deprecation")
class MaintencesAdapterView extends LinearLayout {        
    public static final String LOG_TAG = "ActionsAdapterView";
	public MaintencesAdapterView(Context context,Maintence object ) {
        super(context);
        LinearLayout.LayoutParams defaultParams = 
            new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        defaultParams.setMargins(1, 1, 1, 1);
        String desc = object.toString();
        TextView defaultControl = new TextView( context );
        defaultControl.setPadding(0, 15, 0, 15);
        defaultControl.setText( desc );
        int color = Color.GREEN;
        //if (!object.getStatus().equals("0"))
        //	color = Color.RED;
        defaultControl.setTextColor(color);
        addView( defaultControl, defaultParams);
    }
}

public class MaintencesAdapter extends BaseAdapter {

    private Context context;
    private List<Maintence> list;

    public MaintencesAdapter(Context context, List<Maintence> list ) { 
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
    	Maintence object = list.get(position);
        return new MaintencesAdapterView(this.context, object );
    }

}