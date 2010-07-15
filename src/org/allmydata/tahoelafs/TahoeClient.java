package org.allmydata.tahoelafs;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;

public class TahoeClient {
    // private static String TAG = "TahoeClient";
    private String node;
    private RESTClient rest;

    public TahoeClient(String url) {
        node = url;
        rest = new RESTClient();
    }

    public void uploadFile(String dir, String filename, String src) throws ClientProtocolException,
            IOException, IllegalArgumentException {
        rest.put(node + "/uri/" + dir + "/" + filename, src);
    }

    public void uploadFile(String dir, String filename, InputStream src, long len)
            throws ClientProtocolException, IOException, IllegalArgumentException {
        rest.put(node + "/uri/" + dir + "/" + filename, src, len);
    }

    public void downloadFile(String cap, String dst) throws IOException {
        rest.download(node + "/uri/" + cap, dst);
    }

    public TahoeDirectory getDirectory(String cap) throws Exception {
        JSONArray json = rest.getJSON(node + "/uri/" + cap + "/?t=json");
        return new TahoeDirectory(json);
    }
}
