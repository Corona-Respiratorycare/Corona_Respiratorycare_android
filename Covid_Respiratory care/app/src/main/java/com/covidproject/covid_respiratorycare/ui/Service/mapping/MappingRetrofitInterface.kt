package com.covidproject.covid_respiratorycare.ui.Service.mapping

import com.covidproject.covid_respiratorycare.data.HopistalInfoResult_test
import com.covidproject.covid_respiratorycare.data.UpdateInfo_test
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MappingRetrofitInterface {
    @GET("/api/v1/hospitals")
    suspend fun getHospitalInfo(): HopistalInfoResult_test

    @GET("/api/v1/hospitals/updated-date")
    suspend fun getUpdateInfo() : UpdateInfo_test

//    UpdateDataResult
}