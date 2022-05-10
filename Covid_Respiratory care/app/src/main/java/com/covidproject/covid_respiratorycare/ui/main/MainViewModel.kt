package com.covidproject.covid_respiratorycare.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.covidproject.covid_respiratorycare.ui.Service.main.DaumNews
import com.covidproject.covid_respiratorycare.ui.Service.main.NaverNews

class MainViewModel : ViewModel() {
    // 전광판 쪽 데이터 누적 확진자, 신규 확진자, 지역 확진자, 외국 확진자
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

    // 네이버 뉴스
    private val _naverNews = MutableLiveData<List<NaverNews>>()
    val naverNews : LiveData<List<NaverNews>>
        get() = _naverNews

    fun updatenaverNews(naverNews: List<NaverNews>){
        _naverNews.value = naverNews
    }

    private val _daumNews = MutableLiveData<List<DaumNews>>()
    val daumNews : LiveData<List<DaumNews>>
        get() = _daumNews

    fun updatedaumNews(daumNews: List<DaumNews>){
        _daumNews.value = daumNews
    }

    private val _scrollLocation = MutableLiveData<Pair<Int,Int>>()
    val scrollLocation : LiveData<Pair<Int,Int>>
        get() = _scrollLocation

    fun updateScrollLocation(location: Pair<Int,Int>){
        _scrollLocation.value = location
    }

    init{
        // 전광판 메뉴
        _defCnt.value = "0"
        _incCnt.value = "0"
        _localCnt.value = "0"
        _overFlowCnt.value = "0"
        _stdDay.value = "0"
        _scrollLocation.value=Pair(0,0)
        // 네이버 뉴스
//        _naverNews.value = null
    }

}