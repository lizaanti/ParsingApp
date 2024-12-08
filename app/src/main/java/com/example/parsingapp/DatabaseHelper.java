package com.example.parsingapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WebData.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "ParsedData";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CONTENT = "content";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<String> getAllData() {
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if (cursor.moveToFirst()) {
                do {
                    data.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Логирование ошибок
        }

        return data;
    }


    public List<String> searchData(String keyword) {
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_CONTENT + " LIKE ?", new String[]{"%" + keyword + "%"});

        if (cursor.moveToFirst()) {
            do {
                data.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }
}
