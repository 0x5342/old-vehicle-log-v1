package com.diversedistractions.vehiclelog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
    // Overall database info
    private static final String DATABASE_NAME = "vehiclelog.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "com.diversedistractions.vehiclelog.DbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    private static class DatabaseHelper extends SQLiteOpenHelper

    {

        // Calls the super constructor, requesting the default cursor factory
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creates the underlying database with the table names and column names
        // provided in each of the table classes.
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            DbVehicleTable.onCreate(db);
            DbFuelEntryTable.onCreate(db);
            DbServiceEntryTable.onCreate(db);
        }

        // Method is called during an upgrade of the database,
        // e.g. if you increase the database version
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // TODO: Change this text to say it will upgrade the database and
            //       add a user interface to allow them to acknowledge or deny
            //       the upgrade.
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            //TODO: Change this over to an Intent for the new activity
            /**
             // Back up the existing database
             com.diversedistractions.vehiclelog.DbBackupActivity();
             */
            //TODO: Check that the backup completed correctly before proceeding

            // Create the new database tables
            db.execSQL("DROP TABLE IF EXISTS vehicles");
            db.execSQL("DROP TABLE IF EXISTS fuel_entries");
            db.execSQL("DROP TABLE IF EXISTS service_entries");
            onCreate(db);

            //TODO: Change this over to an Intent for the new activity
            /**
             // Restore the backed up data into the new database tables
             com.diversedistractions.vehiclelog.DbRestoreActivity();
             */
        }
    }

    /**
     * Constructor - takes the context to allow the database to be opened/created
     *
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx)
    {
        this.mCtx = ctx;
    }

    /**
     * Open the vehicles database. If it cannot be opened, try to create a new instance of the
     * database. If it cannot be created, throw an exception to signal the failure.
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException
    {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    /**
     * Create a new vehicle using the make, model, year, vin, license plate, path to vehicle image,
     * & renewal date provided.
     *
     * @param make value to set vehicle make to
     * @param model value to set vehicle model to
     * @param year value to set vehicle year to
     * @param vin value to set vehicle identification number to
     * @param plate value to set license plate to
     * @param veh_image value to set location of vehicle image to
     * @param ren_date value to set license plate renewal date to
     * @param note value to set the vehicle's note to
     * @return return the new rowId for this vehicle, otherwise return a -1 to indicate failure
     */
    public long createVehicle(String make, String model, Long year, String vin, String plate,
                              String veh_image, Long ren_date, String note)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DbVehicleTable.COL_VEHICLE_MAKE, make);
        initialValues.put(DbVehicleTable.COL_VEHICLE_MODEL, model);
        initialValues.put(DbVehicleTable.COL_VEHICLE_YEAR, year);
        initialValues.put(DbVehicleTable.COL_VEHICLE_VIN, vin);
        initialValues.put(DbVehicleTable.COL_VEHICLE_LP, plate);
        initialValues.put(DbVehicleTable.COL_VEHICLE_IMAGE, veh_image);
        initialValues.put(DbVehicleTable.COL_VEHICLE_REN_DATE, ren_date);
        initialValues.put(DbVehicleTable.COL_VEHICLE_NOTE, note);

        return mDb.insert(DbVehicleTable.VEHICLE_DATABASE_TABLE, null, initialValues);
    }

    /**
     * Create a new fuel entry using the date, odometer, gallons, partial/full tank,
     * & entry MPG provided.
     *
     * @param vehicle value to set fuel entry vehicle ID to
     * @param date value to set fuel entry date to
     * @param odometer value to set fuel entry odometer value to
     * @param gallons value to set fuel entry gallons filled to
     * @param cost value to set fuel entry amount paid to
     * @param partial value to set fuel entry is not a full tank
     * @param entry_mpg value to set fuel entry Miles Per Gallon to
     * @return return the new rowId for this entry, otherwise return a -1 to indicate failure
     */
    public long createFuelEntry(Long vehicle, Long date, String odometer, String gallons,
                                String cost, int partial, String entry_mpg)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DbFuelEntryTable.COL_VEH_ROW_ID, vehicle);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_DATE, date);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER, odometer);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_GALLONS, gallons);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_COST, cost);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_PARTIAL, partial);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_MPG, entry_mpg);

        return mDb.insert(DbFuelEntryTable.FUEL_ENTRY_DATABASE_TABLE, null, initialValues);
    }

    /**
     * Create a new service entry using the date, odometer, oil flag, oil filter flag,
     * air filter flag, cabin air filter flag, rotated tires flag & service note provided.
     *
     * @param vehicle value to set service entry to
     * @param date value to set service entry date to
     * @param odometer value to set service entry odometer to
     * @param oil value to set service entry oil change checkbox to
     * @param oilFilter value to set service entry oil filter checkbox to
     * @param airFilter value to set service entry air filter checkbox to
     * @param caFilter value to set service entry cabin air filter checkbox to
     * @param rotTires value to set service entry rotated tires checkbox to
     * @param note value to set service entry note to
     * @return return the new rowId for this entry, otherwise return a -1 to indicate failure
     */
    public long createServiceEntry(Long vehicle, Long date, String odometer, int oil, int oilFilter,
                                   int airFilter, int caFilter, int rotTires, String note)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DbServiceEntryTable.COL_VEH_ROW_ID, vehicle);
        initialValues.put(DbServiceEntryTable.COL_SERVICE_ENTRY_DATE, date);
        initialValues.put(DbServiceEntryTable.COL_SERVICE_ENTRY_ODOMETER, odometer);
        initialValues.put(DbServiceEntryTable.COL_SERVICE_ENTRY_OIL, oil);
        initialValues.put(DbServiceEntryTable.COL_SERVICE_ENTRY_OIL_FILTER, oilFilter);
        initialValues.put(DbServiceEntryTable.COL_SERVICE_ENTRY_AIR_FILTER, airFilter);
        initialValues.put(DbServiceEntryTable.COL_SERVICE_ENTRY_CABIN_AIR_FILTER, caFilter);
        initialValues.put(DbServiceEntryTable.COL_SERVICE_ENTRY_ROTATED_TIRES, rotTires);
        initialValues.put(DbServiceEntryTable.COL_SERVICE_ENTRY_NOTE, note);

        return mDb.insert(DbServiceEntryTable.SERVICE_ENTRY_DATABASE_TABLE, null,
                initialValues);
    }

    /**
     * Delete the vehicle with the given rowId
     *
     * @param rowId the unique ID of the vehicle to delete
     */
    public boolean deleteVehicle(long rowId)
    {
        return mDb.delete(DbVehicleTable.VEHICLE_DATABASE_TABLE,
                DbVehicleTable.COL_VEHICLE_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Delete the fuel entry with the given rowId
     *
     * @param rowId the unique ID of the fuel entry to delete
     */
    public boolean deleteFuelEntry(long rowId)
    {
        return mDb.delete(DbFuelEntryTable.FUEL_ENTRY_DATABASE_TABLE,
                DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Delete the service entry with the given rowId
     *
     * @param rowId the unique ID of the service entry to delete
     */
    public boolean deleteServiceEntry(long rowId)
    {
        return mDb.delete(DbServiceEntryTable.SERVICE_ENTRY_DATABASE_TABLE,
                DbServiceEntryTable.COL_SERVICE_ENTRY_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Delete all data entries for a vehicle with a given vehRowId
     *
     * @param vehicle the unique ID of the vehicle to delete all fuel entries
     */
    // TODO: Change this to delete all associated data for the vehicle
    public boolean deleteAllVehicleFuelEntries(long vehicle)
    {
        return mDb.delete(DbFuelEntryTable.FUEL_ENTRY_DATABASE_TABLE,
                DbFuelEntryTable.COL_VEH_ROW_ID + "=" + vehicle, null) > 0;
    }

    /**
     * Return a Cursor over the list of all vehicles in the database
     */
    public Cursor fetchAllVehiclesOldDb()
    {
        return mDb.query(DbVehicleTable.VEHICLE_DATABASE_TABLE, new String[] {
                        DbVehicleTable.COL_VEHICLE_ROW_ID,
                        DbVehicleTable.COL_VEHICLE_MAKE,
                        DbVehicleTable.COL_VEHICLE_MODEL,
                        DbVehicleTable.COL_VEHICLE_YEAR,
                        DbVehicleTable.COL_VEHICLE_VIN,
                        DbVehicleTable.COL_VEHICLE_LP,
                        DbVehicleTable.COL_VEHICLE_IMAGE,
                        DbVehicleTable.COL_VEHICLE_REN_DATE},
                null, null, null, null, null);
    }

    /**
     * Return a Cursor over the list of all vehicles in the database
     */
    public Cursor fetchAllVehicles()
    {
        return mDb.query(DbVehicleTable.VEHICLE_DATABASE_TABLE, new String[] {
                        DbVehicleTable.COL_VEHICLE_ROW_ID,
                        DbVehicleTable.COL_VEHICLE_MAKE,
                        DbVehicleTable.COL_VEHICLE_MODEL,
                        DbVehicleTable.COL_VEHICLE_YEAR,
                        DbVehicleTable.COL_VEHICLE_VIN,
                        DbVehicleTable.COL_VEHICLE_LP,
                        DbVehicleTable.COL_VEHICLE_IMAGE,
                        DbVehicleTable.COL_VEHICLE_REN_DATE,
                        DbVehicleTable.COL_VEHICLE_NOTE},
                null, null, null, null, null);
        //TODO: need to add in the new column
    }

    /**
     * Return a Cursor over the list of all fuel entries for the selected vehicle in the database
     */
    public Cursor fetchAllEntriesOldDb()
    {
        return mDb.query(DbFuelEntryTable.FUEL_ENTRY_DATABASE_TABLE, new String[] {
                        DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID,
                        DbFuelEntryTable.COL_VEH_ROW_ID,
                        DbFuelEntryTable.COL_FUEL_ENTRY_DATE,
                        DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER,
                        DbFuelEntryTable.COL_FUEL_ENTRY_GALLONS,
                        DbFuelEntryTable.COL_FUEL_ENTRY_COST,
                        DbFuelEntryTable.COL_FUEL_ENTRY_PARTIAL,
                        DbFuelEntryTable.COL_FUEL_ENTRY_MPG},
                null, null, null, null, null);
    }

    /**
     * Return a Cursor over the list of all fuel entries for the selected vehicle in the database
     *
     * @param rowId is the unique id of the vehicle to retrieve all fuel entries for
     * @return Cursor listing all fuel entries for this vehicle
     */
    public Cursor fetchRelevantFuelEntries(long rowId)
    {
        return mDb.query(true, DbFuelEntryTable.FUEL_ENTRY_DATABASE_TABLE, new String[] {
                        DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID,
                        DbFuelEntryTable.COL_FUEL_ENTRY_DATE,
                        DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER,
                        DbFuelEntryTable.COL_FUEL_ENTRY_GALLONS,
                        DbFuelEntryTable.COL_FUEL_ENTRY_COST,
                        DbFuelEntryTable.COL_FUEL_ENTRY_PARTIAL,
                        DbFuelEntryTable.COL_FUEL_ENTRY_MPG},
                DbFuelEntryTable.COL_VEH_ROW_ID + "=" + rowId,
                null, null, null, DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER + " ASC", null);
        //TODO: need to add in the new column
    }

    /**
     * Return a Cursor over the list of all service entries for the selected vehicle in the database
     *
     * @param rowId is the unique id of the vehicle to retrieve all service entries for
     * @return Cursor listing all service entries for this vehicle
     */
    public Cursor fetchRelevantServiceEntries(long rowId)
    {
        return mDb.query(true, DbServiceEntryTable.SERVICE_ENTRY_DATABASE_TABLE,
                new String[] {
                        DbServiceEntryTable.COL_SERVICE_ENTRY_ROW_ID,
                        DbServiceEntryTable.COL_SERVICE_ENTRY_DATE,
                        DbServiceEntryTable.COL_SERVICE_ENTRY_ODOMETER,
                        DbServiceEntryTable.COL_SERVICE_ENTRY_OIL,
                        DbServiceEntryTable.COL_SERVICE_ENTRY_OIL_FILTER,
                        DbServiceEntryTable.COL_SERVICE_ENTRY_AIR_FILTER,
                        DbServiceEntryTable.COL_SERVICE_ENTRY_CABIN_AIR_FILTER,
                        DbServiceEntryTable.COL_SERVICE_ENTRY_ROTATED_TIRES,
                        DbServiceEntryTable.COL_SERVICE_ENTRY_NOTE},
                DbServiceEntryTable.COL_VEH_ROW_ID + "=" + rowId, null, null, null,
                DbServiceEntryTable.COL_SERVICE_ENTRY_DATE + " ASC", null);
    }

    /**
     * Return a Cursor positioned at the vehicle that matches the given rowId
     *
     * @param rowId id of vehicle to retrieve
     * @return Cursor positioned to matching vehicle, if found
     * @throws SQLException if vehicle could not be found/retrieved
     */
    public Cursor fetchVehicle(long rowId) throws SQLException
    {
        Cursor mCursor =

                mDb.query(true, DbVehicleTable.VEHICLE_DATABASE_TABLE, new String[] {
                                DbVehicleTable.COL_VEHICLE_ROW_ID,
                                DbVehicleTable.COL_VEHICLE_MAKE,
                                DbVehicleTable.COL_VEHICLE_MODEL,
                                DbVehicleTable.COL_VEHICLE_YEAR,
                                DbVehicleTable.COL_VEHICLE_VIN,
                                DbVehicleTable.COL_VEHICLE_LP,
                                DbVehicleTable.COL_VEHICLE_IMAGE,
                                DbVehicleTable.COL_VEHICLE_REN_DATE,
                                DbVehicleTable.COL_VEHICLE_NOTE},
                        DbVehicleTable.COL_VEHICLE_ROW_ID + "=" + rowId,
                        null, null, null, null, null);
        //TODO: need to add in the new lifetime mpg column
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Return a Cursor positioned at the fuel entry that matches the given rowId
     *
     * @param rowId id of fuel entry to retrieve
     * @return Cursor positioned to matching entry, if found
     * @throws SQLException if entry could not be found/retrieved
     */
    public Cursor fetchFuelEntry(long rowId) throws SQLException
    {
        Cursor mCursor =

                mDb.query(true, DbFuelEntryTable.FUEL_ENTRY_DATABASE_TABLE, new String[] {
                                DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID,
                                DbFuelEntryTable.COL_VEH_ROW_ID,
                                DbFuelEntryTable.COL_FUEL_ENTRY_DATE,
                                DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER,
                                DbFuelEntryTable.COL_FUEL_ENTRY_GALLONS,
                                DbFuelEntryTable.COL_FUEL_ENTRY_COST,
                                DbFuelEntryTable.COL_FUEL_ENTRY_PARTIAL,
                                DbFuelEntryTable.COL_FUEL_ENTRY_MPG},
                        DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID + "=" + rowId,
                        null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Return a Cursor positioned at the service entry that matches the given rowId
     *
     * @param rowId id of service entry to retrieve
     * @return Cursor positioned to matching entry, if found
     * @throws SQLException if entry could not be found/retrieved
     */
    public Cursor fetchServiceEntry(long rowId) throws SQLException
    {
        Cursor mCursor =

                mDb.query(true, DbServiceEntryTable.SERVICE_ENTRY_DATABASE_TABLE,
                        new String[] {
                                DbServiceEntryTable.COL_SERVICE_ENTRY_ROW_ID,
                                DbServiceEntryTable.COL_SERVICE_ENTRY_DATE,
                                DbServiceEntryTable.COL_SERVICE_ENTRY_ODOMETER,
                                DbServiceEntryTable.COL_SERVICE_ENTRY_OIL,
                                DbServiceEntryTable.COL_SERVICE_ENTRY_OIL_FILTER,
                                DbServiceEntryTable.COL_SERVICE_ENTRY_AIR_FILTER,
                                DbServiceEntryTable.COL_SERVICE_ENTRY_CABIN_AIR_FILTER,
                                DbServiceEntryTable.COL_SERVICE_ENTRY_ROTATED_TIRES,
                                DbServiceEntryTable.COL_SERVICE_ENTRY_NOTE},
                        DbServiceEntryTable.COL_SERVICE_ENTRY_ROW_ID + "=" + rowId,
                        null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the vehicle using the details provided. The vehicle to be updated is
     * specified using the rowId, and it is altered to use the make, model
     * year, vin, license plate, vehicle image, renewal date, & note values passed in.
     *
     * @param rowId id of vehicle to update
     * @param make value to set vehicle make to
     * @param model value to set vehicle model to
     * @param year value to set vehicle year to
     * @param vin value to set vehicle identification number to
     * @param plate value to set license plate to
     * @param veh_image value to set location of vehicle image to
     * @param ren_date value to set license plate renewal date to
     * @param note value to set the vehicle's note to
     * @return true if the vehicle was successfully updated, false otherwise
     */
    public boolean updateVehicle(long rowId, String make, String model, Long year, String vin,
                                 String plate, String veh_image, Long ren_date, String note)
    {
        ContentValues args = new ContentValues();
        args.put(DbVehicleTable.COL_VEHICLE_MAKE, make);
        args.put(DbVehicleTable.COL_VEHICLE_MODEL, model);
        args.put(DbVehicleTable.COL_VEHICLE_YEAR, year);
        args.put(DbVehicleTable.COL_VEHICLE_VIN, vin);
        args.put(DbVehicleTable.COL_VEHICLE_LP, plate);
        args.put(DbVehicleTable.COL_VEHICLE_REN_DATE, ren_date);
        args.put(DbVehicleTable.COL_VEHICLE_NOTE, note);

        return mDb.update(DbVehicleTable.VEHICLE_DATABASE_TABLE, args,
                DbVehicleTable.COL_VEHICLE_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Update the fuel entry using the details provided. The fuel entry to be updated is specified
     * using the rowId, and it is altered to use the vehicle ID, date odometer, gallons,
     * partial tank, & entry MPG values passed in.
     *
     * @param rowId id of fuel entry to update
     * @param date value to set fuel entry date to
     * @param odometer value to set fuel entry odometer value to
     * @param gallons value to set fuel entry gallons filled to
     * @param cost value to set fuel entry amount paid to
     * @param partial value to set fuel entry as a partial tank
     * @param entry_mpg value to set fuel entry Miles Per Gallon to
     * @return true if the fuel entry was successfully updated, false otherwise
     */
    public boolean updateFuelEntry(long rowId, Long date, String odometer, String gallons,
                                   String cost, int partial, String entry_mpg)
    {
        ContentValues args = new ContentValues();
        args.put(DbFuelEntryTable.COL_FUEL_ENTRY_DATE, date);
        args.put(DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER, odometer);
        args.put(DbFuelEntryTable.COL_FUEL_ENTRY_GALLONS, gallons);
        args.put(DbFuelEntryTable.COL_FUEL_ENTRY_COST, cost);
        args.put(DbFuelEntryTable.COL_FUEL_ENTRY_PARTIAL, partial);
        args.put(DbFuelEntryTable.COL_FUEL_ENTRY_MPG, entry_mpg);

        return mDb.update(DbFuelEntryTable.FUEL_ENTRY_DATABASE_TABLE, args,
                DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Update the service entry using the details provided. The service entry to be updated is
     * specified using the rowId, and it is altered to use the vehicle ID, date, odometer, oil,
     * oil filter, air filter, cabin air filter, rotated tires, & note values passed in.
     *
     * @param rowId id of service entry to update
     * @param date value to set service entry date to
     * @param odometer value to set service entry odometer to
     * @param oil value to set service entry oil change checkbox to
     * @param oilFilter value to set service entry oil filter checkbox to
     * @param airFilter value to set service entry air filter checkbox to
     * @param caFilter value to set service entry cabin air filter checkbox to
     * @param rotTires value to set service entry rotated tires checkbox to
     * @param note value to set service entry note to
     * @return true if the service entry was successfully updated, false otherwise
     */
    public boolean updateServiceEntry(Long rowId, Long date, String odometer, int oil,
                                      int oilFilter, int airFilter, int caFilter, int rotTires,
                                      String note)
    {
        ContentValues args = new ContentValues();
        args.put(DbServiceEntryTable.COL_SERVICE_ENTRY_DATE, date);
        args.put(DbServiceEntryTable.COL_SERVICE_ENTRY_ODOMETER, odometer);
        args.put(DbServiceEntryTable.COL_SERVICE_ENTRY_OIL, oil);
        args.put(DbServiceEntryTable.COL_SERVICE_ENTRY_OIL_FILTER, oilFilter);
        args.put(DbServiceEntryTable.COL_SERVICE_ENTRY_AIR_FILTER, airFilter);
        args.put(DbServiceEntryTable.COL_SERVICE_ENTRY_CABIN_AIR_FILTER, caFilter);
        args.put(DbServiceEntryTable.COL_SERVICE_ENTRY_ROTATED_TIRES, rotTires);
        args.put(DbServiceEntryTable.COL_SERVICE_ENTRY_NOTE, note);

        return mDb.update(DbServiceEntryTable.SERVICE_ENTRY_DATABASE_TABLE, args,
                DbServiceEntryTable.COL_SERVICE_ENTRY_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Write the vehicle into the new db table using the primary key, make, model, year, vin,
     * license plate, vehicle image, renewal date and year-to-date mpg read from the backup file.
     *
     * @param rowId id of vehicle to update
     * @param make value to set vehicle make to
     * @param model value to set vehicle model to
     * @param year value to set vehicle year to
     * @param vin value to set vehicle identification number to
     * @param plate value to set license plate to
     * @param ren_date value to set license plate renewal date to
     * @param ytd_mpg value to set year-to-date mpg to
     * @return true if the vehicle data was successfully written, false otherwise
     */
    public long writeUpgradeVehicle(Long rowId, String make, String model, Long year,
                                    String vin, String plate, Long ren_date,
                                    String ytd_mpg)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbVehicleTable.COL_VEHICLE_ROW_ID, rowId);
        contentValues.put(DbVehicleTable.COL_VEHICLE_MAKE, make);
        contentValues.put(DbVehicleTable.COL_VEHICLE_MODEL, model);
        contentValues.put(DbVehicleTable.COL_VEHICLE_YEAR, year);
        contentValues.put(DbVehicleTable.COL_VEHICLE_VIN, vin);
        contentValues.put(DbVehicleTable.COL_VEHICLE_LP, plate);
        contentValues.put(DbVehicleTable.COL_VEHICLE_REN_DATE, ren_date);
        contentValues.put(DbVehicleTable.COL_VEHICLE_YTD_MPG, ytd_mpg);

        return mDb.insert(DbVehicleTable.VEHICLE_DATABASE_TABLE, null, contentValues);
    }

    /**
     * Write the entry into the new db table using the entry row ID, vehicle row ID,
     * date, odometer, gallons, & full tank flag, & entry's mpg read from the backup file.
     *
     * @param rowId id of fuel entry to write
     * @param vehicle value to set fuel entry to
     * @param date value to set fuel entry to
     * @param odometer value to set fuel entry to
     * @param gallons value to set fuel entry to
     * @param partial value to set fuel entry to
     * @param entry_mpg value to set fuel entry to
     * @return true if the fuel entry was successfully written, false otherwise
     */
    public long writeUpgradeFuelEntry(Long rowId, Long vehicle, Long date, String odometer,
                                      String gallons, int partial, String entry_mpg)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_ROW_ID, rowId);
        initialValues.put(DbFuelEntryTable.COL_VEH_ROW_ID, vehicle);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_DATE, date);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_ODOMETER, odometer);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_GALLONS, gallons);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_PARTIAL, partial);
        initialValues.put(DbFuelEntryTable.COL_FUEL_ENTRY_MPG, entry_mpg);

        return mDb.insert(DbFuelEntryTable.FUEL_ENTRY_DATABASE_TABLE, null, initialValues);
    }

    // TODO: Write Upgrade Service Entry
    // TODO: Write Upgrade Vehicle Note
}
