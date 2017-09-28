package ru.sonic.zabbix.pro.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ZBXautostartReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context ctsx, Intent nintent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctsx);
		if (prefs.getBoolean("service_enable", false)) {
			ctsx.startService(new Intent(ctsx, ZBXCheckService.class));
		}
	}
}