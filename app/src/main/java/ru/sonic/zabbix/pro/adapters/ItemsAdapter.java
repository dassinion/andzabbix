package ru.sonic.zabbix.pro.adapters;

import android.content.Context;
import android.graphics.Color;
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

import ru.sonic.zabbix.pro.base.Item;

class ItemsAdapterView extends LinearLayout {
	public static final String TAG = "ItemsAdapterView";

	@SuppressWarnings("deprecation")
	public ItemsAdapterView(Context context, Item item) {
		super(context);
		// this.setBackgroundColor(Color.parseColor(item.getSeverity()));
		//this.setOrientation(HORIZONTAL);
		
		if (item==null)
			return;

		WindowManager mWinMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int displayWidth = mWinMgr.getDefaultDisplay().getWidth();
		int status = Integer.parseInt(item.getStatus());		
		/*
		int textColor = Color.GREEN;
		if (status == 1)
			textColor = Color.GRAY;
		else if (status ==3)
			textColor = Color.RED;
		*/

		TextView description = new TextView(context);
		description.setText(item.getExpanedDescription());
		description.setTextColor(Color.WHITE);
		description.setTextSize(16);
		
		//Log.d(TAG,"Item"+description.getText().toString()+", Type: "+item.getValueType());
		
		TextView value = new TextView(context);
			value.setText(item.lastValue().toString());
			value.setTextColor(Color.WHITE);
			value.setTextSize(12);
			value.setPadding(0, 10, 0, 0);

		ImageView itemStatus = new ImageView(context);
		itemStatus.setImageResource(item.getStatusImage());

		// ##############################################################################3
		TableLayout table = new TableLayout(context);
		// ##############################################################################3

		TableRow row1 = new TableRow(context);

		TableRow.LayoutParams itemNameparam = new TableRow.LayoutParams(
				displayWidth / 2, LayoutParams.WRAP_CONTENT);
		itemNameparam.gravity = Gravity.LEFT;
		itemNameparam.setMargins(6, 4, 4, 4);

		TableRow.LayoutParams valueparams = new TableRow.LayoutParams(
				displayWidth / 3, LayoutParams.WRAP_CONTENT);
		valueparams.gravity = Gravity.RIGHT;
		//valueparams.setMargins(6, 4, 5, 4);

		row1.addView(description, itemNameparam);
		row1.addView(value, valueparams);

		table.addView(row1, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		// ##############################################################################3

		if (status!=0 && item.getError().length()>0) {
			
			TextView errortext = new TextView(context);
			errortext.setText(item.getError());
			errortext.setTextColor(Color.GRAY);
			errortext.setTextSize(14);
			
			TableRow row2 = new TableRow(context);
	
			LinearLayout errortextLayout = new LinearLayout(context);
	
			TableRow.LayoutParams params = new TableRow.LayoutParams();
			params.span = 2;
			errortextLayout.addView(errortext);
			errortextLayout.setLayoutParams(params);
			row2.addView(errortextLayout);
	
			table.addView(row2);
		}

		// #############################################################################3
		LinearLayout itemStatusLayout = new LinearLayout(context);
		itemStatusLayout.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//itemStatusLayout.setOrientation(VERTICAL);

		itemStatusLayout.addView(itemStatus);
		itemStatusLayout.setGravity(Gravity.LEFT);
		itemStatusLayout.setPadding(10, 4, 6, 4);
		// #############################################################################3

		addView(itemStatusLayout);
		addView(table);
	}
}

public class ItemsAdapter extends BaseAdapter {
	private Context context;
	private List<Item> eventList;

	public ItemsAdapter(Context context, List<Item> eventList) {
		this.context = context;
		this.eventList = eventList;
	}

	public int getCount() {
		return eventList.size();
	}

	public Object getItem(int position) {
		return eventList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			Item item = (Item) getItem(position);
			return new ItemsAdapterView(this.context, item);
		} catch (Exception e) {
			return new ItemsAdapterView(this.context, null);
		}
	}
}