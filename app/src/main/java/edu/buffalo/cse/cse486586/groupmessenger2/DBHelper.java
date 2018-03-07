package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by parik on 2/17/18.
 */

public class DBHelper extends SQLiteOpenHelper {


    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE = " CREATE TABLE " + DBContract.ConversationTable.TABLE_NAME
            + " ( " + DBContract.ConversationTable.KEY_COLUMN + " TEXT NOT NULL, "
            + DBContract.ConversationTable.VALUE_COLUMN + " TEXT NOT NULL )";

    static final String DELETE_TABLE = "Drop table if exists " + DBContract.ConversationTable.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DBContract.ConversationTable.DATABASE_NAME, null, DATABASE_VERSION);
       // db.execSQL(DELETE_TABLE);
    }

//    public DBHelper getDbHelper(){
//
//        if (instance==null) {
//
//            instance= new DBHelper(this.getContext(),db);
//        }
//        return instance;
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       
        db.execSQL(CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);

    }
}
