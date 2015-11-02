package com.example.t0m3k88.greendao;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

/**
 * Created by t0m3k88 on 16.08.2015.
 */
public class JoinColumns {
    private String property;
    private String table;
    AbstractDao dao;

    public JoinColumns(Property property, AbstractDao dao){
        this.property = property.columnName;
        this.table = dao.getTablename();
        this.dao = dao;
    }

    public String joinPart(AbstractDao dao){
        String join = String.format("JOIN '%s' ON '%s'.'%s' = '%s'.'_id'", table, table, property, dao.getTablename());
        return join;
    }

    public AbstractDao getDao(){
        return dao;
    }

    public String getProperty() {
        return property;
    }
}
