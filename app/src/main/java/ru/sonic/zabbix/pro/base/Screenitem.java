package ru.sonic.zabbix.pro.base;

import java.util.Map;

import ru.sonic.zabbix.pro.R;
/**
 * Object Representing a Zabbix Host
 * @author dassinion
 *
 */
public class Screenitem extends ZabbixObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final String TAG = "Screen";

	public Screenitem(Map<?, ?> m){
		super(m);
	}
	
	public String getScreenid(){
		return (String)get("screenid");
	}
	
	public String getID(){
		return (String)get("screenitemid");
	}
	
	public String getResourceType(){
		return (String)get("resourcetype");
	}
	
	public int getResourceTypeString() {
		try {
			switch (Integer.parseInt(getResourceType())) {
			case 0:
				return R.string.graph;
			case 1:
				return R.string.simple_graph;
			case 2:
				return R.string.map;
			case 3:
				return R.string.plain_text;
			case 4:
				return R.string.host_info;
			case 5:
				return R.string.triggers_info;
			case 6:
				return R.string.server_info;
			case 7:
				return R.string.clock;
			case 8:
				return R.string.screen;
			case 9:
				return R.string.triggers_overview;
			case 10:
				return R.string.data_owerview;
			case 11:
				return R.string.url;
			case 12:
				return R.string.history_of_action;
			case 13:
				return R.string.history_of_events;
			case 14:
				return R.string.status_hostgr_triggers;
			case 15:
				return R.string.system_status;
			case 16:
				return R.string.status_host_triggers;
			default:
				return R.string.unknown;
			}
		} catch (NullPointerException e) {
			return R.string.unknown;
		} catch (Exception e) {
			return R.string.unknown;
		}
	}
	
	public String getResourceID(){
		return (String)get("resourceid");
	}
	
	public String getWidth(){
		return (String)get("width");
	}
	
	public String getHeight(){
		return (String)get("height");
	}
	
	public String getX(){
		return (String)get("x");
	}
	
	public String getY(){
		return (String)get("y");
	}
	
	public String getColspan(){
		return (String)get("colspan");
	}
	
	public String getRowspan(){
		return (String)get("rowspan");
	}
	
	public String getElements(){
		return (String)get("elements");
	}
	
	public String getValign(){
		return (String)get("valign");
	}
	
	public String getHalign(){
		return (String)get("halign");
	}
	
	public String getStyle(){
		return (String)get("style");
	}
	
	public String geturl(){
		return (String)get("url");
	}
	
	public String getDynamic(){
		return (String)get("dynamic");
	}
	
	public String getSort_triggers(){
		return (String)get("sort_triggers");
	}
}