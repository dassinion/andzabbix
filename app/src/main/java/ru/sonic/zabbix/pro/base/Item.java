package ru.sonic.zabbix.pro.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.sonic.zabbix.pro.R;

/**
 * Object representing a Zabbix Item
 * 
 * @author dassinion
 * 
 */
public class Item extends ZabbixObject {
	protected static String TAG = "ZabbixItem";
	/**
	 * 
	 */
	private static final long serialVersionUID = -2100624850747391912L;

	public Item(Map<?, ?> m) {
		super(m);
	}

	public String lastValue() {
		try {
			return (String) get("lastvalue");
		} catch (Exception e) {
			return "";
		}
	}

	public String getDescription() {
		try {
			return (String) get("description");
		} catch (Exception e) {
			return "";
		}
	}

	public String getName() {
		try {
			return (String) get("name");
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public int getStatusString() {
		try {
			String status = getStatus();
			if (status.equals("0"))
				return R.string.activated;
			else if (status.equals("1"))
				return R.string.disable;
			else if (status.equals("3"))
				return R.string.not_supported;
			else
				return R.string.unknown;
		} catch (Exception e) {
			return R.string.unknown;
		}
	}

	public int getStatusImage() {
		String status = getStatus();
		if (status.equals("0"))
			return R.drawable.ok_icon;
		else if (status.equals("1"))
			return R.drawable.zabbix_unknown;
		else if (status.equals("3"))
			return R.drawable.error_icon;
		else
			return R.drawable.zabbix_unknown;
	}

	public String getError() {
		try {
			return (String) get("error");
		} catch (Exception e) {
			return "";
		}
	}

	public String getType() {
		try {
			return (String) get("type");
		} catch (Exception e) {
			return "";
		}
	}

	public String getValueType() {
		try {
			return (String) get("value_type");
		} catch (Exception e) {
			return "";
		}
	}

	public String getKey() {
		try {
			return (String) get("key_");
		} catch (Exception e) {
			return "";
		}
	}

	public String getID() {
		try {
			return (String) get("itemid");
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public String toString() {
		if (getDescription().equals(""))
			return getName();
		return getDescription();
	}

	public String getExpanedDescription() {
		String desc = "";
		if (getDescription().length() > 0) {
			desc = getDescription();
		} else if (getName().length() > 0) {
			desc = getName();
		}
		// Log.d(TAG, "getExpanedDescription: "+desc);
		String key = getKey();
		String expDesc = "";
		if (desc.indexOf("$1") > 0) {
			// Log.d(TAG, "Description: " + desc);
			String param1 = key.substring(key.indexOf("["),
					key.lastIndexOf("]"));
			String[] params = param1.substring(1, param1.length()).split(",");
			List<String> para = new ArrayList<String>();
			for (String strings : params) {
				para.add(strings);
			}
			expDesc = desc.replace("$1", para.get(0).toString());
		} else {
			expDesc = desc;
		}
		// Log.d(TAG, expDesc);
		return expDesc;
	}
}