package com.example.parsingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WebData";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "ParsedData";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "titles";
    private static final String COLUMN_CONTENT = "content";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
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

    public void saveData(String title, String content) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);

        long rowId = db.insert(TABLE_NAME, null, values);
        Log.d("Database", "Inserted row ID: " + rowId); // Проверка ID вставленной строки
    }




    public List<Map<String, String>> getSavedData() {
        List<Map<String, String>> data = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT titles, content FROM " + TABLE_NAME, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Map<String, String> item = new HashMap<>();
                    item.put("title", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                    item.put("content", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                    data.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
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

