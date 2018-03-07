package edu.buffalo.cse.cse486586.groupmessenger2;

import android.provider.BaseColumns;

/**
 * Created by parik on 2/17/18.
 */

public class DBContract {

    private DBContract(){}

    public static class ConversationTable implements BaseColumns{

        public static final String DATABASE_NAME = "ConversationDB";
        public static final String TABLE_NAME="Conversation";
        public static final String KEY_COLUMN="key";
        public static final String VALUE_COLUMN= "value";

    }
}
