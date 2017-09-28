package ru.sonic.zabbix.pro.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.adapters.DiffAdapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class GraphsFilterActivity extends Activity {
	private static final String DECORDER = "graph_decorder";
	private static final String Hostsortfield = "graph_hostsortfield";
	private static final String Hostsortfieldpos = "graph_hostsortfieldpos";
	private static final String FilterHostByName = "graph_filterHostByName";
	private static final String FilterHostName = "graph_filterHostName";
	private static final String FilterHostByDns = "graph_filterHostByDns";
	private static final String FilterHostDns = "graph_filterHostDns";
	private static final String FilterHostByIP = "graph_filterHostByIP";
	private static final String FilterHostIP = "graph_filterHostIP";
	private static final String FilterHostByPort = "graph_filterHostByPort";
	private static final String FilterHostPort = "graph_filterHostPort";
	private static final String FilterHostByStatus = "graph_filterHostByStatus";
	private static final String FilterHostStatusPos = "graph_filterHostStatusPos";
	private static final String FilterHostStatusVal = "graph_filterHostStatusVal";
	private static final String FilterHostByAvailability = "graph_filterHostByAvailability";
	private static final String FilterHostAvailabilityPos = "graph_filterHostAvailabilityPos";
	private static final String FilterHostAvailabilityVal = "graph_filterHostAvailabilityVal";
	private static final String FilterHostByUseIpmi = "graph_filterHostByUseIpmi"; 
	private static final String FilterHostByUseSNMP = "graph_filterHostByUseSNMP";
	private static final String FilterHostByMaintenced = "graph_filterHostByMaintenced"; 
	private static final String OnlyWithItem = "graph_onlyWithItem";
	private static final String OnlyMonItem = "graph_onlyMonItem";
	private static final String OnlyHistItems = "graph_onlyHistItems";
	private static final String OnlyWithTriggers = "graph_onlyWithTriggers";
	private static final String OnlyMonTrig = "graph_onlyMonTrig";
	private static final String OnlyHttpTest = "graph_onlyHttpTest";
	private static final String OnlyMonHttpTest = "graph_onlyMonHttpTest"; 
	private static final String OnlyithGraphs = "graph_onlyithGraphs";
	private static final String RetProxies = "graph_retProxies";
	private static final String TAG = "GraphFilterActivity";
	
	View filters;
	View filters2;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    LayoutInflater inflater = LayoutInflater.from(this);
	    List<View> pages = new ArrayList<View>();
		
	    filters = inflater.inflate(R.layout.filter, null);
	    pages.add(filters);

		filters2 = inflater.inflate(R.layout.filters2, null);
	    pages.add(filters2);

		DiffAdapter pagerAdapter = new DiffAdapter(pages);
	    ViewPager viewPager = new ViewPager(this);
	    viewPager.setAdapter(pagerAdapter);
	    viewPager.setCurrentItem(0);

	    loadstates();
	    
	    Button reset = (Button)filters.findViewById(R.id.resetAllFilters);
	    reset.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resetAll();
			};
		});
	    
	    setContentView(viewPager);
	}
	
	public String getsParam(String param){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(param,"");
	}
	
	public void setsParam(String param, String value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(param, value);
        editor.commit();
	}
	
	public boolean getbParam(String param){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(param,false);
	}
	
	public void setbParam(String param, boolean value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(param, value);
        editor.commit();
	}
	
	public void setSortorder(boolean sort){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(DECORDER, sort);
        editor.commit();
	}
	
	public boolean getSortorder(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(DECORDER,false);
	}
	
	public void loadstates() {
		
		/* Filters 1 Loading */
		
		Spinner hostsorting = (Spinner)filters.findViewById(R.id.hostsorting);
    	String val = getsParam(Hostsortfieldpos);
    	if (val.length()>0) {
    		try {
    			hostsorting.setSelection(Integer.parseInt(val));
    		} catch (Exception e) {}
    	}
    	
    	RadioButton descorder = (RadioButton)filters.findViewById(R.id.orderHostDesc);
    	RadioButton ascorder = (RadioButton)filters.findViewById(R.id.orderHostAsc);
    	descorder.setChecked(getSortorder());
    	if (descorder.isChecked())
    		ascorder.setChecked(false);
    	else
    		ascorder.setChecked(true);
    	
    	if (getbParam(FilterHostByName)) {
    		CheckBox filterHostByName = (CheckBox)filters.findViewById(R.id.filterHostByName);	
    		filterHostByName.setChecked(true);
    		TextView tw = (TextView)filters.findViewById(R.id.filterHostName);
    		tw.setText(getsParam(FilterHostName));
    	} else {
    		CheckBox filterHostByName = (CheckBox)filters.findViewById(R.id.filterHostByName);	
    		filterHostByName.setChecked(false);
    		TextView tw = (TextView)filters.findViewById(R.id.filterHostName);
    		tw.setText("");
    	}
    	
    	if (getbParam(FilterHostByDns)) {
    		CheckBox filterHostByDns = (CheckBox)filters.findViewById(R.id.filterHostByDny);
    		filterHostByDns.setChecked(true);
    		TextView tw = (TextView)filters.findViewById(R.id.filterHostDNStext);
    		tw.setText(getsParam(FilterHostDns));
    	} else {
    		CheckBox filterHostByDns = (CheckBox)filters.findViewById(R.id.filterHostByDny);
    		filterHostByDns.setChecked(false);
    		TextView tw = (TextView)filters.findViewById(R.id.filterHostDNStext);
    		tw.setText("");
    	}
    	
    	
    	if (getbParam(FilterHostByIP)) {
    		CheckBox filterHostByIP = (CheckBox)filters.findViewById(R.id.filterHostByIp);
    		filterHostByIP.setChecked(true);
    		TextView tw = (TextView)filters.findViewById(R.id.filterHostIP);
    		tw.setText(getsParam(FilterHostIP));
    	} else {
    		CheckBox filterHostByIP = (CheckBox)filters.findViewById(R.id.filterHostByIp);
    		filterHostByIP.setChecked(false);
    		TextView tw = (TextView)filters.findViewById(R.id.filterHostIP);
    		tw.setText("");
    	}

    	if (getbParam(FilterHostByPort)) {
    		CheckBox filterHostByIP = (CheckBox)filters.findViewById(R.id.filterHostByPort);
    		filterHostByIP.setChecked(true);
    		TextView tw = (TextView)filters.findViewById(R.id.filterHostPort);
    		tw.setText(getsParam(FilterHostPort));
    	} else {
    		CheckBox filterHostByIP = (CheckBox)filters.findViewById(R.id.filterHostByPort);
    		filterHostByIP.setChecked(false);
    		TextView tw = (TextView)filters.findViewById(R.id.filterHostPort);
    		tw.setText("");
    	}
    	
    	if (getbParam(FilterHostByStatus)) {
    		CheckBox filterHostByStatus = (CheckBox)filters.findViewById(R.id.filterHostByStatus);
    		filterHostByStatus.setChecked(true);
    		Spinner hostStatuses = (Spinner)filters.findViewById(R.id.filterHostStatus);
    		hostStatuses.setSelection(Integer.parseInt(getsParam(FilterHostStatusPos)));
    	} else {
    		CheckBox filterHostByStatus = (CheckBox)filters.findViewById(R.id.filterHostByStatus);
    		filterHostByStatus.setChecked(false);
    		Spinner hostStatuses = (Spinner)filters.findViewById(R.id.filterHostStatus);
    		hostStatuses.setSelection(0);
    	}
    	
    	if (getbParam(FilterHostByAvailability)) {
    		CheckBox filterHostByAvail = (CheckBox)filters.findViewById(R.id.filterHostByAvailability);
    		filterHostByAvail.setChecked(true);
    		Spinner hostAvails = (Spinner)filters.findViewById(R.id.filterHostAvailability);
    		hostAvails.setSelection(Integer.parseInt(getsParam(FilterHostAvailabilityPos)));
    	} else {
    		CheckBox filterHostByAvail = (CheckBox)filters.findViewById(R.id.filterHostByAvailability);
    		filterHostByAvail.setChecked(false);
    		Spinner hostAvails = (Spinner)filters.findViewById(R.id.filterHostAvailability);
    		hostAvails.setSelection(0);
    	}
    	
    	if (getbParam(FilterHostByUseIpmi)) {
    		CheckBox f = (CheckBox)filters.findViewById(R.id.filterHostByUseIpmi);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters.findViewById(R.id.filterHostByUseIpmi);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(FilterHostByUseSNMP)) {
    		CheckBox f = (CheckBox)filters.findViewById(R.id.filterHostByUseSNMP);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters.findViewById(R.id.filterHostByUseSNMP);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(FilterHostByMaintenced)) {
    		CheckBox f = (CheckBox)filters.findViewById(R.id.filterHostByMaintences);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters.findViewById(R.id.filterHostByMaintences);
    		f.setChecked(false);
    	}
    	
    	/* Filters 1 Loaded*/
    	/* Load Filters 2*/
    	
    	if (getbParam(OnlyWithItem)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyWithItem);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyWithItem);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(OnlyMonItem)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyMonItem);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyMonItem);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(OnlyHistItems)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyHistItems);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyHistItems);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(OnlyWithTriggers)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyWithTriggers);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyWithTriggers);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(OnlyMonTrig)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyMonTrig);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyMonTrig);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(OnlyHttpTest)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyHttpTest);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyHttpTest);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(OnlyMonHttpTest)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyMonHttpTest);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyMonHttpTest);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(OnlyithGraphs)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyithGraphs);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.onlyithGraphs);
    		f.setChecked(false);
    	}
    	
    	if (getbParam(RetProxies)) {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.retProxies);
    		f.setChecked(true);
    	} else {
    		CheckBox f = (CheckBox)filters2.findViewById(R.id.retProxies);
    		f.setChecked(false);
    	}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	RadioButton descorder = (RadioButton)filters.findViewById(R.id.orderHostDesc);
		    if (descorder.isChecked())
		    	setSortorder(true);
		    else
		    	setSortorder(false);
		    	
		    Spinner hostsorting = (Spinner)filters.findViewById(R.id.hostsorting);
		    int pos = hostsorting.getSelectedItemPosition();
		    setsParam(Hostsortfield, getResources().getStringArray(R.array.HostSortValArray)[pos]);
		    setsParam(Hostsortfieldpos, pos+"");

	    	CheckBox filterHostByName = (CheckBox)filters.findViewById(R.id.filterHostByName);
	    	if (filterHostByName.isChecked()) {
	    		setbParam(FilterHostByName, true);
	    		TextView tw = (TextView)filters.findViewById(R.id.filterHostName);
	    		setsParam(FilterHostName,tw.getText().toString());
	    	} else {
	    		setbParam(FilterHostByName, false);
	    		setsParam(FilterHostName,"");
	    	}
	    	
	    	CheckBox filterHostByDns = (CheckBox)filters.findViewById(R.id.filterHostByDny);
	    	if (filterHostByDns.isChecked()) {
	    		setbParam(FilterHostByDns, true);
	    		TextView tw = (TextView)filters.findViewById(R.id.filterHostDNStext);
	    		setsParam(FilterHostDns,tw.getText().toString());
	    	} else {
	    		setbParam(FilterHostByDns, false);
	    		setsParam(FilterHostDns,"");
	    	}
	    	
	    	CheckBox filterHostByIP = (CheckBox)filters.findViewById(R.id.filterHostByIp);
	    	if (filterHostByIP.isChecked()) {
	    		setbParam(FilterHostByIP, true);
	    		TextView tw = (TextView)filters.findViewById(R.id.filterHostIP);
	    		setsParam(FilterHostIP,tw.getText().toString());
	    	} else {
	    		setbParam(FilterHostByIP, false);
	    		setsParam(FilterHostIP,"");
	    	}
	    	
	    	CheckBox filterHostByPort = (CheckBox)filters.findViewById(R.id.filterHostByPort);
	    	if (filterHostByPort.isChecked()) {
	    		setbParam(FilterHostByPort, true);
	    		TextView tw = (TextView)filters.findViewById(R.id.filterHostPort);
	    		setsParam(FilterHostPort,tw.getText().toString());
	    	} else {
	    		setbParam(FilterHostByPort, false);
	    		setsParam(FilterHostPort,"");
	    	}
	    	
	    	CheckBox filterHostByStatus = (CheckBox)filters.findViewById(R.id.filterHostByStatus);
	    	if (filterHostByStatus.isChecked()) {
	    		setbParam(FilterHostByStatus, true);
	    		Spinner hostStatuses = (Spinner)filters.findViewById(R.id.filterHostStatus);
	    		int statusPos = hostStatuses.getSelectedItemPosition();
	    		setsParam(FilterHostStatusPos, statusPos+"");
	    		setsParam(FilterHostStatusVal, getResources().getStringArray(R.array.HostStatusValues)[statusPos]);
	    	} else {
	    		setbParam(FilterHostByStatus, false);
	    		setsParam(FilterHostStatusPos,"");
	    		setsParam(FilterHostStatusPos,"");
	    	}
	    	
	    	CheckBox filterHostByAvailability = (CheckBox)filters.findViewById(R.id.filterHostByAvailability);
	    	if (filterHostByAvailability.isChecked()) {
	    		setbParam(FilterHostByAvailability, true);
	    		Spinner hostAvailabilityes = (Spinner)filters.findViewById(R.id.filterHostAvailability);
	    		int availPos = hostAvailabilityes.getSelectedItemPosition();
	    		setsParam(FilterHostAvailabilityPos, availPos+"");
	    		setsParam(FilterHostAvailabilityVal, getResources().getStringArray(R.array.HostAvailabilityValues)[availPos]);
	    	} else {
	    		setbParam(FilterHostByAvailability, false);
	    		setsParam(FilterHostAvailabilityPos,"");
	    		setsParam(FilterHostAvailabilityVal,"");
	    	} 
	    	
	    	CheckBox filterHostByUseipmi = (CheckBox)filters.findViewById(R.id.filterHostByUseIpmi);
	    	if (filterHostByUseipmi.isChecked()) {
	    		setbParam(FilterHostByUseIpmi, true);
	    	} else {
	    		setbParam(FilterHostByUseIpmi, false);
	    	}
	    	
	    	CheckBox filterHostByUseSnmp = (CheckBox)filters.findViewById(R.id.filterHostByUseSNMP);
	    	if (filterHostByUseSnmp.isChecked()) {
	    		setbParam(FilterHostByUseSNMP, true);
	    	} else {
	    		setbParam(FilterHostByUseSNMP, false);
	    	}
	    	
	    	CheckBox filterHostByMaintenced = (CheckBox)filters.findViewById(R.id.filterHostByMaintences);
	    	if (filterHostByMaintenced.isChecked()) {
	    		setbParam(FilterHostByMaintenced, true);
	    	} else {
	    		setbParam(FilterHostByMaintenced, false);
	    	}
	    	
	    	/* Filters 1 saved*/
	    	/* Saving Filters 2*/
	    	
	    	CheckBox filterHostOnlyItems = (CheckBox)filters2.findViewById(R.id.onlyWithItem);
	    	if (filterHostOnlyItems.isChecked()) {
	    		setbParam(OnlyWithItem, true);
	    	} else {
	    		setbParam(OnlyWithItem, false);
	    	}
	    	
	    	CheckBox filterHostonlyMonItem = (CheckBox)filters2.findViewById(R.id.onlyMonItem);
	    	if (filterHostonlyMonItem.isChecked()) {
	    		setbParam(OnlyMonItem, true);
	    	} else {
	    		setbParam(OnlyMonItem, false);
	    	}
	    	
	    	CheckBox filterHostonlyHistItems = (CheckBox)filters2.findViewById(R.id.onlyHistItems);
	    	if (filterHostonlyHistItems.isChecked()) {
	    		setbParam(OnlyHistItems, true);
	    	} else {
	    		setbParam(OnlyHistItems, false);
	    	}
	    	
	    	CheckBox filterHostonlyWithTriggers = (CheckBox)filters2.findViewById(R.id.onlyWithTriggers);
	    	if (filterHostonlyWithTriggers.isChecked()) {
	    		setbParam(OnlyWithTriggers, true);
	    	} else {
	    		setbParam(OnlyWithTriggers, false);
	    	}
	    	
	    	CheckBox filterHostonlyMonTrig = (CheckBox)filters2.findViewById(R.id.onlyMonTrig);
	    	if (filterHostonlyMonTrig.isChecked()) {
	    		setbParam(OnlyMonTrig, true);
	    	} else {
	    		setbParam(OnlyMonTrig, false);
	    	}
	    	
	    	CheckBox filterHostonlyHttpTest = (CheckBox)filters2.findViewById(R.id.onlyHttpTest);
	    	if (filterHostonlyHttpTest.isChecked()) {
	    		setbParam(OnlyHttpTest, true);
	    	} else {
	    		setbParam(OnlyHttpTest, false);
	    	}
	    	
	    	CheckBox filterHostonlyMonHttpTest = (CheckBox)filters2.findViewById(R.id.onlyMonHttpTest);
	    	if (filterHostonlyMonHttpTest.isChecked()) {
	    		setbParam(OnlyMonHttpTest, true);
	    	} else {
	    		setbParam(OnlyMonHttpTest, false);
	    	}
	    	
	    	CheckBox filterHostonlyithGraphs = (CheckBox)filters2.findViewById(R.id.onlyithGraphs);
	    	if (filterHostonlyithGraphs.isChecked()) {
	    		setbParam(OnlyithGraphs, true);
	    	} else {
	    		setbParam(OnlyithGraphs, false);
	    	}
	    	
	    	CheckBox filterHostretProxies = (CheckBox)filters2.findViewById(R.id.retProxies);
	    	if (filterHostretProxies.isChecked()) {
	    		setbParam(RetProxies, true);
	    	} else {
	    		setbParam(RetProxies, false);
	    	}
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void resetAll(){
	    setSortorder(false);
   		setbParam(FilterHostByName, false);
   		setsParam(FilterHostName,"");
   		setbParam(FilterHostByDns, false);
   		setsParam(FilterHostDns,"");
   		setbParam(FilterHostByIP, false);
   		setsParam(FilterHostIP,"");
   		setbParam(FilterHostByPort, false);
   		setsParam(FilterHostPort,"");
   		setbParam(FilterHostByStatus, false);
   		setsParam(FilterHostStatusPos,"");
   		setsParam(FilterHostStatusPos,"");
   		setbParam(FilterHostByAvailability, false);
   		setsParam(FilterHostAvailabilityPos,"");
   		setsParam(FilterHostAvailabilityVal,"");
   		setbParam(FilterHostByUseIpmi, false);
   		setbParam(FilterHostByUseSNMP, false);
   		setbParam(FilterHostByMaintenced, false);
    	
    	/* Filters 1 saved*/
    	/* Saving Filters 2*/
    	
   		setbParam(OnlyWithItem, false);
   		setbParam(OnlyMonItem, false);
   		setbParam(OnlyHistItems, false);
   		setbParam(OnlyWithTriggers, false);
   		setbParam(OnlyMonTrig, false);
   		setbParam(OnlyHttpTest, false);
   		setbParam(OnlyMonHttpTest, false);
   		setbParam(OnlyithGraphs, false);
   		setbParam(RetProxies, false);
   		Log.d(TAG,"States reset");
   		loadstates();
	}
	
	   @Override
	    public void onStart() {
	      super.onStart();
	      EasyTracker.getInstance(this).activityStart(this);
	    }

	    @Override
	    public void onStop() {
	      super.onStop();
	      EasyTracker.getInstance(this).activityStop(this);
	    }
}