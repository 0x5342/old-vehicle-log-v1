package com.diversedistractions.vehiclelog;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/*
 * ServiceEntriesListActivity displays the existing selected vehicle's service entries in a list
 * 
 * You can create new ones via the ActionBar entry "Add". You can delete or edit existing entries
  * via a long press on the item and selecting the appropriate action in the Content ActionBar.
 */

public class ServiceEntriesListActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
    private DbAdapter mDbAdapter;
	public long mVehRowId;
	private long mVehYearEpoch;
	private String mVehMake;
	private String mVehModel;
	private String mVehYearDisplay;
	private String mCompVehLabel;
    private TextView vehicleTitle;
    static String NEW_ENTRY = "1";
    static String VEHICLE_NAME = "vehicle_name";
    static String VEHICLE_ROW_ID = "vehicle_row_id";
    private long ItemId;
    protected Object mActionMode;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbAdapter = new DbAdapter(this);
        mDbAdapter.open();
        
        // Extract the selected vehicle's row id from the bundle and assign it to the variable
        // mVehRowId for use in queries specific to the selected vehicle
        Bundle extras = getIntent().getExtras();
        mVehRowId = extras != null ?
        		extras.getLong(DbVehicleTable.COL_VEHICLE_ROW_ID): null;
        
        // Retrieve the year, make, and model of the selected vehicle and assign it to the variable
        // mCompVehLabel in order to display in the title of the entry list
        Cursor vehicle = mDbAdapter.fetchVehicle(mVehRowId);
        startManagingCursor(vehicle);
        mVehYearEpoch = (vehicle.getLong(vehicle.getColumnIndexOrThrow(
        		DbVehicleTable.COL_VEHICLE_YEAR)));
        mVehMake = (vehicle.getString(
        		vehicle.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_MAKE)));
        mVehModel = (vehicle.getString(
        		vehicle.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_MODEL)));
        // Convert the vehicle year from epoch time to a user-friendly format
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
    	mVehYearDisplay = (formatter.format(mVehYearEpoch));
    	// Assign the year, make, and model to a variable as the whole vehicle label
        mCompVehLabel = mVehYearDisplay + " " + mVehMake + " " + mVehModel;

        setContentView(R.layout.service_entries_list);
        vehicleTitle = (TextView)findViewById(R.id.vehicle);
        vehicleTitle.setText(mCompVehLabel);
        setTitle(R.string.service_entries_title);
        
        // Set home icon as clickable
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        fillData();

        // Define the contextual action mode
		ListView lv = getListView();
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			// Called when the user long-clicks on an item in the list
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mActionMode != null) {
					return false;
				}
				ItemId = id;
				// Start the content action bar using the ActionMode.Callback defined above
				mActionMode = ServiceEntriesListActivity.this.startActionMode(mActionModeCallback);
				view.setSelected(true);
				return true;
			}
		});
    }

    // Create the ActionBar menu based on the XML definition
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.service_entries_list_menu, menu);
    	return true;
    }

    // Act on which item is selected in the above menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.add_entry:
    		createEntry();
    		return true;
    	case android.R.id.home:
            // Returns to the screen displaying the currently selected vehicle.
            Intent i = new Intent(this, VehicleDetailActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(DbVehicleTable.COL_VEHICLE_ROW_ID, mVehRowId);
            startActivity(i);
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }

    // Content ActionBar
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		// Called each time the action mode is shown. Always called after onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
    	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// Return false if nothing is done
    		return false;
		}
		
		// Called when the user exits the action mode
    	public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}
		
		// Called when the action mode is created by calling startActionMode()
    	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    		// Inflate a menu resource providing content menu items
    		MenuInflater inflater = mode.getMenuInflater();
    		inflater.inflate(R.menu.service_entries_list_content_menu, menu);
			return true;
		}
		
		// Called when the user selects a content menu item
    	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    		switch (item.getItemId()){
    		case R.id.edit_entry:
    			// Launches EntryEdit activity for the selected vehicle
    			editEntry(ItemId);
    			mode.finish();		// Closes the content action bar
    			return true;
    		case R.id.delete_entry:
    			// Deletes the selected data
    			mDbAdapter.deleteServiceEntry(ItemId);
    			fillData();
    			mode.finish();		// Closes the content action bar
    			return true;
            //TODO: Create an option for viewing without opening to edit
    		default:
    			return false;
    		}
		}
	};
    
   // Create a new service entry for the current vehicle
   private void createEntry() {
	   Intent i = new Intent(this, ServiceEntryCreateEditActivity.class);
	   	  i.putExtra(NEW_ENTRY, 1);
       	  i.putExtra(VEHICLE_NAME, mCompVehLabel);
       	  i.putExtra(VEHICLE_ROW_ID, mVehRowId);
       	  startActivityForResult(i, ACTIVITY_CREATE);
    }
    
   // Edit the selected service entry for the current vehicle
   private void editEntry(long id) {
	   Intent i = new Intent(this, ServiceEntryCreateEditActivity.class);
	   	  i.putExtra(NEW_ENTRY, 0);
          i.putExtra(DbServiceEntryTable.COL_SERVICE_ENTRY_ROW_ID, id);
          i.putExtra(VEHICLE_NAME, mCompVehLabel);
          i.putExtra(VEHICLE_ROW_ID, mVehRowId);
          startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void fillData() {
    	// Get all of the data for the selected vehicle from the database and create the item list
    	Cursor dataCursor = mDbAdapter.fetchRelevantServiceEntries(mVehRowId);
    	startManagingCursor(dataCursor);
    	
        // Use the custom cursor adapter to display the data
        setListAdapter(new ServiceEntriesListCursorAdapter(this, dataCursor));
    }
    
	// Called with the result of the other activity requestCode was the original request code send
	// to the activity resultCode is the returned code, 0 is everything is ok intent can be used to
	// get data
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
}
