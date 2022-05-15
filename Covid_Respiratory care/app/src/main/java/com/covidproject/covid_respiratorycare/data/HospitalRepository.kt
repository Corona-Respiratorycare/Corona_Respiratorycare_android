package com.covidproject.covid_respiratorycare.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class HospitalRepository(application: Application) : ViewModel() {
    private val hospitalDB: HospitalDatabase = HospitalDatabase.getInstance(application)!!
    private val hospitalDao = hospitalDB.HospitalInfoDao()
    private val hospitalLlist : LiveData<List<ResultX>> = hospitalDao.getallHospital()
    private val TAG = "HospitalRepository"

    fun getAllHosptial() : LiveData<List<ResultX>>{
        return hospitalLlist
    }

    fun insert(hospital : ResultX) {
        viewModelScope.launch(Dispatchers.Default) {
            try{
                hospitalDao.insert(hospital)
            } catch (e:Exception){
                Log.d(TAG,e.toString())
            }
        }
    }
    fun deleteAllHospital(){
        viewModelScope.launch(Dispatchers.Default) {
            try{
                hospitalDao.deleteAllHospital()
            }catch (e:Exception){
                Log.d(TAG,e.toString())
            }
        }

    }

}