package ru.sonic.zabbix.pro.ZControl;

import java.util.List;

import ru.sonic.zabbix.pro.activities.DefaultZabbixListActivity;

import ru.sonic.zabbix.pro.api.ProxyApiHandler;

import ru.sonic.zabbix.pro.api.ZabbixAPIException;
import android.os.Bundle;



/**
 * display all hosts
 * @author dassinion
 *
 */
public class ProxiesActivity extends DefaultZabbixListActivity {
	protected static final int CONTEXTMENU_SHOWHOSTPREF = 0;
	protected static final int CONTEXTMENU_SHOWITEMS = 1;
	protected static final int CONTEXTMENU_SHOWHOSTGRAPHS = 2;
	protected static final int CONTEXTMENU_ACTIVATEHOST = 3; 
	protected static final int CONTEXTMENU_DISABLEHOST = 4; 
	private ProxyApiHandler api = null;
	
	@Override
	protected List getData() throws ZabbixAPIException {
		if (api==null)
			api = new ProxyApiHandler(this,getSelectedServer());
		return api.get();
	}
}
