package com.diversedistractions.vehiclelog;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FuelEntryCreateEditActivity extends Activity {
    private TextView mDateDisplay;
    private Button mPickDate;
    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentDay;
    private long mEntryDateEpoch;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Button mEnter;
    private EditText mOdometerText;
    private EditText mGallonsText;
    private EditText mCostText;
    private CheckBox mPartialTank;
    private int mPartialTankTemp;
    private int mNewEntry;
    private Long mRowId;
    private TextView vehicleTitle;
    private String mCompVehLabel;
    private Long mVehRowId;
    private DbAdapter mDbAdapter;

    static final int DATE_DIALOG_ID = 0;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        mDbAdapter = new DbAdapter(this);
        mDbAdapter.open();
        
        // Check for a saved state and use that data for the rowId
        mRowId = (savedInstanceState == null) ? null:
        	(Long) savedInstanceState.getSerializable(DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID);
        
        if (mRowId == null) 
        {
            // Extract the selected entry's row id, vehicle name, & vehicle row id from the bundle
            // and assign them to the variables mRowId & mCompVehLabel.
        	Bundle extras = getIntent().getExtras();
        	mNewEntry = extras.getInt(FuelEntriesListActivity.NEW_ENTRY);
        	if (mNewEntry == 0)
        	{
            	mRowId = extras.getLong(DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID);
        	}else
        	{
        		mRowId = null;
        	}
        	mCompVehLabel = extras.getString(FuelEntriesListActivity.VEHICLE_NAME);
        	mVehRowId = extras.getLong(FuelEntriesListActivity.VEHICLE_ROW_ID);
        }

        // Set the view to display the data in to the layout entry_edit
		setContentView(R.layout.activity_fuel_entry_create_edit);

        // Set the title of the layout to the vehicle's name that the data is getting entered for.
        //TODO: Change this to "Fuel Entry" and put the vehicle name at top above scrolling list.
        setTitle(R.string.fuel_entry_create_edit_title);

        // Set home icon as clickable
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        // This associates local variables with variables in the layout entry_edit
        vehicleTitle = (TextView) findViewById(R.id.vehicleTitle);
        vehicleTitle.setText(mCompVehLabel);
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mOdometerText = (EditText) findViewById(R.id.currentOdometer);
        mGallonsText = (EditText) findViewById(R.id.gallonsFilled);
        mCostText = (EditText) findViewById(R.id.fuelCost);
        mPickDate = (Button) findViewById(R.id.dateDisplay);
        mEnter = (Button) findViewById(R.id.enter);
        mPartialTank = (CheckBox) findViewById(R.id.partialTank);
        mPartialTank.setChecked(false);

		// Constructs a new instance of the Calendar subclass appropriate for the default Locale
        final Calendar c = Calendar.getInstance();
        // Get the current date
        mCurrentYear = c.get(Calendar.YEAR);
        mCurrentMonth = c.get(Calendar.MONTH);
        mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
        
        // Convert the current date to epoch time for the entry
        setEntryDateEpoch(mCurrentYear, (mCurrentMonth+1), mCurrentDay);        
        // Update the displayed date
        updateDisplayedDate();
        
        // Populate the fields if editing or resuming
        populateFields();

        mPickDate.setOnClickListener(new View.OnClickListener()
		{
			
			public void onClick(View v)
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
        
		mEnter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TextUtils.isEmpty(mDateDisplay.getText().toString())) {
					makeToast();
				} else {
					setResult(RESULT_OK);
					finish();
				}
			}

		});
	}

    // Act when the Home on Up icon in the action bar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Returns to the list of fuel entries for the selected vehicle.
                Intent i = new Intent(this, FuelEntriesListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(DbVehicleTable.COL_VEHICLE_ROW_ID, mVehRowId);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setEntryDateEpoch(int yr, int m, int d) {
        /* 
         * Pull the year, month, & day out of the current date and convert to epoch time format as
          * an integer for the entry date.
         */
        SimpleDateFormat entryDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date edf = null;
		try {
			edf = entryDateFormatter.parse(Integer.toString(yr)+ "-" + Integer.toString(m)
					+ "-" + Integer.toString(d));
		} catch (ParseException rde) {
			// TODO Auto-generated catch block
			rde.printStackTrace();
		}
		mEntryDateEpoch = edf.getTime();    	
    }
    
    private void updateDisplayedDate()
    {
    	SimpleDateFormat formatter = new SimpleDateFormat("MMM' 'dd', 'yyyy");
    	mDateDisplay.setText(formatter.format(mEntryDateEpoch));
    }
    
    private void populateFields() 
    {
    	// If the rowID is not null, meaning an entry was selected to edit or one was open when
    	// focus was lost and needs to be restored, fill in the data with the existing entry.
    	if (mRowId != null) 
    	{
    		Cursor cursor = mDbAdapter.fetchFuelEntry(mRowId);
    		startManagingCursor(cursor);
    		mVehRowId = (cursor.getLong(cursor.getColumnIndexOrThrow
    				(DbFuelEntryTable.COL_VEH_ROW_ID)));
    		mEntryDateEpoch = cursor.getLong(cursor.getColumnIndexOrThrow
                    (DbFuelEntryTable.COL_FUEL_ENTRY_DATE));
    		updateDisplayedDate();
			mOdometerText.setText(cursor.getString(cursor.getColumnIndexOrThrow
                    (DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER)));
			mGallonsText.setText(cursor.getString(cursor.getColumnIndexOrThrow
                    (DbFuelEntryTable.COL_FUEL_ENTRY_GALLONS)));
            mCostText.setText(cursor.getString(cursor.getColumnIndexOrThrow
                    (DbFuelEntryTable.COL_FUEL_ENTRY_COST)));
    		mPartialTankTemp = (cursor.getInt(cursor.getColumnIndexOrThrow
                    (DbFuelEntryTable.COL_FUEL_ENTRY_PARTIAL)));
    		if (mPartialTankTemp == 0)
    		{
    			mPartialTank.setChecked(false);
    		}else if (mPartialTankTemp == 1)
    		{
    			mPartialTank.setChecked(true);
    		}

			// Always close the cursor
			cursor.close();
		}
	}

    // The callback received when the user sets the date in the date picker dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
    		new DatePickerDialog.OnDateSetListener()
			{
				
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth)
				{
					// Convert the date into epoch time for storage in database
					setEntryDateEpoch(year, (monthOfYear+1), dayOfMonth);
					// Convert to a user-friendly format for display
					updateDisplayedDate();
				}
			};
			
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id){
		case DATE_DIALOG_ID:
			// Constructs a new instance of the Calendar subclass appropriate for the default Locale
			Calendar cal = Calendar.getInstance();
			// Set the date to the value stored for the vehicle
			cal.setTimeInMillis(mEntryDateEpoch);
	        mYear = cal.get(Calendar.YEAR);
	        mMonth = cal.get(Calendar.MONTH);
	        mDay = cal.get(Calendar.DAY_OF_MONTH);
	        // Assign the value stored to the DatePickerDialog
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	}
    
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
    	outState.putSerializable(DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
    	Long vehicle = mVehRowId;
    	Long date = mEntryDateEpoch;
    	String odometer = mOdometerText.getText().toString();
    	String gallons = mGallonsText.getText().toString();
        String cost = mCostText.getText().toString();
		String entry_mpg = "";
    	int partial;
    	
    	// If the partial tank check box is check, set partial to 1, else to 0
    	if (mPartialTank.isChecked() == true)
    	{
    		partial = 1;
			// entry_mpg is assigned the string "Not a full tank"
			entry_mpg = "Not a full tank";
    	}else
    	{
    		partial = 0;
			//TODO: Need to have the previous entry's odometer or, if it was not a full tank,
			//      the gallons and odometer back to last full tank.
			double galTemp = 0;
			double currentOdometerTemp = 0;
			double previousOdometerTemp = 0;
			double mpgTemp = 0;
			try {
				galTemp = Double.parseDouble(gallons);
			} catch(NumberFormatException nfe) {
				System.out.println("Could not parse " + nfe);
			}
			try {
				currentOdometerTemp = Double.parseDouble(odometer);
			} catch(NumberFormatException nfe) {
				System.out.println("Could not parse " + nfe);
			}
			// Calculate what the fuel entries MPG is and assign to entry_mpg
			mpgTemp = (currentOdometerTemp - previousOdometerTemp) / galTemp;
			DecimalFormat df = new DecimalFormat("0.000");
			entry_mpg = df.format(mpgTemp);
		}
    	
		// Only save if date and odometer are available
		if (odometer.length() == 0 || date == 0) {
			//TODO: Create a text message stating that there are required fields left blank
			return;
		}

    	// If mRowId is null, it is a new entry that was being edited, so create a new entry
    	if (mRowId == null) 
    	{
    		long id = mDbAdapter.createFuelEntry(vehicle, date, odometer, gallons, cost, partial,
                    entry_mpg);
    		if (id > 0) 
    		{
    			mRowId = id;
    		}
    	} else 
    	// If mRowId was not null, this is an existing entry, so only need to update the entry
    	{
    		mDbAdapter.updateFuelEntry(mRowId, date, odometer, gallons, cost, partial, entry_mpg);
		}
	}

	private void makeToast() {
		Toast.makeText(FuelEntryCreateEditActivity.this, "Please pick a date",
				Toast.LENGTH_LONG).show();
	}
}
