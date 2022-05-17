package com.covidproject.covid_respiratorycare.ui.Service.mapping

import com.covidproject.covid_respiratorycare.data.HospitalInfo
import kotlinx.coroutines.Job

interface MappingView {
    fun onMappingLoading()
    fun onMappingSuccess(hopitalList: List<HospitalInfo>)
    fun onMappingFailure(code:Int, message:String)
}

interface UpdateMapView {
    fun onUpdateMapLoading()
    suspend fun onUpdateMapSuccess(date : String): Job
    fun onUpdateMapFailure(code:Int, message:String)
}