package com.diversedistractions.vehiclelog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class VehicleCreateEditActivity extends Activity{
    private EditText mMakeText;
    private EditText mModelText;
    private TextView mVehYearDisplay;
    private Button mPickVehYear;
    private EditText mVinText;
    private EditText mPlateText;
    private TextView mRenDateDisplay;
    private Button mPickRenDate;
    private EditText mVehNote;
    private Button mConfirmButton;
    private Long mRowId;
    private int mCurrentYear;
    private int mCurrentMonth;
    private int mVehYear;
    private int mVehMonth;
    private int mVehDay;
    private int mRenYear;
    private int mRenMonth;
    private int mRenDay;
    private long vehYearEpoch;
    private long renDateEpoch;
    private DbAdapter mDbAdapter;
    
    static final int VEH_YEAR_DATE_DIALOG_ID = 0;
    static final int REN_DATE_DIALOG_ID = 1;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        mDbAdapter = new DbAdapter(this);
        mDbAdapter.open();
        
		setContentView(R.layout.activity_vehicle_create_edit);
        setTitle(R.string.menu_edit_vehicle);

        // Set home icon as clickable
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        //TODO: Need to make this navigate up one level to
        //      the vehicle list which is also the top Activity.

        mVehYearDisplay = (TextView) findViewById(R.id.year_display);
        mPickVehYear = (Button) findViewById(R.id.year_display);
        mMakeText = (EditText) findViewById(R.id.make);
        mModelText = (EditText) findViewById(R.id.model);
        mVinText = (EditText) findViewById(R.id.vin);
        mPlateText = (EditText) findViewById(R.id.plate);
        mRenDateDisplay = (TextView) findViewById(R.id.ren_date_display);
        mPickRenDate = (Button) findViewById(R.id.ren_date_display);
        mVehNote = (EditText) findViewById(R.id.vehicleNote);
        mConfirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null:
        	(Long) savedInstanceState.getSerializable(
        			DbVehicleTable.COL_VEHICLE_ROW_ID);
        if (mRowId == null) {
        	Bundle extras = getIntent().getExtras();
        	mRowId = extras != null ? extras.getLong(
        			DbVehicleTable.COL_VEHICLE_ROW_ID) : null;
        }

		// Constructs a new instance of the Calendar subclass appropriate for the default Locale
        final Calendar cal = Calendar.getInstance();
        // Get the current date
        mCurrentYear = cal.get(Calendar.YEAR);
        mCurrentMonth = cal.get(Calendar.MONTH);

        // Convert the current year to epoch time for the vehicle year
        setVehYearEpoch(mCurrentYear);        
        // Convert the current year and month to epoch time for the renewal date
        setRenDateEpoch(mCurrentYear, mCurrentMonth);
        
        // Update the displayed vehicle model year
        updateDisplayedVehicleYear();
        // Update the displayed license plate renewal date
        updateDisplayedRenewalDate();
        
        populateFields();
        
        mPickVehYear.setOnClickListener(new View.OnClickListener()
		{
			
			public void onClick(View v)
			{
				showDialog(VEH_YEAR_DATE_DIALOG_ID);
			}
		});
        
        mPickRenDate.setOnClickListener(new View.OnClickListener()
		{
			
			public void onClick(View v)
			{
				showDialog(REN_DATE_DIALOG_ID);
			}
		});
        
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    private void setVehYearEpoch(int yr) {
        /* 
         * Pull just the year out of the date and convert to epoch
         * time format as an integer for the vehicle model year
         */
        SimpleDateFormat vehYearFormatter = new SimpleDateFormat("yyyy");
        Date vy = null;
		try {
			vy = vehYearFormatter.parse(Integer.toString(yr));
		} catch (ParseException vye) {
			// TODO Auto-generated catch block
			vye.printStackTrace();
		}
		vehYearEpoch = vy.getTime();
    }
    
    private void setRenDateEpoch(int yr, int m) {
        /* 
         * Pull the year and month out of the current date and convert to epoch
         * time format as an integer for the license plate renewal date 
         */
        SimpleDateFormat renDateFormatter = new SimpleDateFormat("yyyy-MM");
        Date rd = null;
		try {
			rd = renDateFormatter.parse(Integer.toString(yr)+ "-" + Integer.toString(m));
		} catch (ParseException rde) {
			// TODO Auto-generated catch block
			rde.printStackTrace();
		}
		renDateEpoch = rd.getTime();    	
    }
    
    private void updateDisplayedVehicleYear() {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
    	mVehYearDisplay.setText(formatter.format(vehYearEpoch));
    }
    
    private void updateDisplayedRenewalDate() {
    	SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy");
    	mRenDateDisplay.setText(formatter.format(renDateEpoch));
    }
    
    private void populateFields() {
    	/* If the rowID is not null, meaning an entry was selected to edit or
    	 * one was open when focus was lost and needs to be restored, fill in
    	 * the data with the existing entry */
    	if (mRowId != null) {
    		Cursor cursor = mDbAdapter.fetchVehicle(mRowId);
    		startManagingCursor(cursor);
    		vehYearEpoch = cursor.getLong(cursor.getColumnIndexOrThrow(
    				DbVehicleTable.COL_VEHICLE_YEAR));
    		updateDisplayedVehicleYear();
    		mMakeText.setText(cursor.getString(
    				cursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_MAKE)));
    		mModelText.setText(cursor.getString(
    				cursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_MODEL)));
    		mVinText.setText(cursor.getString(
    				cursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_VIN)));
    		mPlateText.setText(cursor.getString(
    				cursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_LP)));
    		renDateEpoch = cursor.getLong(cursor.getColumnIndexOrThrow(
    				DbVehicleTable.COL_VEHICLE_REN_DATE));
    		updateDisplayedRenewalDate();
            mVehNote.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DbVehicleTable.COL_VEHICLE_NOTE)));
			cursor.moveToFirst();

			cursor.close();
		}
	}

    // The callback received when the user sets the date in the date picker dialog
    private DatePickerDialog.OnDateSetListener mVehYearSetListener =
    		new DatePickerDialog.OnDateSetListener(){
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth){
					// Convert the year into epoch time for storage in database
					setVehYearEpoch(year);
					// Convert to a user-friendly format for display
					updateDisplayedVehicleYear();
				}
			};
			
    // The callback received when the user sets the date in the date picker dialog
    private DatePickerDialog.OnDateSetListener mRenDateSetListener =
    		new DatePickerDialog.OnDateSetListener(){
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth){
			        // Convert the year and month into epoch time for storage in database
					setRenDateEpoch(year, (monthOfYear+1));
					// Convert to a user-friendly format for display
					updateDisplayedRenewalDate();
				}
			};
					
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id){
		case VEH_YEAR_DATE_DIALOG_ID:
			// Constructs a new instance of the Calendar subclass appropriate for the default Locale
			Calendar vc = Calendar.getInstance();
			// Set the date to the value stored for the vehicle
			vc.setTimeInMillis(vehYearEpoch);
	        mVehYear = vc.get(Calendar.YEAR);
	        mVehMonth = vc.get(Calendar.MONTH);
	        mVehDay = vc.get(Calendar.DAY_OF_MONTH);
	        // Assign the value stored to the DatePickerDialog
			return new DatePickerDialog(this, mVehYearSetListener, mVehYear, mVehMonth, mVehDay);
		case REN_DATE_DIALOG_ID:
			// Constructs a new instance of the Calendar subclass appropriate for the default Locale
			Calendar rdc = Calendar.getInstance();
			// Set the date to the value stored for the vehicle
			rdc.setTimeInMillis(renDateEpoch);
	        mRenYear = rdc.get(Calendar.YEAR);
	        mRenMonth = rdc.get(Calendar.MONTH);
	        mRenDay = rdc.get(Calendar.DAY_OF_MONTH);
	        // Assign the value stored to the DatePickerDialog
			return new DatePickerDialog(this, mRenDateSetListener, mRenYear, mRenMonth, mRenDay);
		}
		return null;
	}
    
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(DbVehicleTable.COL_VEHICLE_ROW_ID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
    	String make = mMakeText.getText().toString();
    	String model = mModelText.getText().toString();
		Long year = vehYearEpoch;
    	String vin = mVinText.getText().toString();
    	String plate = mPlateText.getText().toString();
    	String veh_image = "vi_car";
    	Long ren_date = renDateEpoch;
        String note = mVehNote.getText().toString();

		// Only save if make and model are available
		if (make.length() == 0 && model.length() == 0) {
			return;
		}
		//TODO: Create a text message stating that there are required fields left blank
		//      if the above is false.

    	if (mRowId == null) {
    		long id = mDbAdapter.createVehicle(make, model, year, vin, plate, veh_image, ren_date,
                    note);
    		if (id > 0) {
    			mRowId = id;
    		}
    	} else {
    		mDbAdapter.updateVehicle(mRowId, make, model, year, vin, plate, veh_image, ren_date,
                    note);
    	}
    }
}
