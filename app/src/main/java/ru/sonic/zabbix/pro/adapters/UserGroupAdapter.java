package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.List;

import ru.sonic.zabbix.pro.base.UserGroup;

class UserGroupAdapterView extends LinearLayout {        
    public static final String LOG_TAG = "DefaultAdapterView";
    Context ctx;
    
    @SuppressWarnings("deprecation")
	public UserGroupAdapterView(Context context,UserGroup object ) {
        super(context);
        ctx = this.getContext();
        LinearLayout.LayoutParams defaultParams = 
            new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        defaultParams.setMargins(1, 1, 1, 1);
        
        String ret = object.getName();
		ret = ret + "\n Ststus:	"+getResources().getString(object.getStatusString());
		ret = ret + "\n Gui:		"+getResources().getString(object.getGuiString());
		ret = ret + "\n Debug:	"+getResources().getString(object.getDebugString());
		if (object.getApi_access()!=null)
			ret = ret +"\n Api:		"+getResources().getString(object.getApiString());
		
        String desc = ret;
        TextView defaultControl = new TextView( context );
        defaultControl.setPadding(0, 15, 0, 15);
        defaultControl.setText( desc );
        addView( defaultControl, defaultParams);
    }
}

public class UserGroupAdapter extends BaseAdapter {

    private Context context;
    private List<UserGroup> list;

    public UserGroupAdapter(Context context, List<UserGroup> list ) { 
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
    	UserGroup object = list.get(position);
        return new UserGroupAdapterView(this.context, object );
    }

}