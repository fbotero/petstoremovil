package com.mocatto.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by froilan.ruiz on 6/21/2016.
 */
public class EventSQLiteOpenHelper extends SQLiteOpenHelper {

    public EventSQLiteOpenHelper(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Apointment(id integer primary key, eventName text, " +
                "appointmentDate text, hour text,location text, minutesBefore integer,email text," +
                "namePet text, type text, status integer,createdBy integer,countReminder integer, " +
                "bathFrecuency integer, foodBuyRegularity integer)");
        db.execSQL("create table Pet(id integer primary key, image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists Apointment");
        db.execSQL("drop table if exists Pet");
        db.execSQL("create table Apointment(id integer primary key, eventName text, " +
                "appointmentDate text, hour text,location text, minutesBefore integer,email text," +
                "namePet text, type text, status integer,createdBy integer,countReminder integer," +
                "bathFrecuency integer, foodBuyRegularity integer)");
        db.execSQL("create table Pet(id integer primary key, image BLOB)");
    }
}
