package com.covidproject.covid_respiratorycare.ui.Service.main

interface MainInfoView {
    fun onInfoLoading()
    fun onInfoSuccess(funcname : String, data : ArrayList<String>)
    fun onNaverNewsSuccess(data : List<NaverNews>)
    fun onInfoFailure(message:String)
}