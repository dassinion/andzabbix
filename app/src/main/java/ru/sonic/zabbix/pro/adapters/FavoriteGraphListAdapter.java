package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.base.Graph;

@SuppressWarnings("deprecation")
class FavoriteGraphListAdapterView extends LinearLayout {        

		public FavoriteGraphListAdapterView(Context context,Graph graph ) {
            super(context);
            
            //this.setOrientation(HORIZONTAL);   
            //this.setBackgroundColor(Color.parseColor("#222222"));
            
            LinearLayout.LayoutParams imgParams = 
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
            imgParams.setMargins(1, 1, 1, 1);
            ImageView graphImg = new ImageView(context);
            graphImg.setImageResource(R.drawable.graph);
            graphImg.setAdjustViewBounds(true);
            graphImg.setPadding(0, 20, 0, 20);

            graphImg.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            addView( graphImg, imgParams);
            
            LinearLayout.LayoutParams graphParams = 
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
            graphParams.setMargins(1, 1, 1, 1);

            TextView garphControl = new TextView( context );
            //garphControl.setTextAppearance( context, R.style.HostList );
            garphControl.setPadding(0, 20, 0, 20);
            garphControl.setText( graph.getHostNmae() + ": "+graph.getName());
            garphControl.setGravity(Gravity.LEFT);
            addView( garphControl, graphParams);       

        }
}

public class FavoriteGraphListAdapter extends BaseAdapter {

    private Context context;
    private List<Graph> graphList;

    public FavoriteGraphListAdapter(Context context, List<Graph> graphList ) { 
        this.context = context;
        this.graphList = graphList;
    }

    public int getCount() {                        
        return graphList.size();
    }

    public Object getItem(int position) {     
        return graphList.get(position);
    }

    public long getItemId(int position) {  
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) { 
    	Graph graph = graphList.get(position);
        return new FavoriteGraphListAdapterView(this.context, graph );
    }

}