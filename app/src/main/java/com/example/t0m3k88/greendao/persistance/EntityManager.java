package com.example.t0m3k88.greendao.persistance;

import android.content.Context;

import com.example.t0m3k88.greendao.App;

import java.lang.ref.WeakReference;
import java.util.Objects;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by t0m3k88 on 31.10.2015.
 */
public class EntityManager<T extends Object, V> {

    AbstractDao<T, V> mDao = null;
    WeakReference<Context> mWeakContext = null;

    public EntityManager(Context ctx, AbstractDao<T, V> dao){
        mDao = dao;
        mWeakContext = new WeakReference<Context>(ctx);
    }

    public void delete(T item, String tag){
        mDao.delete(item);
        Context ctx = mWeakContext.get();
        if(ctx != null){
            DbNotificationManager manager = DbNotificationManager.from(ctx);
            manager.notify(tag);
        }
    }

    public void create(T item, String tag){
        mDao.insertOrReplace(item);
        Context ctx = mWeakContext.get();
        if(ctx != null){
            DbNotificationManager manager = DbNotificationManager.from(ctx);
            manager.notify(tag);
        }
    }

    public void update(T item, String tag){
        mDao.update(item);
        Context ctx = mWeakContext.get();
        if(ctx != null){
            DbNotificationManager manager = DbNotificationManager.from(ctx);
            manager.notify(tag);
        }
    }
}
