package com.diversedistractions.vehiclelog;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
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

/*
 * VehicleLogVehicleListActivity displays the existing vehicles
 * in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete or edit existing vehicles via a long press 
 * on the item and selecting the appropriate action in the
 * Content ActionBar
 */

public class VehiclesListActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_VIEW = 2;
	private DbAdapter mDbAdapter;
    private long ItemId;
    //TODO: Remove the long click to edit but maybe keep for delete
    protected Object mActionMode;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicles_list);
        mDbAdapter = new DbAdapter(this);
        mDbAdapter.open();
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
				mActionMode = VehiclesListActivity.this.startActionMode(mActionModeCallback);
				view.setSelected(true);
				return true;
			}
		});
    }

    // Create the ActionBar menu based on the XML definition
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.vehicles_list_menu, menu);
    	return true;
    }

    // Act on which item is selected in the above menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.add_vehicle:
    		createVehicle();
    		return true;
    	case R.id.import_export:
    		//TODO: make this start an intent where one can
    		//      choose import or export.
    		export();
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }

    // Content ActionBar
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		// Called each time the action mode is shown. Always called after
    	// onCreateActionMode, but may be called multiple times if the
    	// mode is invalidated.
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
    		inflater.inflate(R.menu.vehicle_list_content_menu, menu);
			return true;
		}
		
		// Called when the user selects a content menu item
    	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    		switch (item.getItemId()){
    		case R.id.edit_vehicle:
    			// Launches VehicleEdit activity for the selected vehicle
    			editVehicle(ItemId);
    			mode.finish();		// Closes the content action bar
    			return true;
    		case R.id.delete_vehicle:
    			// Display an alert dialog asking for confirmation.
    			displayVehicleDeleteAlertDialog(ItemId);
    			mode.finish();		// Closes the content action bar
    			return true;
            //TODO: Create an option for viewing without opening to edit
    		//TODO: Create an option to view mileage info/chart about the selected vehicle
    			//TODO: This should probably entail a new activity
    		default:
    			return false;
    		}
		}
	};
    
    private void createVehicle() 
    {
        Intent i = new Intent(this, VehicleCreateEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void editVehicle(long id)
    {
    	Intent i = new Intent(this, VehicleCreateEditActivity.class);
        i.putExtra(DbVehicleTable.COL_VEHICLE_ROW_ID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    public void deleteVehicle(long id) {
		//TODO: This should also delete all associated service entries as well
    	// Deletes all entries associated with this vehicle id
    	mDbAdapter.deleteAllVehicleFuelEntries(id);
		// Deletes the selected vehicle
		mDbAdapter.deleteVehicle(id);
		fillData();
    }
    
    private void export()
    {
    	Intent i = new Intent(this, DbBackupActivity.class);
    	//TODO: may want to make this return a result to be used by a
    	//      everything is done statement.
    	startActivity(i);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
    	super.onListItemClick(l, v, position, id);
        Intent i;
        i = new Intent(this, VehicleDetailActivity.class);
        i.putExtra(DbVehicleTable.COL_VEHICLE_ROW_ID, id);
        startActivityForResult(i, ACTIVITY_VIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
    {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    private void fillData() 
    {
    	// Get all of the vehicles from the database and create the item list
    	Cursor vehicleCursor = mDbAdapter.fetchAllVehicles();
    	startManagingCursor(vehicleCursor);
   	
        // Use the custom cursor adapter to display the data
        setListAdapter(new VehiclesListCursorAdapter(this, vehicleCursor));
    }

    private void displayVehicleDeleteAlertDialog(Long ItemId) {
		// Delete the vehicle and associated entries if confirmed,
		// else back out and refresh the list.

    	// Set the context and variables for the AlertDialog
    	Context context = VehiclesListActivity.this;
        String title = "Are you sure?";
        String message = "Delete selected vehicles & all associated data?";
        String PosButtonString = "Delete";
        String NegButtonString = "Cancel";
        final Long id = ItemId;

        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);

        ad.setPositiveButton(PosButtonString,
        		new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            	deleteVehicle(id);
            }
          }
        );

        ad.setNegativeButton(NegButtonString,
        		new DialogInterface.OnClickListener(){
        	public void onClick(DialogInterface dialog, int arg1) {
        		// Do nothing but refresh the screen
        		fillData();
            }
          }
        );

        ad.show();
      }
      
}
