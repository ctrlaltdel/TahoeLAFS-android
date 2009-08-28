package org.allmydata.tahoelafs;

import java.io.IOException;

import org.allmydata.tahoelafs.TahoeDirectory.types;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class Browser extends ListActivity {
    private static final String TAG = "DirectoryList";
    private String rootcap = "URI:DIR2:djrdkfawoqihigoett4g6auz6a:jx5mplfpwexnoqff7y5e4zjus4lidm76dcuarpct7cckorh2dpgq";
    private String node    = "http://testgrid.allmydata.org:3567";
    
    private TahoeClient tahoe;
    private TahoeDirectory dir;
    
    // Menu item ids
    public static final int MENU_UPLOAD   = Menu.FIRST;
    public static final int MENU_HOME     = Menu.FIRST + 1;
    public static final int MENU_REFRESH  = Menu.FIRST + 2;
    public static final int MENU_SETTINGS = Menu.FIRST + 3;
    public static final int MENU_ABOUT    = Menu.FIRST + 4;
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_UPLOAD,   0, "Upload");
        menu.add(0, MENU_HOME,     0, "Home");
        menu.add(0, MENU_REFRESH,  0, "Refresh");
        menu.add(0, MENU_SETTINGS, 0, "Settings");
        menu.add(0, MENU_ABOUT,    0, "About");
        return true;
    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
    	
        switch (item.getItemId()) {
        	case MENU_UPLOAD:
        		intent = new Intent("org.openintents.action.PICK_FILE");
        		startActivityForResult(intent, 1);
        		return true;
        	case MENU_HOME:
        		intent = new Intent(android.content.Intent.ACTION_VIEW);
        		intent.setData(Uri.fromParts("lafs", "", rootcap));
        		return true;
        		
        	case MENU_REFRESH:
        		return true;
        	
        	case MENU_SETTINGS:
        		return true;
        		
        	case MENU_ABOUT:
        		intent = new Intent("org.openintents.action.SHOW_ABOUT_DIALOG");
        		startActivityForResult(intent, 0);
        		return true;
        }
        
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        // Inform the list we provide context menus for items
        getListView().setOnCreateContextMenuListener(this);
        
        // Load Tahoe client
        tahoe  = new TahoeClient(node);
        
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(Uri.fromParts("lafs", "", rootcap));
        }
             
        String cap = getIntent().getData().getEncodedFragment();
        loadDirectory(cap);
    }
    
    private void loadDirectory(String cap) {        
        try {
        	dir = tahoe.getDirectory(cap);
        	assert dir != null;
        } catch (Exception e) {
        	Toast.makeText(this, "Cannot access the directory referenced by " + cap, Toast.LENGTH_LONG).show();
        	return;
        }
        
        this.setListAdapter(new ArrayAdapter<String>(
        	this, android.R.layout.simple_list_item_1, dir.toStringArray()));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

    	Log.d(TAG, "Item number at position " + position + " with id " + id + " was selected");

    	try {
    		Log.i(TAG, "Getting type");
    		types type = dir.getType(position);
    		Log.i(TAG, "Type: " + type);
    		
    		switch (type) {
    		case dirnode:
    			Log.i(TAG, "A directory was selected");
    			loadDirectory(dir.getReadCap(position));
    			return;

    		case filenode:
    			Log.i(TAG, "A file was selected");
    			openFile(dir.getReadCap(position));
    		}
    	} catch (Exception e) {
    		Log.d(TAG, e.getLocalizedMessage());
    		Toast.makeText(this, "Cannot access this element", Toast.LENGTH_LONG).show();
    	}
    }
    
    private void openFile(String cap) {
    	Log.i(TAG, "Getting file " + cap);
    	String dst = "/sdcard/test.png";

    	try {
    		tahoe.downloadFile(cap, dst);

    		Uri data = Uri.parse("file:///sdcard/test.jpg");
    		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
    		intent.setData(data);
    		intent.setType("image/png");
    		startActivity(intent);
    	} catch (IOException e) {
    		Log.e(TAG, "Download failed");
    	}
    }
}
