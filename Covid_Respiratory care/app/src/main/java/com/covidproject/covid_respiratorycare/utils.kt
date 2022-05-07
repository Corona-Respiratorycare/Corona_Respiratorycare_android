package com.covidproject.covid_respiratorycare

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

val gson = Gson()

val baseretrofit = Retrofit.Builder()
    .baseUrl("https://aws-api.10cheon00.xyz") //베이스 URL 넣기
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

val openapiretrofit = Retrofit.Builder()
    .baseUrl("http://openapi.data.go.kr") //베이스 URL 넣기
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

val navernewsretrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://openapi.naver.com")
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()
