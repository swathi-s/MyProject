package com.example.swathi.myproject;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by user on 9/21/2016.
 */
public class EmployeeDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ProjectDatabase";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Employees";
    public static final String UID = "_id";
    public static final String NAME = "name";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    Context context;

    public static final String createQuery = "CREATE TABLE "+ TABLE_NAME +" ( " + UID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME + " VARCHAR(50), " + USERNAME + " VARCHAR(250), " + PASSWORD + " VARCHAR(250))";
    //public static final String createQuery = "CREATE TABLE " + TABLE_NAME + " ( " + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " VARCHAR(255), " + PASSWORD + " VARCHAR(255))";

    public static final String dropQuery = "DROP TABLE IF EXISTS "+ TABLE_NAME;

    public EmployeeDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Toast.makeText(context,"Constructor method has called",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(createQuery);
            Toast.makeText(context,TABLE_NAME+" table has been created successfully",Toast.LENGTH_SHORT).show();
        }catch (SQLException e){
            Toast.makeText(context,"exception is "+ e,Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL(dropQuery);
            onCreate(db);
            Toast.makeText(context,TABLE_NAME+" table has been upgraded successfully",Toast.LENGTH_SHORT).show();
        }catch (SQLException e){
            Toast.makeText(context,"exception is "+e,Toast.LENGTH_LONG).show();
        }

    }
}
