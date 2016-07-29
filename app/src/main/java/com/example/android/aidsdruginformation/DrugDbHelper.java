package com.example.android.aidsdruginformation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 21-05-2016.
 */
public class DrugDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "drugs.db";

    public DrugDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_DRUGS_TABLE = "CREATE TABLE " + DrugsContract.DrugsEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                DrugsContract.DrugsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                DrugsContract.DrugsEntry.COLUMN_DRUG_ID + " INTEGER NOT NULL, " +
                DrugsContract.DrugsEntry.COLUMN_APPROVAL_STATUS + " TEXT NOT NULL, " +
                DrugsContract.DrugsEntry.COLUMN_DRUG_CLASS + " TEXT NOT NULL, " +
                DrugsContract.DrugsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DrugsContract.DrugsEntry.COLUMN_COMPANY + " TEXT NOT NULL, " +
                DrugsContract.DrugsEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                DrugsContract.DrugsEntry.COLUMN_APPROVED_USE + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_DRUGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DrugsContract.DrugsEntry.TABLE_NAME);
        onCreate(db);
    }
}
