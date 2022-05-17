package com.covidproject.covid_respiratorycare

import android.app.Application
import android.util.Log
import com.covidproject.covid_respiratorycare.data.datastore.DataStoreHospitalUpdate

class CovidRespiratorycareApp : Application() {
    private lateinit var dataStore : DataStoreHospitalUpdate

    companion object {
        private lateinit var covidRespiratorycareApp : CovidRespiratorycareApp
        fun getInstance() : CovidRespiratorycareApp = covidRespiratorycareApp
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AppClass","Test")
        covidRespiratorycareApp = this
        dataStore = DataStoreHospitalUpdate(this)
    }

    fun getDataStore() : DataStoreHospitalUpdate = dataStore
}