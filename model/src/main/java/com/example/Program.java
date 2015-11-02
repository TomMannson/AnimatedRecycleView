package com.example;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class Program {
    public static void main(String[] args){

        Schema schema = new Schema(1, "com.example.t0m3k88.greendao.persistance.model");

        Entity user = schema.addEntity("User");
        user.addIdProperty();
        user.addStringProperty("name");

        Entity notes = schema.addEntity("Note");
        notes.addIdProperty();
        notes.addStringProperty("name");
        Property user_id = notes.addLongProperty("user_id").getProperty();

        user.addToMany(notes, user_id);

        File fff = new File("");

        try {
            File aaaaaaa = fff.getAbsoluteFile();
            DaoGenerator daoGenerator = new DaoGenerator();
            daoGenerator.generateAll(schema, aaaaaaa.getAbsolutePath());
        }catch (Exception ex){
            ex.toString();
        }
    }
}
