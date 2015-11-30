package com.diversedistractions.vehiclelog; /**
 * Created by sbrown on 1/5/2015.
 */

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Defines column names and creation strings for the Vehicles table.
 */
public final class DbVehicleTable {

    /*
     * Name of the table that contains the list of vehicles
     */
    public static final String VEHICLE_DATABASE_TABLE = "vehicles";

    /*
     * Vehicle table column definitions
     */
    // Column name for the primary key
    public static final String COL_VEHICLE_ROW_ID = "_id";
    // Column name for the vehicle's make
    public static final String COL_VEHICLE_MAKE = "make";
    // Column name for the vehicle's model
    public static final String COL_VEHICLE_MODEL = "model";
    // Column name for the vehicle's model year
    public static final String COL_VEHICLE_YEAR = "year";
    // Column name for the vehicle's identification number
    public static final String COL_VEHICLE_VIN = "vin";
    // Column name for the vehicle's license plate
    public static final String COL_VEHICLE_LP = "plate";
    // Column name for the vehicle's license plate renewal date
    public static final String COL_VEHICLE_REN_DATE = "ren_date";
    // Column name for the vehicle's icon or photo
    public static final String COL_VEHICLE_IMAGE = "veh_image";
    // Column name for the vehicle's year-to-date miles per gallon
    public static final String COL_VEHICLE_YTD_MPG = "ytd_mpg";
    // Column name for the vehicle's note
    public static final String COL_VEHICLE_NOTE = "note";

    /*
     * Database table creation SQL statement for the vehicles list
     */
    private static final String VEHICLES_TABLE_CREATE =
            "create table " + VEHICLE_DATABASE_TABLE + "(" +
                    COL_VEHICLE_ROW_ID + " integer primary key autoincrement, " +
                    COL_VEHICLE_MAKE + " text not null, " +
                    COL_VEHICLE_MODEL + " text not null, " +
                    COL_VEHICLE_YEAR + " integer not null, " +
                    COL_VEHICLE_VIN + " text, " +
                    COL_VEHICLE_LP + " text, " +
                    COL_VEHICLE_REN_DATE + " integer, " +
                    COL_VEHICLE_IMAGE + " text, " +
                    COL_VEHICLE_NOTE + " text, " +
                    COL_VEHICLE_YTD_MPG + " text);";

    //TODO: Is this duplicated in the DbAdapter?
    // Create the database table
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(VEHICLES_TABLE_CREATE);
    }

    //TODO: Is this duplicated in the DbAdapter?
    // Upgrade the database table
    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(DbVehicleTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        //TODO: Create code to upgrade the database table in place
        database.execSQL("DROP TABLE IF EXISTS " + VEHICLE_DATABASE_TABLE);
        onCreate(database);
    }

}
