package com.diversedistractions.vehiclelog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/*
 * VehicleLogDbRestore will read each table's backed up CSV file one at a time
 * and restore the data into each of the newly upgraded database's tables.
 */

public class DbRestoreActivity extends Activity {

    Long vRowId;
    String make;
    String model;
    Long year;
    String vin;
    String lp;
    Long rd;
    Long eRowId;
    Long date;
    String odometer;
    String gallons;
    int partialTank;

    // String to use in the log while debugging
	private static final String TAG = "VehicleLogDbRestore"; 

	private DbAdapter mDbAdapter;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of the com.diversedistractions.vehiclelog.DbAdapter class
        mDbAdapter = new DbAdapter(this);
        // Open the database
        mDbAdapter.open();
        
	    /**
	     * Read the CSV file 'vehicles.txt' and restore the data to
	     * the newly upgraded database's VEHICLE table.
	     */
        // Read the data for the VEHICLE table

        // Enter a value of zero for ytd_mpg and calculate it later.
     	String ytd_mpg = "0";
       
    	try {
    		// Open the vehicles.txt file to read
    		FileInputStream fIn = openFileInput("vehicles.txt");
    		BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
    	    try {
    	        String line;
    	        // Step through the CSV file line by line. Each section between
    	        // commas is assigned to a variable which will be written back
    	        // into the new database table one row at a time.
    	        while ((line = reader.readLine()) != null) {
    	             String[] RowData = line.split(",");
    	             vRowId = Long.parseLong(RowData[0]);
    	             make = RowData[1];
    	             model = RowData[2];
    	             year = Long.parseLong(RowData[3]);
    	             vin = RowData[4];
    	             lp = RowData[5];
    	             rd = Long.parseLong(RowData[6]);

    	     		 // Insert the read data into a row in the upgraded database's
    	             // vehicle table.
    	             mDbAdapter.writeUpgradeVehicle(vRowId, make, model, year, vin, lp, 
    	    				rd, ytd_mpg);

    	             //TODO: Add a visual indication of this ongoing process.
    	        }
    	    }
    	    catch (IOException ex) {
        		Log.w(TAG, "IO Exception while trying to read vehicles.txt");
        		ex.printStackTrace();
    	    }
    	    finally {
    	        try {
    	            // Close the vehicles.txt file
    	        	fIn.close();
    	        }
    	        catch (IOException e) {
    	    		Log.w(TAG, "IO Exception while trying to close vehicles.txt");
    	    		e.printStackTrace();
    	        }
    	    }

    	}
    	catch (IOException ioe) {
    		Log.w(TAG, "IO Exception while trying to open vehicles.txt");
    		ioe.printStackTrace();
    	}
    	
	    /**
	     * Read the CSV file 'entries.txt' and restore the data to
	     * the newly upgraded database's ENTRIES table.
	     */
        // Read the data for the ENTRIES table
    	
    	// Enter a value of zero for entry_mpg and calculate it later.
      	 String entry_mpg = "0";
        
    	try {
    		// Open the entries.txt file to read
    		FileInputStream fIn = openFileInput("entries.txt");
    		BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
    	    try {
    	        String line;
    	        // Step through the CSV file line by line. Each section between
    	        // commas is assigned to a variable which will be written back
    	        // into the new database table one row at a time.
    	        while ((line = reader.readLine()) != null) {
    	             String[] RowData = line.split(",");
    	             eRowId = Long.parseLong(RowData[0]);
    	             vRowId = Long.parseLong(RowData[1]);
    	             date = Long.parseLong(RowData[2]);
    	             odometer = RowData[3];
    	             gallons = RowData[4];
    	             partialTank = Integer.parseInt(RowData[5]);

    	             // Insert the read data into a row in the upgraded database's
    	             // entries table.
    	             mDbAdapter.writeUpgradeFuelEntry(eRowId, vRowId, date, odometer,
                             gallons, partialTank, entry_mpg);

    	             //TODO: Add a visual indication of this ongoing process.
    	        }
    	    }
    	    catch (IOException ex) {
        		Log.w(TAG, "IO Exception while trying to read entries.txt");
        		ex.printStackTrace();
    	    }
    	    finally {
    	        try {
    	            // Close the entries.txt file
    	        	fIn.close();
    	        }
    	        catch (IOException e) {
    	    		Log.w(TAG, "IO Exception while trying to close entries.txt");
    	    		e.printStackTrace();
    	        }
    	    }

    	}
    	catch (IOException ioe) {
    		Log.w(TAG, "IO Exception while trying to open entries.txt");
    		ioe.printStackTrace();
    	}

	    //TODO: If and when notes are incorporated, a section to restore the 
	    //      notes table will have to be added here.
	    
    	// Close the database
   	 	mDbAdapter.close();
    }
}