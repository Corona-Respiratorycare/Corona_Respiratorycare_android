package com.covidproject.covid_respiratorycare.ui.Service.mapping

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MappingService {

    private lateinit var mappingView: MappingView

    fun setmappingView(mappingView: MappingView) {
        this.mappingView = mappingView
    }

    val gson = Gson()
    val baseretrofit = Retrofit.Builder()
        .baseUrl("https://aws-api.10cheon00.xyz") //베이스 URL 넣기
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

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

}