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
    private var hospitalRepository : HospitalRepository = HospitalRepository(application)
    private var hospitalList : LiveData<List<HospitalInfo>> = hospitalRepository.getAllHosptial()

    fun getAll(): LiveData<List<HospitalInfo>>{
        return this.hospitalList
    }

    fun insert(hospital : HospitalInfo){
        viewModelScope.launch(Dispatchers.IO) {
            hospitalRepository.insert(hospital)
        }
    }

    fun deleteAlldeleteAllHospital(){
        viewModelScope.launch(Dispatchers.IO) {
            hospitalRepository.deleteAllHospital()
        }
    }

}