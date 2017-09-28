package ru.sonic.zabbix.pro.ZControl;

import java.util.List;

import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;
import ru.sonic.zabbix.pro.api.AlertApiHandler;
import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import android.os.Bundle;

public class AlertsListActivity extends DefaultZabbixListActivity { 
	private AlertApiHandler api = null;
	
	@Override
	protected List getData() throws ZabbixAPIException {
		api = new AlertApiHandler(this,getSelectedServer());
		return api.get();
	}
}
