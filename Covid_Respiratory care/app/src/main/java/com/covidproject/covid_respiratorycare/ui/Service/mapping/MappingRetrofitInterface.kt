package com.covidproject.covid_respiratorycare.ui.Service.mapping

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MappingRetrofitInterface {
    @GET("/api/v1/hospitals")
    suspend fun getHospitalInfo(): HopitalInfoResult

    @GET("api/v1/hospitals/updated-date")
    suspend fun getUpdateInfo():UpdateDataResult
//    suspend fun getHospitalInfo(@Header("auth") jwt : String,
//                                @Query("inputStr") inputtext: String): HopitalInfoResult

}