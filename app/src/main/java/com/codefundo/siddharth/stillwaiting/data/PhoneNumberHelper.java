package com.codefundo.siddharth.stillwaiting.data;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codefundo.siddharth.stillwaiting.data.PhoneNumber.*;

public class PhoneNumberHelper extends SQLiteOpenHelper  {


    // The database name
    private static final String DATABASE_NAME = "phone.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public PhoneNumberHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold waitlist data
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + siddharth.TABLE_NAME + " (" +
                siddharth._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                siddharth.COLUMN_GUEST_NAME + " TEXT NOT NULL, " +
                //WaitlistEntry.COLUMN_PARTY_SIZE + " INTEGER NOT NULL, " +
                siddharth.COLUMN_NUMBER+ " TEXT NOT NULL, " +
                siddharth.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + siddharth.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }



    public String[] getPhoneNumber(long id){
        int x=(int)id;
        SQLiteDatabase db=this.getReadableDatabase();

        String[] sd= new String[2];

        Cursor cursor=db.query(siddharth.TABLE_NAME,new String[] {siddharth._ID,siddharth.COLUMN_GUEST_NAME,siddharth.COLUMN_NUMBER},siddharth._ID+"=?",
                new String[] {String.valueOf(x)},null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }

        sd[0]=cursor.getString(1);
        sd[1]=cursor.getString(2);
        return sd;
    }


}

