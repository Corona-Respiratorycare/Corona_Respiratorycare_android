package com.covidproject.covid_respiratorycare.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class HopistalInfoResult(
    val code: Int,
    val message: String,
    @SerializedName("result")val result: List<HospitalInfo>
)

@Entity(tableName = "HospitalTable")
data class HospitalInfo(
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