package org.allmydata.tahoelafs;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class TahoeClient {
	private static String TAG = "TahoeClient";
	private String node;
	
	public TahoeClient(String url) {
		node = url;
	}
	
	public void uploadFile(String directory, String filename, String src) {
		
	}
	
	public void downloadFile(String cap, String dst) throws IOException {
		RESTClient.get(node + "/uri/" + cap, dst);
	}
	
	public TahoeDirectory getDirectory(String cap) throws Exception {
		JSONArray json = RESTClient.getJSON(node + "/uri/" + cap + "/?t=json");
		return new TahoeDirectory(json);	
	}
}
