package org.allmydata.tahoelafs;

import java.io.IOException;
import org.json.JSONArray;

public class TahoeClient {
	private static String TAG = "TahoeClient";
	private String node;
	
	public TahoeClient(String url) {
		node = url;
	}
	
	public void uploadFile(String directory, String filename, String src) {
		
	}
	
	public void downloadFile(String cap, String dst) throws IOException {
		RESTClient.download(node + "/uri/" + cap, dst);
	}
	
	public TahoeDirectory getDirectory(String cap) throws Exception {
		JSONArray json = RESTClient.getJSON(node + "/uri/" + cap + "/?t=json");
		return new TahoeDirectory(json);	
	}
}
