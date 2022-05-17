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
    private val hospitalLlist : LiveData<List<HospitalInfo>> = hospitalDao.getallHospital()
    private val TAG = "HospitalRepository"

    fun getAllHosptial() : LiveData<List<HospitalInfo>>{
        return this.hospitalLlist
    }

    fun insert(hospital : HospitalInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                hospitalDao.insert(hospital)
            } catch (e:Exception){
                Log.d(TAG,e.toString())
            }
        }
    }

    fun deleteAllHospital(){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                hospitalDao.deleteAllHospital()
            }catch (e:Exception){
                Log.d(TAG,e.toString())
            }
        }
    }

}