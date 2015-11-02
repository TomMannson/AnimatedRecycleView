package com.example.t0m3k88.greendao;

import android.app.Application;

import com.example.t0m3k88.greendao.persistance.DbProvider;
import com.example.t0m3k88.greendao.persistance.EntityManager;
import com.example.t0m3k88.greendao.persistance.DbNotificationManager;
import com.example.t0m3k88.greendao.persistance.NotificationManagerProvider;
import com.example.t0m3k88.greendao.persistance.model.DaoMaster;
import com.example.t0m3k88.greendao.persistance.model.DaoSession;
import com.example.t0m3k88.greendao.persistance.model.Note;
import com.example.t0m3k88.greendao.persistance.model.User;

import java.lang.ref.WeakReference;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by t0m3k88 on 16.08.2015.
 */
public class App extends Application implements NotificationManagerProvider {

    static WeakReference<App> weekApp = null;
    DaoMaster master = null;
    DbProvider provider = null;
    DbNotificationManager manager = new DbNotificationManager();

    @Override
    public void onCreate() {
        super.onCreate();
        weekApp = new WeakReference<>(this);
        provider = new DbProvider(this, "database", null);
        master = new DaoMaster(provider.getWritableDatabase());
    }

    static public AbstractDao<?, ?> getInstanceOfDao(Class<?> clazz){
        App app = weekApp.get();
        if(app != null) {
            DaoSession session = app.master.newSession();
            if (clazz == User.class) {
                return session.getDao(clazz);
            }
            if (clazz == Note.class) {
                return session.getDao(clazz);
            }
            else{
                throw new RuntimeException("");
            }
        }
        throw new RuntimeException("");
    }

    static public <T, Long> EntityManager<?, ?> getEntityMamager(Class<T> clazz){
        App app = weekApp.get();
        if(app != null) {
            AbstractDao<T, Long>  dao = (AbstractDao<T, Long>)getInstanceOfDao(clazz);
            return new EntityManager<>(app, dao);
        }
        throw new RuntimeException("");
        }


    static public App getInstance(){
        App app = weekApp.get();
        if(app != null) {
            return app;
        }
        throw new RuntimeException("");
    }

    @Override
    public DbNotificationManager provide() {
        return manager;
    }
}
