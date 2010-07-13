package org.allmydata.tahoelafs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Send extends Activity {

    private final static String TAG = "Send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "Now send the stuff to the grid");
    }

}
