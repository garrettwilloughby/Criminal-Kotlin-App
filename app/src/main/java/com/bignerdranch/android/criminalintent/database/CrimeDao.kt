package com.bignerdranch.android.criminalintent.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.criminalintent.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CrimeDao {

    //Pull out all data entries
    @Query("Select * FROM crime")
    fun getCrimes() : Flow<List<Crime>>

    //pick up specific id
    @Query("SELECT * FROM crime WHERE id = (:id)")
    //suspend = async code :o, multithreading through coroutine.
    suspend fun getCrime(id : UUID) : Crime

    @Update
    suspend fun updateCrime(crime: Crime)

    @Insert
    suspend fun addCrime(crime: Crime)

    //add delete method here
    @Delete
    suspend fun deleteCrime(crime : Crime)
    //then add to repository
}