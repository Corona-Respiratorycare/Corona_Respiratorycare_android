package com.covidproject.covid_respiratorycare.ui.Service.mapping

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//data class HopitalInfoResult(
//    @SerializedName("code")val code: Int,
//    @SerializedName("message")val message: String,
//    @SerializedName("result")val HopitalInfoList: List<HospitalInfo>
//)

//@Entity(tableName = "HospitalTable")
//data class HospitalInfo(
//    @SerializedName("index")val index: Int,
//    @SerializedName("XPosWgs84")val XPosWgs84: Double,
//    @SerializedName("YPosWgs84")val YPosWgs84: Double,
//    @SerializedName("addr")val addr: String,
//    @SerializedName("mgtStaDd")val mgtStaDd: String, // 이건뭐지
//    @SerializedName("pcrPsblYn")val pcrPsblYn: Boolean, // pcr 가능여부
//    @SerializedName("ratPsblYn")val ratPsblYn: Boolean, // rat 가능여부
//    @SerializedName("recuClCd")val recuClCd: Int, // 요양종별코드 11, 21, 31
//    @SerializedName("telno")val telno: String, // 전화번호
//    @SerializedName("yadmNm") val yadmNm: String // 병원 이름
//){
//    @PrimaryKey(autoGenerate = true) var id : Int = 0
//}

data class UpdateDataResult(
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val updatedate: UpdateDate
)

data class UpdateDate(
    @SerializedName("updated_date")val updated_date: String
)