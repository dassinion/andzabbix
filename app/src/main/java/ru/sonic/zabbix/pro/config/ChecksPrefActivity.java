package ru.sonic.zabbix.pro.config;

import ru.sonic.zabbix.pro.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * display/edit user prefs
 * @author dassinion
 *
 */
public class ChecksPrefActivity extends PreferenceActivity implements
OnSharedPreferenceChangeListener {
	
    @SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.config_checktrigger);
        
        EditTextPreference activetriggertime = (EditTextPreference) findPreference("activetriggertime");
        activetriggertime.setSummary(getResources().getString(R.string.custom_time_period_expanded)+ ": "+activetriggertime.getText());
        
        ListPreference check_type = (ListPreference)findPreference("check_type");
        Integer check_type_int = Integer.parseInt(check_type.getValue());

        set_check_type_properties(check_type_int);
    }
    
    @SuppressWarnings("deprecation")
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		ListPreference check_type = (ListPreference)findPreference("check_type");
        Integer check_type_int = Integer.parseInt(check_type.getValue());
        ListPreference trigger_filter = (ListPreference)findPreference("trigger_filter");
        
    	if (key.equals("activetriggertime")) {
	    	EditTextPreference activetriggertime = (EditTextPreference) findPreference("activetriggertime");
	        activetriggertime.setSummary(getResources().getString(R.string.custom_time_period_expanded)+ ": "+activetriggertime.getText()+" min");
    	}
    	
    	if (key.equals("check_type")) {
    		set_check_type_properties(check_type_int);
    	}
    	
    	if (key.equals("trigger_filter")) {
    		if (trigger_filter.getValue().equals("1")){
    			trigger_filter.setSummary("1");	
    		}
    	}
    }
 
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    
    @SuppressWarnings("deprecation")
    public void set_check_type_properties(int check_type_int){
    	EditTextPreference activetriggertime = (EditTextPreference) findPreference("activetriggertime");
    	ListPreference check_type = (ListPreference)findPreference("check_type");
    	if (check_type_int == 1) {
        	activetriggertime.setEnabled(false);
        	check_type.setSummary("Show triggers in state PROBLEM and recently switched (30 min)");
        } else if (check_type_int == 2) {
    		activetriggertime.setEnabled(true);
    		check_type.setSummary("Show only recently switched triggers (with custom period)");
        } else if (check_type_int == 3) {
        	activetriggertime.setEnabled(false);
        	check_type.setSummary("Show only triggers in state PROBLEM");
        }	
    }
}
