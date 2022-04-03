package com.covidproject.covid_respiratorycare.ui.Service.mapping

interface MappingView {
    fun onMappingLoading()
    fun onMappingSuccess(hopitalList: List<HospitalInfo>)
    fun onMappingFailure(code:Int, message:String)
}