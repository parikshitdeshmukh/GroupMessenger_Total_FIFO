package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import static edu.buffalo.cse.cse486586.groupmessenger2.GroupMessengerActivity.TAG;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */

/** References: Android Developer : https://developer.android.com/develop/index.html
 *  Priority Queue : https://docs.oracle.com/javase/7/docs/api/java/util/PriorityQueue.html
 *  Input/Output streams: https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html
 * Content Provider : https://developer.android.com/guide/topics/providers/content-providers.html
 * Content Resolver : https://developer.android.com/reference/android/content/ContentResolver.html
 *
 * */

public class GroupMessengerProvider extends ContentProvider {

    //private DBHelper dbHelper;
    SQLiteDatabase db;
    //DBHelper instance=null;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         *
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
        DBHelper dbHelper = new DBHelper(this.getContext());

        db = dbHelper.getWritableDatabase();

        Log.e(TAG," Inserting Values: "+ values.keySet());
        // long rowID = db.insertWithOnConflict(DBContract.ConversationTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        long rowID = db.insertWithOnConflict(DBContract.ConversationTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        Log.e(TAG, "Inserted");
        if (rowID>0){
            Uri newUri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        db.close();
        // dbHelper.close();
        // Log.v("insert", values.toString());
        throw new SQLiteException("Insert operation Failed for this record and URI" + uri);

    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        Context context=getContext();
        DBHelper dbHelper= new DBHelper(this.getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        //String sel= DBContract.ConversationTable.KEY_COLUMN+"=?"+;
        //selectionArgs[0] = selection;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DBContract.ConversationTable.TABLE_NAME);

        DBHelper dbHelper = new DBHelper(this.getContext());
        db = dbHelper.getReadableDatabase();
        String sel;
        String[] newSelectionArgs;



        if (selection!=null) {
            newSelectionArgs = new String[]{selection};
            sel = " key = ? ";
        }else{
            sel=null;
            newSelectionArgs=null;
        }

        //Log.e(TAG, "Querying the database with selection:  "+ sel + "and selection args: "+ newSelectionArgs);
        Log.e(TAG, "Selection=" + selection + "  "+ "Selectionargs ="+ selectionArgs);

        Cursor cur = qb.query(db, projection, sel, newSelectionArgs,null,null,sortOrder);
        // Cursor cur= db.query( DBContract.ConversationTable.TABLE_NAME, projection, sel,newSelectionArgs,null,null,null,sortOrder);


        Log.e(TAG, "Queried the database. and ouput is:  "+ cur.getCount());
//        Log.v("query", selection);
        return cur;
    }
}
