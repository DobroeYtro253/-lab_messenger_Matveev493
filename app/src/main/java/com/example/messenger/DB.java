package com.example.messenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE address (id INT ,ip TEXT, port TEXT, recport TEXT);";
        db.execSQL(sql);
    }
    public void addAddress(String id ,String ip, String port, String recport)
    {
        String sid = String.valueOf(id);
        String sql = "INSERT INTO address VALUES('" + id + "', '" + ip + "', '" + port + "', '" + recport + "');";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    public String selectAddress(String name, String key)
    {
        String sid = String.valueOf(key);
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + name + " FROM address WHERE id = '" + sid + "';";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst() == true) return cur.getString(0);
        return "";
    }

    public void deleteAddress(String key)
    {
        String sql = "DELETE FROM address WHERE id = '" + key + "';";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
