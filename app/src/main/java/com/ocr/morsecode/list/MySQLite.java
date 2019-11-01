package com.ocr.morsecode.list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DB_HISTORY";
    private static final String TABLE = "history";
    public static final String ID = "_id";
    public static final String INPUT = "inputText";
    private static final String OUTPUT = "outputText";
    private static final String SELECTION = " = ?";

    public MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE
                + "(" + ID + " integer primary key autoincrement, "
                + INPUT + " text not null, "
                + OUTPUT + " text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void dodaj(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(INPUT, entry.getInputText());
        contentValues.put(OUTPUT, entry.getOutputText());

        db.insert(TABLE, null, contentValues);
        db.close();
    }

    public void usun(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, ID + SELECTION, new String[]{id});
    }

    public Entry pobierz(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE,
                new String[]{ID, INPUT, OUTPUT},
                SELECTION,
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Entry entry = new Entry(cursor.getString(1), cursor.getString(2));
        entry.setId(Integer.parseInt(cursor.getString(0)));

        return entry;
    }

    public Cursor lista() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * from " + TABLE, null);
    }

}
