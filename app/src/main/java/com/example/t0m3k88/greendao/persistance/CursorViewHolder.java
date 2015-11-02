package com.example.t0m3k88.greendao.persistance;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import com.example.t0m3k88.greendao.App;
import com.example.t0m3k88.greendao.persistance.model.User;
import com.example.t0m3k88.greendao.persistance.model.UserDao;

/**
 * Created by t0m3k88 on 31.10.2015.
 */
public abstract class CursorViewHolder<T> extends RecyclerView.ViewHolder {

    protected T item = null;
    protected Class<T> clazz = null;

    public CursorViewHolder(View itemView, Class<T> clazz) {
        super(itemView);
        try {
            item = (T) clazz.newInstance();
        }
        catch (Exception ex){}
    }

    public abstract void bind(Cursor cursor);
}
