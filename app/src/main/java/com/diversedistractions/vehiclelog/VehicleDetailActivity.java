package com.diversedistractions.vehiclelog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;


public class VehicleDetailActivity extends Activity {
    private TextView mMakeText;
    private TextView mModelText;
    private TextView mVehYearDisplay;
    private TextView mVinText;
    private TextView mPlateText;
    private TextView mRenDateDisplay;
    private TextView mVehNote;
    private Long mRowId;
    private long vehYearEpoch;
    private long renDateEpoch;
    private ImageButton mFuelButton;
    private ImageButton mServiceButton;
    private DbAdapter mDbAdapter;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_VIEW = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail);

        // Set home icon as clickable
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.vehicle_detail_title);

        mDbAdapter = new DbAdapter(this);
        mDbAdapter.open();


        mVehYearDisplay = (TextView) findViewById(R.id.year_display);
        mMakeText = (TextView) findViewById(R.id.make);
        mModelText = (TextView) findViewById(R.id.model);
        mVinText = (TextView) findViewById(R.id.vin);
        mPlateText = (TextView) findViewById(R.id.plate);
        mRenDateDisplay = (TextView) findViewById(R.id.ren_date_display);
        mFuelButton = (ImageButton) findViewById(R.id.imageButtonFuel);
        mServiceButton = (ImageButton) findViewById(R.id.imageButtonService);
        mVehNote = (TextView) findViewById(R.id.vehicleNote);

        mRowId = (savedInstanceState == null) ? null:
                (Long) savedInstanceState.getSerializable(
                        DbVehicleTable.COL_VEHICLE_ROW_ID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(
                    DbVehicleTable.COL_VEHICLE_ROW_ID) : null;
        }

        populateFields();

        mFuelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Start com.diversedistractions.vehiclelog.FuelEntriesListActivity
                launchFuelEntriesListActivity(mRowId);
            }
        });

        mServiceButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                // Start ServiceEntriesListActivity
                launchServiceEntriesListActivity(mRowId);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vehicle_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_settings:
                return true;
            case R.id.edit_vehicle:
                // Launches VehicleEdit activity for the selected vehicle
                editVehicle(mRowId);
                return true;
            case R.id.delete_vehicle:
                // Display an alert dialog asking for confirmation before deleting.
                displayVehicleDeleteAlertDialog(mRowId);
                // Returns to the home screen of the list of vehicles.
                Intent i = new Intent(this, VehiclesListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return true;
            //TODO: Create an option to view mileage info/chart about the selected vehicle
            //TODO: This should probably entail a new activity
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchFuelEntriesListActivity(long id)
    {
        // TODO: This needs to give visual feedback when pressed
        Intent i = new Intent(this, FuelEntriesListActivity.class);
        i.putExtra(DbVehicleTable.COL_VEHICLE_ROW_ID, id);
        startActivityForResult(i,ACTIVITY_VIEW);
    }

    private void launchServiceEntriesListActivity(long id)
    {
        // TODO: This needs to give visual feedback when pressed
        Intent i = new Intent(this, ServiceEntriesListActivity.class);
        i.putExtra(DbVehicleTable.COL_VEHICLE_ROW_ID, id);
        startActivityForResult(i,ACTIVITY_VIEW);
    }

    private void editVehicle(long id)
    {
        Intent i = new Intent(this, VehicleCreateEditActivity.class);
        i.putExtra(DbVehicleTable.COL_VEHICLE_ROW_ID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
        //TODO: Does this need to restart com.diversedistractions.vehiclelog.VehicleDetailActivity
        // after returning?
    }

    public void deleteVehicle(long id) {
        //TODO: This should also delete all associated service entries as well
        // Deletes all entries associated with this vehicle id
        mDbAdapter.deleteAllVehicleFuelEntries(id);
        // Deletes the selected vehicle
        mDbAdapter.deleteVehicle(id);
        //TODO: This should probably restart the com.diversedistractions.vehiclelog.VehiclesListActivity here
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
    	/* If the rowID is not null, meaning an entry was selected to view or
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
            // Allows the text view to scroll if bigger than the desired height
            mVehNote.setMovementMethod(new ScrollingMovementMethod());
            cursor.moveToFirst();

            // Always close the cursor
            cursor.close();
        }
    }

    private void displayVehicleDeleteAlertDialog(Long ItemId) {
        // Delete the vehicle and associated entries if confirmed,
        // else back out and refresh the list.

        // Set the context and variables for the AlertDialog
        Context context = VehicleDetailActivity.this;
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
                        populateFields();
                    }
                }
        );

        ad.show();
    }
}
