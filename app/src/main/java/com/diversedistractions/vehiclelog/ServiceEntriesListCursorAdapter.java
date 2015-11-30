package com.diversedistractions.vehiclelog;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServiceEntriesListCursorAdapter extends CursorAdapter{
	private final LayoutInflater mInflater;

	public ServiceEntriesListCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, false);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.service_entries_list_row, parent, false);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// Specify the columns we want to display in the list
		long date = cursor.getLong(cursor.getColumnIndex
                (DbServiceEntryTable.COL_SERVICE_ENTRY_DATE));
		String odometer = cursor.getString(cursor.getColumnIndex
                (DbServiceEntryTable.COL_SERVICE_ENTRY_ODOMETER));

		// Constructs a new instance of the Calendar subclass appropriate for the default Locale
		Calendar cal = Calendar.getInstance();
		// Set the date/time to the value in COL_VEHICLE_YEAR (stored in Epoch time)
		cal.setTimeInMillis(date);
		// Reformat the date/time to be user-friendly
		String format = "MMM' 'dd', 'yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String dateString = sdf.format(cal.getTime());
		
		// Assign the cursor data to fields in the listView 
		((TextView) view.findViewById(R.id.date_text)).setText(dateString);
		((TextView) view.findViewById(R.id.odometer_text)).setText(odometer);
	}

}
