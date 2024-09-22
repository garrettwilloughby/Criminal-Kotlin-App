package com.bignerdranch.android.criminalintent

import android.app.Application

class CriminalIntentApplication : Application(){
    //we want to extend application, if we do, we will get access as soon as
    //app is created.

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}