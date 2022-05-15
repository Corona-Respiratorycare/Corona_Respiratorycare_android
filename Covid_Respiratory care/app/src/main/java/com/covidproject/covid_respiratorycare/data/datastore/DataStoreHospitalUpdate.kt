package com.covidproject.covid_respiratorycare.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreHospitalUpdate(val context : Context){

    private val Context.dataStore  by preferencesDataStore(name = "dataStore")
    private val stringKey = stringPreferencesKey("update_day") // string 저장 키값
    
    // Flow : coroutines.flow import 해야됨
    val text : Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            preferences[stringKey] ?: ""
        }

    // String값을 stringKey 키 값에 저장
    suspend fun setText(updateDay : String){
        context.dataStore.edit { preferences ->
            preferences[stringKey] = updateDay
        }
    }
}