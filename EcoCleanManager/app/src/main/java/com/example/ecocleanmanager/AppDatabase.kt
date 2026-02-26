package com.example.ecocleanmanager

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ResiduoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun residuoDao(): ResiduoDao
}