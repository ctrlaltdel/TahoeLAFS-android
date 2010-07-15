package org.allmydata.tahoelafs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

public class Send extends Activity {

    private final static String TAG = "Send";
    private static final String[] COLUMNS = { OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Exit if wrong action or if no file data
        Intent intent = getIntent();
        if (!intent.getAction().equals(Intent.ACTION_SEND) && !intent.hasExtra(Intent.EXTRA_STREAM)) {
            Log.i(TAG, "No valid intent data");
            finish();
        }

        // Get intent data and upload file
        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            Log.i(TAG, "uri=" + uri.getPath());

            // Get Node and Capability
            String node = prefs.getString(Prefs.KEY_NODE, getString(R.string.test_node));
            String cap = prefs.getString(Prefs.KEY_ROOTCAP, getString(R.string.test_rootcap));

            String name = null;
            String scheme = uri.getScheme();
            TahoeClient tahoe = new TahoeClient(node);

            if (scheme != null) {
                if (scheme.equals("content")) {
                    // Content: URI
                    long size = 0;
                    Cursor c = managedQuery(uri, COLUMNS, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        name = encode(c.getString(0));
                        size = c.getLong(1);
                    }
                    c.close();
                    // Upload file
                    if (size > 0) {
                        try {
                            tahoe.uploadFile(cap, name, getContentResolver().openInputStream(uri),
                                    size);
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (scheme.equals("file")) {
                    // File: URI
                    name = uri.getLastPathSegment();
                    String path = uri.getPath();
                    if (path != null) {
                        File file = new File(path);
                        if (file.canRead()) {
                            // Upload file
                            try {
                                tahoe.uploadFile(cap, name, encode(file.getCanonicalPath()));
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e(TAG, "file is not readable");
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "File not supported: " + scheme,
                            Toast.LENGTH_SHORT);
                }
            } else {
                Log.i(TAG, "scheme is null");
            }
        } else {
            Log.i(TAG, "uri is null");
        }
    }

    private String encode(String name) {
        return URLEncoder.encode(name);
    }
}
