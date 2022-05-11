package com.covidproject.covid_respiratorycare.ui.Service.main

interface MainInfoView {
    fun onInfoLoading(funcname : String)
    fun onInfoSuccess(funcname : String, data : ArrayList<String>)
    fun onNaverNewsSuccess(data : List<NaverNews>)
    fun onDaumNewsSuccess(news : List<DaumNews>)
    fun onInfoFailure(message:String)
}