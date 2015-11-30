package com.diversedistractions.vehiclelog;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class VehiclesListCursorAdapter extends CursorAdapter {
	private final LayoutInflater mInflater;
	
	public VehiclesListCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, false);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.vehicles_list_row, parent, false);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// Specify the columns we want to display in the list
		long year = cursor.getLong(cursor.getColumnIndex(DbVehicleTable.COL_VEHICLE_YEAR));
		String make = cursor.getString(cursor.getColumnIndex(DbVehicleTable.COL_VEHICLE_MAKE));
		String model = cursor.getString(cursor.getColumnIndex(DbVehicleTable.COL_VEHICLE_MODEL));
		
		// Constructs a new instance of the Calendar subclass appropriate for the default Locale
		Calendar cal = Calendar.getInstance();
		// Set the date/time to the value in COL_VEHICLE_YEAR (stored in Epoch time)
		cal.setTimeInMillis(year);
		
		// Reformat the date/time to be user-friendly
		String format = "yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String yearString = sdf.format(cal.getTime());
		
		// Assign the cursor data to fields in the listView 
		((TextView) view.findViewById(R.id.ytext)).setText(yearString);
		((TextView) view.findViewById(R.id.mktext)).setText(make);
		((TextView) view.findViewById(R.id.mdtext)).setText(model);
	}
}
