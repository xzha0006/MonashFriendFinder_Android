package com.johnsyard.monashfriendfinder.dbmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * This class is used for SQLite store
 * Created by xuanzhang on 2/05/2017.
 */

public class DBStructure {
    public static abstract class tableEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_ID = "userid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DOB = "dob";
    }

}

