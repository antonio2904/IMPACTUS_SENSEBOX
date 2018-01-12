package com.impactus.impactus_sensebox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class ImpactusDataBaseAdapter
{

    static Cursor impactuscursor;
    static Cursor impactuscursor2;
    static final String DATABASE_NAME = "impactusdatabase.db";
    static final int DATABASE_VERSION = 1;
    public static final int NAME_COLUMN = 1;
    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE = "create table "+"IMPACTUS_DATA"+
            "( " +"ID"+" integer primary key autoincrement,"+ "LATITUDE  text,LONGITUDE text,DATE_TIME text, TEMP text,HUMIDITY text, SOIL text, LOCATION text); ";
    // Variable to hold the database instance
    public  SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;
    public ImpactusDataBaseAdapter(Context _context)
    {
        context = _context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public  ImpactusDataBaseAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        db.close();

    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    public void insertEntry(String latitude,String longitude,String date_time, String temp,String humi, String SoilM, String loconame)
    {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("LATITUDE", latitude);
        newValues.put("LONGITUDE",longitude);
        newValues.put("DATE_TIME", date_time);
        newValues.put("TEMP", temp);
        newValues.put("HUMIDITY", humi);
        newValues.put("SOIL", SoilM);
        newValues.put("LOCATION",loconame);
        // Insert the row into your table
        db.insert("IMPACTUS_DATA", null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }

    public Cursor MapperReturn()
    {
        impactuscursor=db.query(true,"IMPACTUS_DATA",new String[]{"LATITUDE","LONGITUDE","LOCATION"},null,null,null,null,null,null);
        impactuscursor.moveToFirst();
        return impactuscursor;
    }

    public Cursor ListReturn(double lat, double longi)
    {   String lati;
        String longit;
        lati=Double.toString(lat);
        longit=Double.toString(longi);
        impactuscursor2=db.query("IMPACTUS_DATA",new String[]{"TEMP","HUMIDITY","SOIL","DATE_TIME","LATITUDE","LONGITUDE","LOCATION"},("LATITUDE = " + lati +" and LONGITUDE = "+longit),null,null,null,null,null);
        impactuscursor2.moveToFirst();
        return impactuscursor2;
    }
}