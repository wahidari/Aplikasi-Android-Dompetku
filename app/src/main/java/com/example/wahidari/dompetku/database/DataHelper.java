package com.example.wahidari.dompetku.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dompetku.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_PENGELUARAN  = "CREATE TABLE pengeluaran (id INTEGER PRIMARY KEY, idkategori INTEGER, deskripsi TEXT, jumlah INTEGER, tanggal DEFAULT CURRENT_DATE);";
    private static final String CREATE_TABLE_PENDAPATAN   = "CREATE TABLE pendapatan (id INTEGER PRIMARY KEY, idkategori INTEGER, deskripsi TEXT, jumlah INTEGER, tanggal DEFAULT CURRENT_DATE);";
    private static final String CREATE_TABLE_KATEGORI_PENDAPATAN  = "CREATE TABLE kategori_pendapatan (id INTEGER PRIMARY KEY, jenis TEXT);";
    private static final String CREATE_TABLE_KATEGORI_PENGELUARAN = "CREATE TABLE kategori_pengeluaran (id INTEGER PRIMARY KEY, jenis TEXT);";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        // Create Table
        db.execSQL(CREATE_TABLE_PENGELUARAN);
        db.execSQL(CREATE_TABLE_PENDAPATAN);
        db.execSQL(CREATE_TABLE_KATEGORI_PENDAPATAN);
        db.execSQL(CREATE_TABLE_KATEGORI_PENGELUARAN);

        // Insert To Table kategori_pendapatan
        db.execSQL("INSERT INTO 'kategori_pendapatan' ('jenis') VALUES ('Gaji') ");
        db.execSQL("INSERT INTO 'kategori_pendapatan' ('jenis') VALUES ('Laba Usaha') ");
        db.execSQL("INSERT INTO 'kategori_pendapatan' ('jenis') VALUES ('Hadiah') ");
        db.execSQL("INSERT INTO 'kategori_pendapatan' ('jenis') VALUES ('Lain-Lain') ");

        // Insert To Table kategori_pengeluaran
        db.execSQL("INSERT into 'kategori_pengeluaran' ('jenis') VALUES ('Konsumsi') ");
        db.execSQL("INSERT into 'kategori_pengeluaran' ('jenis') VALUES ('Transportasi') ");
        db.execSQL("INSERT into 'kategori_pengeluaran' ('jenis') VALUES ('Belanja') ");
        db.execSQL("INSERT into 'kategori_pengeluaran' ('jenis') VALUES ('Kesehatan') ");
        db.execSQL("INSERT into 'kategori_pengeluaran' ('jenis') VALUES ('Lain-Lain') ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    public void onReset(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // on reset drop older tables
        db.execSQL("DROP TABLE IF EXISTS pengeluaran");
        db.execSQL("DROP TABLE IF EXISTS pendapatan");
        db.execSQL("DROP TABLE IF EXISTS kategori_pendapatan");
        db.execSQL("DROP TABLE IF EXISTS kategori_pengeluaran");

        onCreate(db);
    }
}
