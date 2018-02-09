package com.example.alexander.birthday.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.alexander.birthday.data.BirthContract.*;

/**
 * Created by Alexander on 05.02.2018.
 */

public class BirthDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BirthDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "birth.db";
    private static final int DATABASE_VERSION = 1;

    public BirthDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_BIRTH_TABLE = "CREATE TABLE " + ManEntry.TABLE_NAME + " ("
                + ManEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ManEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ManEntry.COLUMN_DATE + " TEXT NOT NULL);";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_BIRTH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_NAME);
        // Создаём новую таблицу
        onCreate(db);
    }
}
