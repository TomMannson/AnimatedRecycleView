package com.example.t0m3k88.greendao.persistance;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.t0m3k88.greendao.JoinColumns;
import com.example.t0m3k88.greendao.persistance.model.Note;
import com.example.t0m3k88.greendao.persistance.model.NoteDao;
import com.example.t0m3k88.greendao.persistance.model.User;
import com.example.t0m3k88.greendao.persistance.model.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.Query;

/**
 * Created by t0m3k88 on 16.08.2015.
 */
public class GreenDaoHelper {

    Query aaa = null;


//    public <T> List<T> eagerSelect(AbstractDao<T, ?> dao , JoinColumns... joins){
//
//
//        aaa.
//        List<T> result = new ArrayList<>();
//        HashMap<Long, T> ids = new HashMap<>();
//        for (int i = 0; i < joins.length; i++){
//            JoinColumns join = joins[i];
//            String selection = buildColumnSelectForDao(dao, join.getDao());
//            String tablesToJoin = buildJoinTablesFromDao(dao, new JoinColumns(NoteDao.Properties.User_id, join.getDao()));
//            Cursor c = dao.getDatabase().rawQuery(String.format("Select %s from %s", selection, tablesToJoin), null);
//            int aaaaaaaaa = c.getCount();
//            T u = new T();
//            while(c.moveToNext()) {
//
//                u = dataFromCache(ids, c, dao);
//                if(u == null) {
//                    u = (T) getObject(dao, c);
//                    ids.put(c.getLong(0), u);
//                    result.add(u);
//                }
//                Object a = getObject(join.getDao(), c, true);
//                fillObject(u, join, a);
//            }
//        }
//
//        return result;
//    }



    public <T> List<T> eagerSelect(AbstractDao<T, ?> dao , JoinColumns... joins){

        List<T> result = new ArrayList<>();
        HashMap<Long, T> ids = new HashMap<>();
        for (int i = 0; i < joins.length; i++){
            JoinColumns join = joins[i];
            String selection = buildColumnSelectForDao(dao, join.getDao());
            String tablesToJoin = buildJoinTablesFromDao(dao, new JoinColumns(NoteDao.Properties.User_id, join.getDao()));
            Cursor c = dao.getDatabase().rawQuery(String.format("Select %s from %s", selection, tablesToJoin), null);
            int aaaaaaaaa = c.getCount();
            T u = null;
            while(c.moveToNext()) {

                u = dataFromCache(ids, c, dao);
                if(u == null) {
                    u = (T) getObject(dao, c);
                    ids.put(c.getLong(0), u);
                    result.add(u);
                }
                Object a = getObject(join.getDao(), c, true);
                fillObject(u, join, a);
            }
        }

        return result;
    }

    private String buildColumnSelectForDao(AbstractDao<?,?>... dao){
        StringBuilder builder = new StringBuilder();

        for (int daosI = 0 ; daosI < dao.length; daosI++){
            AbstractDao daoToBuild = dao[daosI];

            for (int i = 0; i <  daoToBuild.getAllColumns().length; i++){
                String columnName = daoToBuild.getAllColumns()[i];
                builder.append(String.format("'%s'.'%s'", daoToBuild.getTablename(), columnName));
                if(i < daoToBuild.getAllColumns().length - 1 || daosI < dao.length -1)
                    builder.append(',');
            }
        }

        return builder.toString();
    }

    private String buildJoinTablesFromDao(AbstractDao dao , JoinColumns... joins){
        StringBuilder builder = new StringBuilder();
        builder.append(dao.getTablename());

        for (int i = 0; i < joins.length; i++){
            builder.append(' ');
            builder.append(joins[i].joinPart(dao));
        }

        return builder.toString();
    }

    private <T> T dataFromCache(HashMap<Long, T> set, Cursor cursor, AbstractDao dao){
        String idColumn = String.format("'%s'.'_id'", dao.getTablename());
        int index = cursor.getColumnIndex(idColumn);
        long id = cursor.getLong(0);
        return set.get(id);
    }

    private <T> T getObject(AbstractDao<?, ?> dao, Cursor c){
        return getObject(dao, c, false);
    }

    private <T> T getObject(AbstractDao<?, ?> dao, Cursor c, boolean offset){
        if(dao instanceof UserDao){
            UserDao userDao = (UserDao)dao;
            return (T) userDao.readEntity(c, offset ? dao.getAllColumns().length - 1 : 0);
        }
        else if(dao instanceof NoteDao){
            NoteDao noteDao = (NoteDao)dao;
            return (T) noteDao.readEntity(c, offset ? dao.getAllColumns().length - 1 : 0);
        }
        else{
            return null;
        }
    }

    private void fillObject(Object object, JoinColumns joinData, Object objectToFill){
        if(object instanceof User){
            User user = (User) object;

            switch (joinData.getProperty()){
                case "USER_ID":{
                    user.getNoteList().add((Note)objectToFill);
                    break;
                }
            }
        }
        else if(object instanceof Note){

        }
    }
}
