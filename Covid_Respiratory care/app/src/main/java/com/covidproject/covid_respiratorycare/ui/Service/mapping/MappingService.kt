package com.covidproject.covid_respiratorycare.ui.Service.mapping

import com.covidproject.covid_respiratorycare.baseretrofit

class MappingService {

    private lateinit var mappingView: MappingView
    private lateinit var updateMapView: UpdateMapView

    fun setmappingView(mappingView: MappingView) {
        this.mappingView = mappingView
    }

    fun setupdateMapView(updateMapView: UpdateMapView) {
        this.updateMapView = updateMapView
    }


    suspend fun getHospitalInfo() {
        val mappingService = baseretrofit.create(MappingRetrofitInterface::class.java)
        mappingView.onMappingLoading()
        var mappingresult = mappingService.getHospitalInfo()
        if (mappingresult.code == 1000){
            mappingView.onMappingSuccess(mappingresult.HopitalInfoList)
        }
        else{
            mappingView.onMappingFailure(5000,"네트워크 오류일 가능성이 큼")
        }
    }

    suspend fun getUpdateInfo() {
        val mappingService = baseretrofit.create(MappingRetrofitInterface::class.java)
        updateMapView.onUpdateMapLoading()
        var mappingresult = mappingService.getUpdateInfo()
        if (mappingresult.code == 1000){
            updateMapView.onUpdateMapSuccess(mappingresult.updatedate.updated_date)
        }
        else{
            updateMapView.onUpdateMapFailure(5000,"네트워크 오류일 가능성이 큼")
        }
    }

}