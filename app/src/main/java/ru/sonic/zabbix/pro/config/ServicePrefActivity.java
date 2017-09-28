package ru.sonic.zabbix.pro.config;

import java.util.ArrayList;

import ru.sonic.zabbix.pro.R;
import ru.sonic.zabbix.pro.service.ZBXCheckService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

/**
 * display/edit user prefs
 * @author dassinion
 * 
 */
public class ServicePrefActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "ServicePrefActivity";

	@SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.config_service);
        
        ArrayList<String> alarmsArray = new ArrayList<String>();
        ArrayList<Uri> alarmsUriArray = new ArrayList<Uri>();
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
        	String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
        	//String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
        	Uri uri2 = manager.getRingtoneUri(cursor.getPosition());
        	// Do something with the title and the URI of ringtone
        	alarmsArray.add(title);
        	alarmsUriArray.add(uri2);
        	//Log.d(TAG,"List uri: "+uri2);
        }

        CharSequence[] alarms = new CharSequence[alarmsArray.size()];
        CharSequence[] alarmsuri = new CharSequence[alarmsArray.size()];
        int k = 0;
        for (k=0;k<alarmsArray.size();k++) {
        	alarms[k] = alarmsArray.get(k);
        	alarmsuri[k] = alarmsUriArray.get(k).toString();
        }
        ListPreference melody_list = (ListPreference)findPreference("service_alert_sound_melody");
        melody_list.setEntries(alarms);
        melody_list.setEntryValues(alarmsuri);
        
        CheckBoxPreference cbpr1 = (CheckBoxPreference)findPreference("service_alert_sound_custom_melody");
        ListPreference cbmelody = (ListPreference)findPreference("service_alert_sound_melody");
		if (cbpr1.isChecked())
			cbmelody.setEnabled(true);
		else
			cbmelody.setEnabled(false);
		
		CheckBoxPreference alert_melody = (CheckBoxPreference)findPreference("service_alert_sound");
		CheckBoxPreference alert_sound_custom = (CheckBoxPreference)findPreference("service_alert_sound_custom_melody");
		if (alert_melody.isChecked())
			alert_sound_custom.setEnabled(true);
		else
			alert_sound_custom.setEnabled(false);
    }
    
	@SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	if (key.equals("service_enable")) {
    		CheckBoxPreference cbpr = (CheckBoxPreference)findPreference("service_enable");
    		if (cbpr.isChecked())
    			startService(new Intent(this, ZBXCheckService.class));
    		else
    			stopService(new Intent(this, ZBXCheckService.class));
    	} else if (key.equals("service_update_interval")) {
    		stopService(new Intent(this, ZBXCheckService.class));
    		startService(new Intent(this, ZBXCheckService.class));
    	} else if (key.equals("service_alert_sound_custom_melody")) {
    		ListPreference cbmelody = (ListPreference)findPreference("service_alert_sound_melody");
    		CheckBoxPreference cbpr1 = (CheckBoxPreference)findPreference("service_alert_sound_custom_melody");
    		if (cbpr1.isChecked())
    			cbmelody.setEnabled(true);
    		else
    			cbmelody.setEnabled(false);
    	} else if (key.equals("service_alert_sound_melody")) {
    		ListPreference cbmelody = (ListPreference)findPreference("service_alert_sound_melody");
    		//Log.d(TAG,"service_alert_sound_melody changed to: "+cbmelody.getValue());
    	} else if (key.equals("service_alert_sound")) {
    		CheckBoxPreference alert_melody = (CheckBoxPreference)findPreference("service_alert_sound");
    		CheckBoxPreference alert_sound_custom = (CheckBoxPreference)findPreference("service_alert_sound_custom_melody");
    		if (alert_melody.isChecked())
    			alert_sound_custom.setEnabled(true);
    		else
    			alert_sound_custom.setEnabled(false);
    	}
    }
 
	@SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

	@SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}