package com.covidproject.covid_respiratorycare.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    // 누적 확진자, 신규 확진자, 지역 확진자, 외국 확진자
    private val _defCnt = MutableLiveData<String>()
    private val _incCnt = MutableLiveData<String>()
    private val _localCnt = MutableLiveData<String>()
    private val _overFlowCnt = MutableLiveData<String>()
    private val _stdDay = MutableLiveData<String>()
    val defCnt : LiveData<String>
        get() = _defCnt
    val incCnt : LiveData<String>
        get() = _incCnt
    val localCnt : LiveData<String>
        get() = _localCnt
    val overFlowCnt : LiveData<String>
        get() = _overFlowCnt
    val stdDay : LiveData<String>
        get() = _stdDay

    fun updatedefCnt(str:String){
        _defCnt.value = str
    }
    fun updateincCnt(str:String){
        _incCnt.value = str
    }
    fun updatelocalCnt(str:String){
        _localCnt.value = str
    }
    fun updateoverFlowCnt(str:String){
        _overFlowCnt.value = str
    }
    fun updatestdDay(str:String){
        _stdDay.value = str
    }

    init{
        _defCnt.value = "0"
    }

}