package com.covidproject.covid_respiratorycare.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class HopistalInfoResult_test(
    val code: Int,
    val message: String,
    val result: List<ResultX>
)

@Entity(tableName = "HospitalTable")
data class ResultX(
    val XPosWgs84: Double,
    val YPosWgs84: Double,
    val addr: String,
    val mgtStaDd: String,
    val pcrPsblYn: Boolean,
    val ratPsblYn: Boolean,
    val recuClCd: Int,
    val telno: String,
    val yadmNm: String
){
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}