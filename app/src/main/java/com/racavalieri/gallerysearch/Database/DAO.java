package com.racavalieri.gallerysearch.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;


public class DAO {

    private static String dbPath;

    private static Context dbContext = null;

    private static Database database;

    public DAO(Context context) {
        dbContext = context;

        try {
            database = new Database(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean exist(String fields, String table, String Column, String where) {
        SQLiteDatabase db = database.getReadableDatabase();

        return DatabaseUtils.queryNumEntries(db, table, Column + "=?", new String[]{fields}) > 0;
    }

    public static Cursor select(String fields, String table, String where) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + fields + " FROM " + table + " WHERE " + where + ";", null);
        return cursor;
    }

    public static long insert(String table, ContentValues values) {
        SQLiteDatabase db = database.getWritableDatabase();
        long rowsInserted = -1;
        try {
            rowsInserted = db.insert(table, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();

        return rowsInserted;
    }

    public static long update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        long rowsInserted = -1;
        try {
            rowsInserted = db.update(table, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();

        return rowsInserted;
    }
}