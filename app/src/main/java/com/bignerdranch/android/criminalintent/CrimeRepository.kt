package com.bignerdranch.android.criminalintent

import android.content.Context
import androidx.room.Room
import com.bignerdranch.android.criminalintent.database.CrimeDatabase
import com.bignerdranch.android.criminalintent.database.migration_1_2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.UUID

private const val DATABASE_NAME = "crime-database"
class CrimeRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope){

    private val database : CrimeDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
        )
        //.createFromAsset(DATABASE_NAME)
        //get rid of migrate so data can load??
        .addMigrations(migration_1_2)
        .build()

    //everytime we update database we gotta change the crimeRepo
    fun getCrimes(): Flow<List<Crime>> = database.crimeDao().getCrimes()

    suspend fun getCrime(id:UUID) : Crime = database.crimeDao().getCrime(id)
    //fill out crime repo with functions so other components can perf operations


    fun updateCrime(crime: Crime){
        coroutineScope.launch {
            database.crimeDao().updateCrime(crime)
        }
    }

    suspend fun addCrime(crime: Crime){
        database.crimeDao().addCrime(crime)
    }

    //add delete crime function
    suspend fun deleteCrime(crimeId : UUID){
        val crime = database.crimeDao().getCrime(crimeId)
        database.crimeDao().deleteCrime(crime)

    }


    companion object{
        private var INSTANCE : CrimeRepository? = null

        fun initialize(context: Context) {
            //our singleton
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository{
            return INSTANCE?:
            throw IllegalStateException("Crime repo must be initalized")
        }


    }
}