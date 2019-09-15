package com.racavalieri.quickgalleryorg.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.racavalieri.quickgalleryorg.Configurations;


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
        db.execSQL("DROP TABLE IF EXISTS IMAGE");
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
                        ",PATH text" +
                        ",LASTMODIFIED text" +
                        ",LATITUDE text" +
                        ",LONGITUDE text" +
                        ");";
                db.execSQL(sqlQuery);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}