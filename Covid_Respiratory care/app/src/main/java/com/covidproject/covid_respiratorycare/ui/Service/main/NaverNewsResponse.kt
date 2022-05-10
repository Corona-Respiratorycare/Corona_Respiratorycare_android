package com.covidproject.covid_respiratorycare.ui.Service.main

import com.google.gson.annotations.SerializedName

data class NaverNewsResponse(
    @SerializedName("display") val display: Int,
    @SerializedName("items")val navernews: List<NaverNews>,
    @SerializedName("lastBuildDate")val lastBuildDate: String,
    @SerializedName("start")val start: Int,
    @SerializedName("total")val total: Int
)

data class NaverNews(
    @SerializedName("description")val description: String,
    @SerializedName("link")val link: String,
    @SerializedName("originallink")val originallink: String,
    @SerializedName("pubDate")val pubDate: String,
    @SerializedName("title")val title: String
)

data class DaumNewsResponse(
    val code: Int,
    val message: String,
    @SerializedName("result")val DaumNews: List<DaumNews>
)

data class DaumNews(
    val contents: String,
    val datetime: String,
    val title: String,
    val url: String
)