package com.androstock.quickgalleryorg.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.androstock.quickgalleryorg.Configurations;


public class Database extends SQLiteOpenHelper {
    String DBPATH;

    private Context DbContext = null;

    public Database(Context context) {
        super(context, Configurations.DATABASE_NAME, null, Configurations.DATABASE_VERSION);

        onCreate(getWritableDatabase());
        DbContext = context;

        DBPATH = context.getDatabasePath(Configurations.DATABASE_NAME).getAbsolutePath();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /* GET DATA */
        db.execSQL("DROP TABLE IF EXISTS IMAGE");
        db.execSQL("DROP TABLE IF EXISTS CATEGORY");

        onCreate(db);
    }


    @Override
    public void onCreate(SQLiteDatabase db) throws SQLiteException {

        String sqlQuery = "";

        if(db.isOpen()){
            try {
                sqlQuery = "CREATE TABLE IF NOT EXISTS `IMAGE` (" +
                        "UID integer primary key autoincrement" +
                        ",KEYWORDS text" +
                        ",ALBUM text" +
                        ",LASTMODIFIED text" +
                        ",LATITUDE text" +
                        ",LONGITUDE text" +
                        ");";
                db.execSQL(sqlQuery);

                Toast.makeText(DbContext,"Banco criado com sucesso",Toast.LENGTH_LONG).show();
            } catch(Exception e) {
                Toast.makeText(DbContext,"Falha ao criar banco",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}