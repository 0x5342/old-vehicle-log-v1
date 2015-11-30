package com.diversedistractions.vehiclelog;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Defines column names and creation strings for the Fuel Entries table.
 */
public class DbFuelEntryTable {

    /*
     * Name of the table that contains the list of fuel entries for each vehicle
     */
    public static final String FUEL_ENTRY_DATABASE_TABLE = "fuel_entries";

    /*
     * Fuel entries table column definitions
     */
    // Column name for the primary key
    public static final String COL_FUEL_ENTRY_ROW_ID = "_id";
    // Column name for the foreign key referencing the vehicle this entry belongs to
    public static final String COL_VEH_ROW_ID = "v_id";
    // Column name for the date of record for this entry
    public static final String COL_FUEL_ENTRY_DATE = "date";
    // Column name for the odometer reading for this entry
    public static final String COL_FUEL_ENTRY_ODOMETER = "odometer";
    // Column name for the number of gallons filled for this entry
    public static final String COL_FUEL_ENTRY_GALLONS = "gallons";
    // Column name for the number of gallons filled for this entry
    public static final String COL_FUEL_ENTRY_COST = "cost";
    // Column name for the flag (1 or 0) denoting that the tank was not filled for this entry
    public static final String COL_FUEL_ENTRY_PARTIAL = "partialtank";
    // Column name for the miles per gallon of the current full tank entry
    public static final String COL_FUEL_ENTRY_MPG = "entry_mpg";

    /*
     * Database table creation SQL statement for each vehicle's fuel entries
     */
    private static final String FUEL_ENTRY_TABLE_CREATE =
            "create table " + FUEL_ENTRY_DATABASE_TABLE + "(" +
                    COL_FUEL_ENTRY_ROW_ID + " integer primary key autoincrement, " +
                    COL_VEH_ROW_ID + " integer not null, " +
                    COL_FUEL_ENTRY_DATE + " integer not null, " +
                    COL_FUEL_ENTRY_ODOMETER + " real not null, " +
                    COL_FUEL_ENTRY_GALLONS + " real, " +
                    COL_FUEL_ENTRY_COST + " real, " +
                    COL_FUEL_ENTRY_PARTIAL + " integer, " +
                    COL_FUEL_ENTRY_MPG + " real, " +
                    "FOREIGN KEY(" + COL_VEH_ROW_ID +") " +
                    "REFERENCES " + DbVehicleTable.VEHICLE_DATABASE_TABLE + "(_id));";

    //TODO: Is this duplicated in the DbAdapter?
    // Create the database table
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(FUEL_ENTRY_TABLE_CREATE);
    }

    //TODO: Is this duplicated in the DbAdapter?
    // Upgrade the database table
    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(DbVehicleTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        //TODO: Create code to upgrade the database table in place
        database.execSQL("DROP TABLE IF EXISTS " + FUEL_ENTRY_DATABASE_TABLE);
        onCreate(database);
    }
}
