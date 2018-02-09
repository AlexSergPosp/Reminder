package com.example.alexander.birthday.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Alexander on 05.02.2018.
 */

public final class BirthContract {

    private BirthContract() {
    };

    // Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.alexander.birthday";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BIRTHS = "main";

    public static final class ManEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BIRTHS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BIRTHS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BIRTHS);
        public final static String TABLE_NAME = "main";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_DATE = "date";
    }

    public static String[] getProtection() {

        String[] projection = {
                BirthContract.ManEntry._ID,
                BirthContract.ManEntry.COLUMN_NAME,
                BirthContract.ManEntry.COLUMN_DATE};

        return projection;
    }
}
