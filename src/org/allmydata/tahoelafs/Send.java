package org.allmydata.tahoelafs;

import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.preference.PreferenceManager;
import java.io.File;

public class Send extends Activity {

    private final static String TAG = "Send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Exit if wrong action or if no file data
        Intent intent = getIntent();
        if(!intent.getAction().equals(Intent.ACTION_SEND) && !intent.hasExtra(Intent.EXTRA_STREAM)){
            Log.i(TAG, "No valid intent data");
            finish();
        }

        // Get intent data and upload file
        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(uri != null){
            Log.i(TAG, "uri="+uri.getPath());
            File file = new File(uri.getPath());
            if (file.canRead()) {
                // Get Node and Capability
                String node = prefs.getString(Prefs.KEY_NODE, getString(R.string.test_node));
                String cap = prefs.getString(Prefs.KEY_ROOTCAP, getString(R.string.test_rootcap));
                // UPload file
                TahoeClient tahoe = new TahoeClient(node);
                try {
                    Log.i(TAG, "filename="+file.getName());
                    Log.i(TAG, "filepath="+file.getCanonicalPath());
                    tahoe.uploadFile(cap, file.getName(), file.getCanonicalPath());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                Log.i(TAG, "file is not readable");
            }
        } else {
            Log.i(TAG, "uri is null");
        }


    }

}
