package com.example.t0m3k88.greendao.persistance;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by t0m3k88 on 31.10.2015.
 */
public class DbNotificationManager {

    public static final String TAG = "NotificationManager";

    final HashMap<String, List<GreenDaoLoader.ForceLoadContentObserver>> observers = new HashMap<>();

    public synchronized void register(String tag, GreenDaoLoader.ForceLoadContentObserver observer){
        List<GreenDaoLoader.ForceLoadContentObserver> list = observers.get(tag);

        if(list == null){
            list = new ArrayList<>();
            observers.put(tag, list);
        }

        if(list.contains(observer))
            return;
        else{
            list.add(observer);
        }
    }

    public synchronized void unregister(String tag, GreenDaoLoader.ForceLoadContentObserver observer){
        List<GreenDaoLoader.ForceLoadContentObserver> list = observers.get(tag);

        if(list != null) {
            list.remove(observer);
        }
    }

    public synchronized void notify(String tag){
        List<GreenDaoLoader.ForceLoadContentObserver> list = observers.get(tag);

        for(int i = 0; i < list.size(); i++){
            list.get(i).onChange(true);
        }
    }

    public static DbNotificationManager from(Context ctx){
        DbNotificationManager manager = null;
        Application app = (Application)ctx.getApplicationContext();
        if(app instanceof NotificationManagerProvider){
            manager =((NotificationManagerProvider)app).provide();//.unregister("TEST", mObserver);
        }
        if(manager == null)
            Log.d(TAG, "Application doesn't implement NotificationManagerProvider");
        return manager;
    }
}
