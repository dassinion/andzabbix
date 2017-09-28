package ru.sonic.zabbix.pro.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.sonic.zabbix.pro.base.DHost;
import ru.sonic.zabbix.pro.base.Host;
import ru.sonic.zabbix.pro.base.HostInterface;
import ru.sonic.zabbix.pro.base.Server;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class HostApiHandler extends ZabbixAPIHandler {
	private static final String DECORDER = "decorder";
	private static final String Hostsortfield = "hostsortfield";
	//private static final String Hostsortfieldpos = "hostsortfieldpos";
	private static final String FilterHostByName = "filterHostByName";
	private static final String FilterHostName = "filterHostName";
	private static final String FilterHostByDns = "filterHostByDns";
	private static final String FilterHostDns = "filterHostDns";
	private static final String FilterHostByIP = "filterHostByIP";
	private static final String FilterHostIP = "filterHostIP";
	private static final String FilterHostByPort = "filterHostByPort";
	private static final String FilterHostPort = "filterHostPort";
	private static final String FilterHostByStatus = "filterHostByStatus";
	//private static final String FilterHostStatusPos = "filterHostStatusPos";
	private static final String FilterHostStatusVal = "filterHostStatusVal";
	private static final String FilterHostByAvailability = "filterHostByAvailability";
	//private static final String FilterHostAvailabilityPos = "filterHostAvailabilityPos";
	private static final String FilterHostAvailabilityVal = "filterHostAvailabilityVal";
	private static final String FilterHostByUseIpmi = "filterHostByUseIpmi"; 
	private static final String FilterHostByUseSNMP = "filterHostByUseSNMP";
	private static final String FilterHostByMaintenced = "filterHostByMaintenced"; 
	private static final String OnlyWithItem = "onlyWithItem";
	private static final String OnlyMonItem = "onlyMonItem";
	private static final String OnlyHistItems = "onlyHistItems";
	private static final String OnlyWithTriggers = "onlyWithTriggers";
	private static final String OnlyMonTrig = "onlyMonTrig";
	private static final String OnlyHttpTest = "onlyHttpTest";
	private static final String OnlyMonHttpTest = "onlyMonHttpTest"; 
	private static final String OnlyithGraphs = "onlyithGraphs";
	private static final String RetProxies = "retProxies";
	
	private static final String gDECORDER = "graph_decorder";
	private static final String gHostsortfield = "graph_hostsortfield";
	//private static final String gHostsortfieldpos = "graph_hostsortfieldpos";
	private static final String gFilterHostByName = "graph_filterHostByName";
	private static final String gFilterHostName = "graph_filterHostName";
	private static final String gFilterHostByDns = "graph_filterHostByDns";
	private static final String gFilterHostDns = "graph_filterHostDns";
	private static final String gFilterHostByIP = "graph_filterHostByIP";
	private static final String gFilterHostIP = "graph_filterHostIP";
	private static final String gFilterHostByPort = "graph_filterHostByPort";
	private static final String gFilterHostPort = "graph_filterHostPort";
	private static final String gFilterHostByStatus = "graph_filterHostByStatus";
	//private static final String gFilterHostStatusPos = "graph_filterHostStatusPos";
	private static final String gFilterHostStatusVal = "graph_filterHostStatusVal";
	private static final String gFilterHostByAvailability = "graph_filterHostByAvailability";
	//private static final String gFilterHostAvailabilityPos = "graph_filterHostAvailabilityPos";
	private static final String gFilterHostAvailabilityVal = "graph_filterHostAvailabilityVal";
	private static final String gFilterHostByUseIpmi = "graph_filterHostByUseIpmi"; 
	private static final String gFilterHostByUseSNMP = "graph_filterHostByUseSNMP";
	private static final String gFilterHostByMaintenced = "graph_filterHostByMaintenced"; 
	private static final String gOnlyWithItem = "graph_onlyWithItem";
	private static final String gOnlyMonItem = "graph_onlyMonItem";
	private static final String gOnlyHistItems = "graph_onlyHistItems";
	private static final String gOnlyWithTriggers = "graph_onlyWithTriggers";
	private static final String gOnlyMonTrig = "graph_onlyMonTrig";
	private static final String gOnlyHttpTest = "graph_onlyHttpTest";
	private static final String gOnlyMonHttpTest = "graph_onlyMonHttpTest"; 
	private static final String gOnlyithGraphs = "graph_onlyithGraphs";
	private static final String gRetProxies = "graph_retProxies";

	public HostApiHandler(Activity owner, Server zserver) {
		super(owner,zserver);
	}

	/**
	 * get all hosts
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public List<Host> get(String groupID) throws ZabbixAPIException{
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("selectInterfaces", "extend");
		//params.put("selectParentTemplates", "refer");
		
		if (groupID.length() != 0) {params.put("groupids", groupID);}
		
		if (getbParam(OnlyWithItem))
			params.put("with_items", "1");
		if (getbParam(OnlyMonItem))
			params.put("with_monitored_items", "1");
		if (getbParam(OnlyHistItems))
			params.put("with_historical_items", "1");
		if (getbParam(OnlyWithTriggers))
			params.put("with_triggers", "1");
		if (getbParam(OnlyMonTrig))
			params.put("with_monitored_triggers", "1");
		if (getbParam(OnlyHttpTest))
			params.put("with_httptests", "1");
		if (getbParam(OnlyMonHttpTest))
			params.put("with_monitored_httptests", "1");
		if (getbParam(OnlyithGraphs))
			params.put("with_graphs", "1");
		if (getbParam(RetProxies))
			params.put("proxy_hosts", "1");
		
		params.put("sortorder", getSortorder());
		
		String sortfield = getsParam(Hostsortfield);
		if (sortfield.length()!=0 && !sortfield.equals("none"))
			params.put("sortfield", sortfield);
		
		JSONObject filter = new JSONObject();
		
		if (getbParam(FilterHostByName)) {
			if (getsParam(FilterHostName).length()>0) {
				filter.put("host", getsParam(FilterHostName));
			}
		}
		
		if (getbParam(FilterHostByDns)) {
			if (getsParam(FilterHostDns).length()>0) {
				filter.put("dns", getsParam(FilterHostDns));
			}
		}
		
		if (getbParam(FilterHostByIP)) {
			if (getsParam(FilterHostIP).length()>0) {
				filter.put("ip", getsParam(FilterHostIP));
			}
		}
		
		if (getbParam(FilterHostByPort)) {
			if (getsParam(FilterHostPort).length()>0) {
				filter.put("port", getsParam(FilterHostPort));
			}
		}
		
		if (getbParam(FilterHostByStatus)) {
			if (getsParam(FilterHostStatusVal).length()>0) {
				filter.put("status", getsParam(FilterHostStatusVal));
			}
		}
		
		if (getbParam(FilterHostByAvailability)) {
			if (getsParam(FilterHostAvailabilityVal).length()>0) {
				filter.put("available", getsParam(FilterHostAvailabilityVal));
			}
		}
		
		if (getbParam(FilterHostByUseIpmi)) {
			filter.put("ipmi_available", "1");
		}
		
		if (getbParam(FilterHostByUseSNMP)) {
			filter.put("snmp_available", "1");
		}
		
		if (getbParam(FilterHostByMaintenced)) {
			filter.put("maintenanceid","1");
		}
		
		if (filter.size()>0) {
			params.put("filter", filter);
		}
		ArrayList<Host> ret = new ArrayList<Host>();
		JSONArray jsonobjects = requestObject("host.get", params);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Host(obj));
		}
		//Log.d(TAG, "getHosts. ret: " + ret);
		return ret;
	}
	
	/*
	@SuppressWarnings("unchecked")
	public List<Host> getHostInfo(String groupID) throws ZabbixAPIException{
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		ArrayList<Host> ret = new ArrayList<Host>();
		JSONArray jsonobjects = requestObject("host.get", params);	
		JSONObject obj = (JSONObject) jsonobjects.get(0);
		ret.add(new Host(obj));
		return ret;
	} */
	
	/**
	 * get all hosts
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public List<Host> getHostsCount(String groupID) throws ZabbixAPIException{
		JSONObject params = new JSONObject();
		params.put("countOutput", "1");
		if (groupID.length() != 0) {params.put("groupids", groupID);}
		ArrayList<Host> ret = new ArrayList<Host>();
		JSONArray jsonobjects = requestObject("host.get", params);	
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Host(obj));
		}
		return ret;
	}
	
	/**
	 * get all discovered hosts
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public List<DHost> getDHosts() throws ZabbixAPIException{
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		ArrayList<DHost> ret = new ArrayList<DHost>();
		JSONArray jsonobjects = requestObject("dhost.get", params);	
		//Log.d(TAG, "getDHosts. ret: " + ret);
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new DHost(obj));
		}
		//Log.d(TAG, "getDHosts. ret: " + ret);
		return ret;
	}
	
	/**
	 * get all hosts with graphs
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public List<Host> getHostsWithGraphs() throws ZabbixAPIException{
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("with_graphs", "1");
		params.put("selectInterfaces", "extend");
		
		if (getbParam(gOnlyWithItem))
			params.put("with_items", "1");
		if (getbParam(gOnlyMonItem))
			params.put("with_monitored_items", "1");
		if (getbParam(gOnlyHistItems))
			params.put("with_historical_items", "1");
		if (getbParam(gOnlyWithTriggers))
			params.put("with_triggers", "1");
		if (getbParam(gOnlyMonTrig) )
			params.put("with_monitored_triggers", "1");
		if (getbParam(gOnlyHttpTest))
			params.put("with_httptests", "1");
		if (getbParam(gOnlyMonHttpTest))
			params.put("with_monitored_httptests", "1");
		if (getbParam(gRetProxies))
			params.put("proxy_hosts", "1");
		
		params.put("sortorder", getgSortorder());
		
		String sortfield = getsParam(gHostsortfield);
		if (sortfield.length()!=0 && !sortfield.equals("none"))
			params.put("sortfield", sortfield);
		
		JSONObject filter = new JSONObject();
		
		if (getbParam(gFilterHostByName)) {
			if (getsParam(gFilterHostName).length()>0) {
				filter.put("host", getsParam(gFilterHostName));
			}
		}
		
		if (getbParam(gFilterHostByDns)) {
			if (getsParam(gFilterHostDns).length()>0) {
				filter.put("dns", getsParam(gFilterHostDns));
			}
		}
		
		if (getbParam(gFilterHostByIP)) {
			if (getsParam(gFilterHostIP).length()>0) {
				filter.put("ip", getsParam(gFilterHostIP));
			}
		}
		
		if (getbParam(gFilterHostByPort)) {
			if (getsParam(gFilterHostPort).length()>0) {
				filter.put("port", getsParam(gFilterHostPort));
			}
		}
		
		if (getbParam(gFilterHostByStatus)) {
			if (getsParam(gFilterHostStatusVal).length()>0) {
				filter.put("status", getsParam(gFilterHostStatusVal));
			}
		}
		
		if (getbParam(gFilterHostByAvailability)) {
			if (getsParam(gFilterHostAvailabilityVal).length()>0) {
				filter.put("available", getsParam(gFilterHostAvailabilityVal));
			}
		}
		
		if (getbParam(gFilterHostByUseIpmi)) {
			filter.put("ipmi_available", "1");
		}
		
		if (getbParam(gFilterHostByUseSNMP)) {
			filter.put("snmp_available", "1");
		}
		
		if (getbParam(gFilterHostByMaintenced)) {
			filter.put("maintenanceid","1");
		}
		
		if (filter.size()>0) {
			params.put("filter", filter);
		}
		
		ArrayList<Host> ret = new ArrayList<Host>();
		JSONArray jsonobjects = requestObject("host.get", params);
		
		for (Iterator<JSONObject> it = jsonobjects.iterator(); it.hasNext();) {
			JSONObject obj = it.next();
			ret.add(new Host(obj));
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getHostInfo(String hostId) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("output", "extend");
		params.put("selectInterfaces", "extend"); 
		params.put("selectParentTemplates", "extend");
		if (hostId.length() != 0)
			params.put("hostids", hostId);
		return requestObject("host.get", params);
	}

	/**
	 * Create host
	 * @param obj
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public JSONArray create(Host obj) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("host",obj.getHost());
		params.put("name",obj.getName());
		params.put("interfaces",obj.getArrayInterfaces());
		//params.put("proxy_hostid",obj.getproxy_hostid());
		params.put("groups",obj.getGroups());
		params.put("templates",obj.getTemplates());
		return requestObject("host.create", params);
	}
	
	/**
	 * Create host
	 * @param obj
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public JSONArray create_old(Host obj) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("host",obj.getHost());
		params.put("name",obj.getName());
		JSONArray interfarray = obj.getArrayInterfaces();
		JSONObject jinterfobj = (JSONObject) interfarray.get(0);
		HostInterface hinterf = new HostInterface(jinterfobj);
		params.put("ip",hinterf.getIP());
		params.put("dns",hinterf.getDns());
		params.put("port",hinterf.getPort());
		params.put("useip",hinterf.getUseIP());
		//params.put("proxy_hostid",obj.getproxy_hostid());
		params.put("groups",obj.getGroups());
		params.put("templates",obj.getTemplates());
		return requestObject("host.create", params);
	}
	
	/**
	 * Edit host
	 * @param obj
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public JSONArray edit(Host obj) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("hostid", obj.getID());
		params.put("status", obj.getStatus());
		params.put("host",obj.getHost());
		if (obj.getName()!=null)
			params.put("name",obj.getName());
		//params.put("proxy_hostid",obj.getproxy_hostid());
		if (obj.getGroups()!=null && obj.getGroups().size()>0)
			params.put("groups",obj.getGroups());
		if (obj.getTemplates()!=null && obj.getTemplates().size()>0)
			params.put("templates",obj.getTemplates());
		Log.d(TAG,"Host: "+params);
		return requestObject("host.update", params);
	}
	
	/**
	 * Edit host
	 * @param obj
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public JSONArray edit_old(Host obj) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("hostid", obj.getID());
		params.put("status", obj.getStatus());
		params.put("host",obj.getHost());
		if (obj.getName()!=null)
			params.put("name",obj.getName());
		//params.put("proxy_hostid",obj.getproxy_hostid());
		if (obj.getGroups()!=null && obj.getGroups().size()>0)
			params.put("groups",obj.getGroups());
		if (obj.getTemplates()!=null && obj.getTemplates().size()>0)
			params.put("templates",obj.getTemplates());
		JSONArray interfarray = obj.getArrayInterfaces();
		JSONObject jinterfobj = (JSONObject) interfarray.get(0);
		HostInterface hinterf = new HostInterface(jinterfobj);
		params.put("ip",hinterf.getIP());
		params.put("dns",hinterf.getDns());
		params.put("port",hinterf.getPort());
		params.put("useip",hinterf.getUseIP());
		return requestObject("host.update", params);
	}
	
	/**
	 * Edit host status
	 * @param obj
	 * @return
	 * @throws ZabbixAPIException
	 */
	@SuppressWarnings("unchecked")
	public JSONArray set_status(Host obj) throws ZabbixAPIException {
		JSONObject params = new JSONObject();
		params.put("hostid", obj.getID());
		params.put("status", obj.getStatus());
		Log.d(TAG,"Host: "+params);
		return requestObject("host.update", params);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray delete(Host obj) throws ZabbixAPIException {
		JSONObject host = new JSONObject();
		host.put("hostid",obj.getID());
		return requestObject("host.delete", host);
	}
	
	public boolean getbParam(String param){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        return prefs.getBoolean(param,false);
	}
	
	public String getsParam(String param){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        return prefs.getString(param,"");
	}
	
	public String getSortorder(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        if (prefs.getBoolean(DECORDER,false))
        	return "DESC";
        else
        	return "ASC";
	}
	
	public String getgSortorder(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.ctx);
        if (prefs.getBoolean(gDECORDER,false))
        	return "DESC";
        else
        	return "ASC";
	}
}