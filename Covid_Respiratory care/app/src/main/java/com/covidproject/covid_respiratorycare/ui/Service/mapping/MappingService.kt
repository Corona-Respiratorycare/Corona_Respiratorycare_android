package com.covidproject.covid_respiratorycare.ui.Service.mapping

import android.util.Log
import com.covidproject.covid_respiratorycare.baseretrofit
import java.lang.Exception

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
        Log.d("Resul","4.1")
        try{
            val mappingService = baseretrofit.create(MappingRetrofitInterface::class.java)
            Log.d("Result","4")
            val mappingresult = mappingService.getHospitalInfo()
            Log.d("Result",mappingresult.toString())
            if (mappingresult.code == 1000){
                mappingView.onMappingSuccess(mappingresult.result)
            }
            else{
                mappingView.onMappingFailure(5000,"네트워크 오류일 가능성이 큼")
            }
        }catch (e :Exception){
            Log.d("SplashAcitivity Error",e.toString())
        }
    }

    suspend fun getUpdateInfo() {
        val updateDateService = baseretrofit.create(MappingRetrofitInterface::class.java)
        updateMapView.onUpdateMapLoading()

        try{
            val updateDateResult = updateDateService.getUpdateInfo()
            Log.d("Result",updateDateResult.result.updated_date.toString())
            if (updateDateResult.code == 1000){
                updateMapView.onUpdateMapSuccess(updateDateResult.result.updated_date)
            }
            else{
                updateMapView.onUpdateMapFailure(5000,"네트워크 오류일 가능성이 큼")
            }
        }catch (e :Exception){
            Log.d("SplashAcitivity Error",e.toString())
        }

    }

}