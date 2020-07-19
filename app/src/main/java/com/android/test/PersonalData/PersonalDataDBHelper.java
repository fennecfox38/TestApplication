package com.android.test.PersonalData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

public class PersonalDataDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="PersonalData.db";
    private static final int DATABASE_VERSION=1;
    private Context context;

    public PersonalDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.SQL_CREATE_TBL);
        Toast.makeText(context,"DBHelper onCreate",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.SQL_DROP_TBL);
        onCreate(db);
    }

    public static final class DBContract implements BaseColumns {
        public static final String TABLE_NAME = "dataTable";
        //public static final String _ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_BIRTHDAY = "birthday";
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_MARRIAGE = "marriage";
        public static final String COLUMN_CHILDREN = "children";

        public static final String SQL_CREATE_TBL="CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" ("+
                //_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_NAME+" TEXT NOT NULL,"+ COLUMN_PASSWORD+" TEXT NOT NULL,"+
                COLUMN_BIRTHDAY+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
                COLUMN_SEX+" INTEGER NOT NULL,"+ COLUMN_MARRIAGE+" INTEGER NOT NULL,"+
                COLUMN_CHILDREN+" INTEGER NOT NULL)";
        public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        public static final String SQL_SELECT = "SELECT * FROM " + TABLE_NAME;
        public static final String SQL_INSERT = "INSERT OR REPLACE INTO "+TABLE_NAME+
                "("+COLUMN_NAME+", "+COLUMN_PASSWORD+", "+COLUMN_BIRTHDAY+", "+
                COLUMN_SEX+", "+COLUMN_MARRIAGE+", "+COLUMN_CHILDREN+") VALUES";
        public static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME;
        public static final String SQL_DELETE_ALL = "DELETE * FROM " + TABLE_NAME;
    }
}
