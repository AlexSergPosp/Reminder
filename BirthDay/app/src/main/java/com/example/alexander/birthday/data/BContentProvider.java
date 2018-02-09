package com.example.alexander.birthday.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alexander.birthday.data.BirthContract.ManEntry;

/**
 * Created by Alexander on 06.02.2018.
 */

public class BContentProvider extends ContentProvider {

    public static final String TAG = BContentProvider.class.getSimpleName();
    private static final int BIRTHS = 100;
    private static final int BIRTH_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BirthContract.CONTENT_AUTHORITY, BirthContract.PATH_BIRTHS, BIRTHS);
        sUriMatcher.addURI(BirthContract.CONTENT_AUTHORITY, BirthContract.PATH_BIRTHS + "/#", BIRTH_ID);
    }

    private BirthDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BirthDBHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Получим доступ к базе данных для чтения
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Курсор, содержащий результат запроса
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BIRTHS:
                cursor = database.query(ManEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BIRTH_ID:
                selection = ManEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ManEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIRTHS:
                return ManEntry.CONTENT_LIST_TYPE;
            case BIRTH_ID:
                return ManEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIRTHS:
                getContext().getContentResolver().notifyChange(uri, null);
                return insertElement(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIRTHS:
                // Delete all rows that match the selection and selection args
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ManEntry.TABLE_NAME, selection, selectionArgs);
            case BIRTH_ID:
                // Delete a single row given by the ID in the URI
                selection = ManEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ManEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIRTHS:
                return updateElement(uri, values, selection, selectionArgs);
            case BIRTH_ID:
                // For the GUEST_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ManEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return updateElement(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertElement(Uri uri, ContentValues values) {
        String name = values.getAsString(ManEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Guest requires a name");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ManEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateElement(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link GuestEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ManEntry.COLUMN_NAME)) {
            String name = values.getAsString(ManEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Guest requires a name");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        return database.update(ManEntry.TABLE_NAME, values, selection, selectionArgs);
    }

}
