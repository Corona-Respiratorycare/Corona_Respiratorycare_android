package com.covidproject.covid_respiratorycare.data

data class Result(
    val XPosWgs84: Double,
    val YPosWgs84: Double,
    val addr: String,
    val mgtStaDd: String,
    val pcrPsblYn: Boolean,
    val ratPsblYn: Boolean,
    val recuClCd: Int,
    val telno: String,
    val yadmNm: String
)