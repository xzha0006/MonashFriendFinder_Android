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
        public static final String TABLE_NAME = "user_location";
        public static final String LOCATION_ID = "location_id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
    }

}

