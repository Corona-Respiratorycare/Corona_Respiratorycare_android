package com.covidproject.covid_respiratorycare.ui.Service.mapping

import com.covidproject.covid_respiratorycare.data.HospitalInfo

interface MappingView {
    fun onMappingLoading()
    fun onMappingSuccess(hopitalList: List<HospitalInfo>)
    fun onMappingFailure(code:Int, message:String)
}

interface UpdateMapView {
    fun onUpdateMapLoading()
    fun onUpdateMapSuccess(date : String)
    fun onUpdateMapFailure(code:Int, message:String)
}