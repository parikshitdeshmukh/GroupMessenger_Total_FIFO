package edu.buffalo.cse.cse486586.groupmessenger2;

import android.provider.BaseColumns;

/**
 * Created by parik on 2/17/18.
 */

/** Reference: Android Developer : https://developer.android.com/develop/index.html
 *  Priority Queue : https://docs.oracle.com/javase/7/docs/api/java/util/PriorityQueue.html
 *  Input/Output streams: https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html
 * Content Provider : https://developer.android.com/guide/topics/providers/content-providers.html
 * Content Resolver : https://developer.android.com/reference/android/content/ContentResolver.html
 *
 * */

public class DBContract {

    private DBContract(){}

    public static class ConversationTable implements BaseColumns{

        public static final String DATABASE_NAME = "ConversationDB";
        public static final String TABLE_NAME="Conversation";
        public static final String KEY_COLUMN="key";
        public static final String VALUE_COLUMN= "value";

    }
}
