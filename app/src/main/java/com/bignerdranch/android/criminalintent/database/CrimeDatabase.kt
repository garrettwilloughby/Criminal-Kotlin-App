package com.bignerdranch.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.criminalintent.Crime

@Database (entities = [Crime::class], version = 2)
//@Database tells room that this class represents a database.
//always need a version number
@TypeConverters(CrimeTypeConverter::class)
abstract class CrimeDatabase : RoomDatabase(){
    abstract fun crimeDao() : CrimeDao
}

val migration_1_2 = object : Migration(1, 2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''")
    }
}