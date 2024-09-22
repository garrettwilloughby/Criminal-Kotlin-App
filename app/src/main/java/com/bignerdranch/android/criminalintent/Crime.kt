package com.bignerdranch.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID


@Entity
data class Crime(
    //val has to be there
    //UUID is not exactly unique but randomly generated
    @PrimaryKey val id: UUID,
    val title: String,
    val date : Date,
    val isSolved : Boolean,
    val suspect : String = ""
)

