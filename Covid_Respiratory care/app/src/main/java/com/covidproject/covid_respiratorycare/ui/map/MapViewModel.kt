package com.covidproject.covid_respiratorycare.ui.map

import androidx.lifecycle.*

class MapViewModel : ViewModel() {
    private val _hospitalname = MutableLiveData<String>()
    private val _telno = MutableLiveData<String>()
    private val _startday = MutableLiveData<String>()
    private val _ispcr = MutableLiveData<String>()
    private val _israt = MutableLiveData<String>()
    private val _hospitalcode = MutableLiveData<String>()
    private val _addr = MutableLiveData<String>()
    private val _lat = MutableLiveData<String>()
    private val _lng = MutableLiveData<String>()
    private val _dist = MutableLiveData<String>()

    private val _userposition = MutableLiveData<Pair<Double,Double>>()
    val userposition : LiveData<Pair<Double,Double>>
        get() = _userposition
    fun updateuserposition(lat : Pair<Double,Double>){
        _userposition.value = lat
    }
    fun updatedist(dist : String){
        _dist.value = dist
    }

    val dist : LiveData<String>
        get() = _dist

    val hospitalname : LiveData<String>
        get() = _hospitalname
    val telno : LiveData<String>
        get() = _telno
    val startday : LiveData<String>
        get() = _startday
    val ispcr : LiveData<String>
        get() = _ispcr
    val israt : LiveData<String>
        get() = _israt
    val hospitalcode : LiveData<String>
        get() = _hospitalcode
    val addr : LiveData<String>
        get() = _addr
    val lat : LiveData<String>
        get() = _lat
    val lng : LiveData<String>
        get() = _lng

    fun updateposition(lat:String, lng:String ){
        _lng.value = lng
        _lat.value = lat
    }
    fun updatehospitalname(name : String){
        _hospitalname.value = name
    }
    fun updatetelno(name : String){
        _telno.value = name
    }
    fun updatestartday(name : String){
        _startday.value = name
    }
    fun updateispcr(name : String){
        _ispcr.value = name
    }
    fun updateisrat(name : String){
        _israt.value = name
    }
    fun updatehospitalcode(name : String){
        _hospitalcode.value = name
    }
    fun updateaddr(name : String){
        _addr.value = name
    }

    private val _telEvent = MutableLiveData<MapEvent<String>>()
    val telEvent : LiveData<MapEvent<String>> get() = _telEvent
    fun onTelEvent(text : String){
        _telEvent.value = MapEvent(text)
    }

    private val _naverAppEvent = MutableLiveData<MapEvent<Triple<String,String,String>>>()
    val naverAppEvent : LiveData<MapEvent<Triple<String,String,String>>> get() = _naverAppEvent
    fun onNaverAppEvent(lat : String, lng : String, hospitalname : String){
        // Content를 T로 생성 -> 인자하나로 그냥 Triple로 다 넣어버림
        _naverAppEvent.value = MapEvent(Triple(lat,lng,hospitalname))
    }

    init{
        _hospitalname.value = ""
        _telno.value = ""
        _startday.value = ""
        _ispcr.value = ""
        _israt.value = ""
        _hospitalcode.value = ""
        _addr.value = ""
//        _userposition.value = Pair(0.0,0.0)
    }

}