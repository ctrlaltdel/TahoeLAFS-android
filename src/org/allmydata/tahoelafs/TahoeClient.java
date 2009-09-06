package org.allmydata.tahoelafs;

import java.io.IOException;
import org.json.JSONArray;

public class TahoeClient {
	private static String TAG = "TahoeClient";
	private String node;
	private RESTClient rest;
	
	public TahoeClient(String url) {
		node = url;
		rest = new RESTClient();
	}
	
	public void uploadFile(String dir, String filename, String src) throws Exception {
		rest.put(node + "/uri/" + dir + "/" + filename, src);
	}
	
	public void downloadFile(String cap, String dst) throws IOException {
		rest.download(node + "/uri/" + cap, dst);
	}
	
	public TahoeDirectory getDirectory(String cap) throws Exception {
		JSONArray json = rest.getJSON(node + "/uri/" + cap + "/?t=json");
		return new TahoeDirectory(json);	
	}
}
