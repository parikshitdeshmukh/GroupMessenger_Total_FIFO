package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by parik on 2/17/18.
 */

/** References: Android Developer : https://developer.android.com/develop/index.html
 *  Priority Queue : https://docs.oracle.com/javase/7/docs/api/java/util/PriorityQueue.html
 *  Input/Output streams: https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html
 * Content Provider : https://developer.android.com/guide/topics/providers/content-providers.html
 * Content Resolver : https://developer.android.com/reference/android/content/ContentResolver.html
 *
 * */
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
