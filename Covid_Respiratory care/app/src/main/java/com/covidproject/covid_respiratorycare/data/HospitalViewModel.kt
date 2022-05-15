package com.covidproject.covid_respiratorycare.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HospitalViewModel(application: Application) : AndroidViewModel(application) {
//class HospitalViewModel(application: Application) : ViewModel() {
    private lateinit var hospitalRepository : HospitalRepository
    private lateinit var hospitalList : LiveData<List<ResultX>>

    fun getAll(): LiveData<List<ResultX>>{
        return hospitalList
    }

    fun insert(hospital : ResultX){
        viewModelScope.launch(Dispatchers.Default) {
            hospitalRepository.insert(hospital)
        }
    }

    fun deleteAlldeleteAllHospital(){
        viewModelScope.launch(Dispatchers.Default) {
            hospitalRepository.deleteAllHospital()
        }
    }

    init{
        hospitalRepository = HospitalRepository(application)
        hospitalList = hospitalRepository.getAllHosptial()
    }

}