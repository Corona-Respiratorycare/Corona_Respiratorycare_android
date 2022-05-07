package com.covidproject.covid_respiratorycare.ui.Service.main

import com.covidproject.covid_respiratorycare.ui.Service.mapping.UpdateDataResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface MainRetrofitInterface {

    // 일일확진자, 누적확진자, 지역발생, 해외유입
    @GET("/openapi/service/rest/Covid19/getCovid19SidoInfStateJson")
    suspend fun getSeoulCovidMain(
        @Query("serviceKey") servicekey : String,
        @Query("startCreateDt") startcreatedt : Int,
        @Query("endCreateDt") endcreatedt : Int,
        @Query("pageNo") pageno : Int = 1,
        @Query("numOfRows") numofrows : Int = 10
    ): String

    // 네이버 뉴스 가져오기
//    @Headers({
//        "X-Naver-Client-Id: jwWWLVsAVg0CcNAxsR1Z",
//        "X-Naver-Client-Secret: wZYJcW4dSD",
//        })
    @GET("/v1/search/news.json")
    suspend fun getCoronaNaverNews(
        @Query("query") findstr : String,
        @Query("display") display : Int = 10,
        @Query("start") start : Int =1,
        @Query("sort") sort : String = "date",
        @Header("X-Naver-Client-Id") clintid: String = "jwWWLVsAVg0CcNAxsR1Z",
        @Header("X-Naver-Client-Secret") clintsecret: String = "wZYJcW4dSD"
        ): NaverNewsResponse
    
}