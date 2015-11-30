package com.diversedistractions.vehiclelog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

/*
 * VehicleLogDbBackup will read each table in the database one at a time
 * and create a CSV file of each one.
 */

public class DbBackupActivity extends Activity {

    // String to use in the log while debugging
	private static final String TAG = "VehicleLogDbBackup"; 

	private DbAdapter mDbAdapter;

    // Used to insert line separator onto the end of row's comma
    // separated string when backing up the database to a text file
	String lineSep = System.getProperty("line.separator");
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of the com.diversedistractions.vehiclelog.DbAdapter class
        mDbAdapter = new DbAdapter(this);
        // Open the database
        mDbAdapter.open();
        
	    /**
	     * Back up the VEHICLE table to the CSV file 'vehicles.txt'
	     */
        // Get all of the vehicles from the database into a result cursor
        Cursor vehicleCursor = mDbAdapter.fetchAllVehiclesOldDb();
        
		// Find and assign the indexes for all vehicles columns that will be backed up
	    int COL_VEHICLE_ROW_ID_INDEX = 
	    		vehicleCursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_ROW_ID);
		int COL_VEHICLE_MAKE_INDEX = 
				vehicleCursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_MAKE);
		int COL_VEHICLE_MODEL_INDEX =
				vehicleCursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_MODEL);
		int COL_VEHICLE_YEAR_INDEX =
				vehicleCursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_YEAR);
		int COL_VEHICLE_VIN_INDEX =
				vehicleCursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_VIN);
		int COL_VEHICLE_LP_INDEX =
				vehicleCursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_LP);
		int COL_VEHICLE_REN_DATE_MONTH_INDEX =
				vehicleCursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_REN_DATE);
	
	    try {
	    	// Create and open a text file named vehicles.txt in MODE_WORLD_READABLE(mode 1)
	    	// may need to use MODE_PRIVATE(mode 0) when doing upgrades.
			FileOutputStream fOut = openFileOutput("vehicles.txt", MODE_WORLD_READABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
		
		    // Step through the cursor's rows and write to the vehicles text file.
		    while (vehicleCursor.moveToNext()) {
		    	String rowString = 
		    			vehicleCursor.getString(COL_VEHICLE_ROW_ID_INDEX) + "," +
		    			vehicleCursor.getString(COL_VEHICLE_MAKE_INDEX) + "," +
					    vehicleCursor.getString(COL_VEHICLE_MODEL_INDEX) + "," +
					    vehicleCursor.getString(COL_VEHICLE_YEAR_INDEX) + "," +
					    vehicleCursor.getString(COL_VEHICLE_VIN_INDEX) + "," +
					    vehicleCursor.getString(COL_VEHICLE_LP_INDEX) + "," +
					    vehicleCursor.getString(COL_VEHICLE_REN_DATE_MONTH_INDEX) +
					    lineSep;
		
		    	// Write rowString to the vehicles text file
		    	osw.write(rowString);

		    	//TODO: Add a visual indication of this ongoing process.
		    }
		    
		    // Flush the write buffer and close the file
		    osw.flush();
		    osw.close();
	    } catch (IOException ioe)
	    {
	    	Log.w(TAG, "IO Exception while trying to back up vehicles");
	    	ioe.printStackTrace();
	    }
	    
	    // Closes the cursor now that we're done with it
	    vehicleCursor.close();
        
	    /**
	     * Back up the ENTRIES table to the CSV file 'entries.txt'
	     */
        // Get all of the entries from the database into a result cursor
        Cursor entriesCursor = mDbAdapter.fetchAllEntriesOldDb();
        
	    // Find and assign the indexes for all entries columns
	    int COL_ENTRY_ROW_ID_INDEX = 
	    		entriesCursor.getColumnIndexOrThrow(DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID);
		int COL_VEH_ROW_ID_INDEX = 
				entriesCursor.getColumnIndexOrThrow(DbFuelEntryTable.COL_VEH_ROW_ID);
		int COL_ENTRY_DATE_INDEX =
				entriesCursor.getColumnIndexOrThrow(DbFuelEntryTable.COL_FUEL_ENTRY_DATE);
		int COL_ENTRY_ODOMETER_INDEX =
				entriesCursor.getColumnIndexOrThrow(DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER);
		int COL_ENTRY_GALLONS_INDEX =
				entriesCursor.getColumnIndexOrThrow(DbFuelEntryTable.COL_FUEL_ENTRY_GALLONS);
		int COL_ENTRY_PARTIAL_INDEX =
				entriesCursor.getColumnIndexOrThrow(DbFuelEntryTable.COL_FUEL_ENTRY_PARTIAL);

	    try {
	    	// Create and open a text file named entries.txt in MODE_WORLD_READABLE(mode 1)
	    	// may need to use MODE_PRIVATE(mode 0) when doing upgrades.
			FileOutputStream fOut = openFileOutput("entries.txt", 0);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
		
		    // Step through the cursor's rows and write to the entries text file.
		    while (entriesCursor.moveToNext()) {
		    	String rowString = 
		    			entriesCursor.getString(COL_ENTRY_ROW_ID_INDEX) + "," +
    					entriesCursor.getString(COL_VEH_ROW_ID_INDEX) + "," +
    					entriesCursor.getString(COL_ENTRY_DATE_INDEX) + "," +
    					entriesCursor.getString(COL_ENTRY_ODOMETER_INDEX) + "," +
    					entriesCursor.getString(COL_ENTRY_GALLONS_INDEX) + "," +
    					entriesCursor.getString(COL_ENTRY_PARTIAL_INDEX) +
					    lineSep;
		
		    	// Write rowString to the entries text file
		    	osw.write(rowString);
		    	
		    	//TODO: Add a visual indication of this ongoing process.
		    }
		    
		    // Flush the write buffer and close the file
		    osw.flush();
		    osw.close();
	    } catch (IOException ioe)
	    {
	    	Log.w(TAG, "IO Exception while trying to back up entries");
	    	ioe.printStackTrace();
	    }
	    
	    // Closes the cursor now that we're done with it
	    entriesCursor.close();
	    
	    //TODO: If and when notes are incorporated, a section to backup the 
	    //      notes table will have to be added here.
	    
	    // Close the database
   	 	mDbAdapter.close();
    }
}
