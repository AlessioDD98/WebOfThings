package com.example.wot2;

import androidx.room.RoomDatabase;

@androidx.room.Database(version = 1, entities = {Sensore.class})
public abstract class Database extends RoomDatabase {
    public abstract DaoSensore daoSensore();
}
