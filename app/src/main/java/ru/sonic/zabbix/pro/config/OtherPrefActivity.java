package ru.sonic.zabbix.pro.config;

import ru.sonic.zabbix.pro.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * display/edit user prefs
 * @author dassinion
 *
 */
public class OtherPrefActivity extends PreferenceActivity implements
OnSharedPreferenceChangeListener {
	
	@SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.config_other);
        
        CheckBoxPreference use_auto_reftriggers = (CheckBoxPreference)findPreference("use_auto_reftriggers");
    	CheckBoxPreference use_timecorrection = (CheckBoxPreference)findPreference("use_timecorrection");
        ListPreference autoref_period = (ListPreference)findPreference("autoref_period");
    	ListPreference time_correction = (ListPreference)findPreference("time_correction");
    	
    	autoref_period.setSummary(getResources().getString(R.string.auto_refresh_period) +": "+autoref_period.getValue());
    	time_correction.setSummary(getResources().getString(R.string.time_zone_correction) + ": " +time_correction.getValue()+" hour(s)");
    	
    	if (use_auto_reftriggers.isChecked())
    		autoref_period.setEnabled(true);
    	else 
    		autoref_period.setEnabled(false);
    	
    	if (use_timecorrection.isChecked())
    		time_correction.setEnabled(true);
    	else
    		time_correction.setEnabled(false);
    }
    
	@SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	CheckBoxPreference use_auto_reftriggers = (CheckBoxPreference)findPreference("use_auto_reftriggers");
    	CheckBoxPreference use_timecorrection = (CheckBoxPreference)findPreference("use_timecorrection");
    	ListPreference autoref_period = (ListPreference)findPreference("autoref_period");
    	ListPreference time_correction = (ListPreference)findPreference("time_correction");
    	
    	if (key.equals("use_auto_reftriggers") && use_auto_reftriggers.isChecked()) {
    		autoref_period.setEnabled(true);
    	} else if (key.equals("use_auto_reftriggers") && !use_auto_reftriggers.isChecked()) {
    		autoref_period.setEnabled(false);
    	}
    	
    	if (key.equals("use_timecorrection") && use_timecorrection.isChecked()) {
    		time_correction.setEnabled(true);
    	} else if (key.equals("use_timecorrection") && !use_timecorrection.isChecked()) {
    		time_correction.setEnabled(false);
    	}
    	
    	if (key.equals("autoref_period"))
    	autoref_period.setSummary(getResources().getString(R.string.auto_refresh_period) +": "+autoref_period.getValue());
    	if (key.equals("time_correction"))
    	time_correction.setSummary(getResources().getString(R.string.time_zone_correction) + ": " +time_correction.getValue()+" hour(s)");
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

}
