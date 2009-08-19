package org.allmydata.tahoelafs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class TahoeDirectory {
	private static String TAG = "TahoeDirectory";
	
	private JSONObject children;
	
	public static enum types {dirnode, filenode, unknown};
	
	public TahoeDirectory(JSONArray json) throws Exception {
		// Check marker
		if (! json.getString(0).equals("dirnode")) {
			Log.e(TAG, "Dirnode marker not found, found that instead: " + json.getString(0));
			throw new Exception("Dirnode marker not found");
		}
		
		// Data
		JSONObject data = json.getJSONObject(1);
		
		// Get children
		children = (JSONObject) data.get("children");
		assert children != null;
	}
	
	public String[] toStringArray() {
		List<String> al = new ArrayList<String>();
					
	    Iterator<String> iter = children.keys();
		    
	    while (iter.hasNext()) {
	    	String entry = (String) iter.next();
	    	Log.i(TAG, "entry:" + entry);
			al.add(entry);
		}

	    String str [] = (String []) al.toArray (new String [al.size ()]);
		return str;
	}
	
	public JSONArray getItem(int id) throws Exception {
		String key = children.names().getString(id);
		JSONArray item = children.getJSONArray(key);
		
		Log.d(TAG, "Return item " + key + " with id " + id);
		return item;
	}
	
	public types getType(int id) throws Exception {
		Log.d(TAG, "getTypes(" + id + ")");
		
		JSONArray item = getItem(id);		
		String type = item.getString(0);
		Log.d(TAG, "type = " + type);

		if (type.equals("dirnode")) {
			return types.dirnode;
		}

		if (type.equals("filenode")) {
			return types.filenode;
		}
		
		return types.unknown;
	}
	
	public String getReadCap(int id) throws Exception {
		JSONArray item = getItem(id);
		
		String cap = item.getJSONObject(1).getString("ro_uri");
		Log.d(TAG, "Cap = " + cap);
		return cap;
	}
}
