package com.covidproject.covid_respiratorycare.ui.Service.mapping

import com.covidproject.covid_respiratorycare.data.ResultX

interface MappingView {
    fun onMappingLoading()
    fun onMappingSuccess(hopitalList: List<ResultX>)
    fun onMappingFailure(code:Int, message:String)
}

interface UpdateMapView {
    fun onUpdateMapLoading()
    fun onUpdateMapSuccess(date : String)
    fun onUpdateMapFailure(code:Int, message:String)
}