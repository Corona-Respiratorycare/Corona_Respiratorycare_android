package com.covidproject.covid_respiratorycare.data

data class UpdateInfo(
    val code: Int,
    val message: String,
    val result: updateinfo_date
)
data class updateinfo_date(
    val updated_date: String,
)