package com.example.t0m3k88.greendao.persistance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.t0m3k88.greendao.persistance.model.DaoMaster;

/**
 * Created by t0m3k88 on 16.08.2015.
 */
public class DbProvider extends DaoMaster.DevOpenHelper {

    public DbProvider(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
