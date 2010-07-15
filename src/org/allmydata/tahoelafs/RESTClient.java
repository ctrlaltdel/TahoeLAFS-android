package org.allmydata.tahoelafs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class RESTClient {
    private static String TAG = "org.allmydata.tahoelafs.RESTClient";

    private HttpClient httpclient;

    public RESTClient() {
        httpclient = new DefaultHttpClient();
    }

    public String get(String url) {
        InputStream instream = openURL(url);
        String result = convertStreamToString(instream);
        Log.i(TAG, "Result of conversion: [" + result + "]");

        try {
            instream.close();
            return result;
        } catch (IOException e) {
            Log.e("REST", "There was an IO Stream related error", e);
            return null;
        }
    }

    public void put(String url, String filename) throws ClientProtocolException, IOException,
            IllegalArgumentException {
        Log.i(TAG, "PUT " + url);

        HttpPut put = new HttpPut(url);
        put.setEntity(new FileEntity(new File(filename), "text/plain"));
        HttpResponse response = httpclient.execute(put);

        Log.i(TAG, "Status:[" + response.getStatusLine().toString() + "]");
    }

    public void put(String url, InputStream instream, long len) throws ClientProtocolException,
            IOException, IllegalArgumentException {
        Log.i(TAG, "PUT " + url);

        HttpPut put = new HttpPut(url);
        put.setEntity(new InputStreamEntity(instream, len));
        HttpResponse response = httpclient.execute(put);

        Log.i(TAG, "Status:[" + response.getStatusLine().toString() + "]");
    }

    public void download(String url, String dst) throws IOException {
        Log.i(TAG, "Downloading " + url + " to " + dst);

        Log.d(TAG, "Opening url " + url);
        InputStream in = openURL(url);

        Log.d(TAG, "Opening destination file " + dst);
        OutputStream out = new FileOutputStream(dst);

        Log.d(TAG, "Writing file");
        final ReadableByteChannel inputChannel = Channels.newChannel(in);
        final WritableByteChannel outputChannel = Channels.newChannel(out);

        fastChannelCopy(inputChannel, outputChannel);

        inputChannel.close();
        outputChannel.close();

        Log.d(TAG, "Closing HTTP connection");
        in.close();

        Log.d(TAG, "Closing file");
        out.close();
    }

    public JSONArray getJSON(String url) throws Exception {
        String result = get(url);
        try {
            JSONArray json = new JSONArray(result);
            for (int i = 0; i < json.length(); i++) {
                Log
                        .i(TAG, "<jsonname" + i + ">\n" + json.getString(i) + "\n</jsonname" + i
                                + ">\n");
            }
            return json;
        } catch (JSONException e) {
            Log.e("JSON", "There was an error parsing the JSON", e);
            throw new Exception(result);
        }
    }

    /*
     * PRIVATE STUFF
     */

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private InputStream openURL(String url) {
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;

        Log.i(TAG, "GET " + url);

        try {
            try {
                response = httpclient.execute(httpget);
            } catch (SSLException e) {
                Log.i(TAG, "SSL Certificate is not trusted");

                /*
                 * KeyStore trustStore =
                 * KeyStore.getInstance(KeyStore.getDefaultType());
                 * KeyStore.getDefaultType(); FileInputStream in = new
                 * FileInputStream(new
                 * File("data/data/com.alu.myic.android/my.trustore3")); try {
                 * trustStore.load(in, "coucou".toCharArray()); } finally {
                 * in.close(); }
                 * 
                 * SSLSocketFactory socketFactory = new
                 * SSLSocketFactory(trustStore); SchemeRegistry registry = new
                 * SchemeRegistry(); registry.register(new Scheme("https",
                 * socketFactory, 443));
                 */

                response = httpclient.execute(httpget);
            }
            Log.i(TAG, "Status:[" + response.getStatusLine().toString() + "]");
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return entity.getContent();
            }
        } catch (ClientProtocolException e) {
            Log.e("REST", "There was a protocol based error", e);
        } catch (IOException e) {
            Log.e("REST", "There was an IO Stream related error", e);
        }

        return null;
    }

    // Code copied from
    // http://thomaswabner.wordpress.com/2007/10/09/fast-stream-copy-using-javanio-channels/
    private static void fastChannelCopy(final ReadableByteChannel src,
            final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            dest.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // make sure the buffer is fully drained.
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
}
