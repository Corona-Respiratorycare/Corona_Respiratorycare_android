package com.covidproject.covid_respiratorycare.ui.Service.mapping

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class HopitalInfoResult(
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val HopitalInfoList: List<HospitalInfo>
)

@Entity(tableName = "HospitalTable")
data class HospitalInfo(
    @SerializedName("XPos")val XPos: Int,
    @SerializedName("XPosWgs84")val XPosWgs84: Double,
    @SerializedName("YPos")val YPos: Int,
    @SerializedName("YPosWgs84")val YPosWgs84: Double,
    @SerializedName("addr")val addr: String,
    @SerializedName("mgtStaDd")val mgtStaDd: String,
    @SerializedName("pcrPsblYn")val pcrPsblYn: Boolean,
    @SerializedName("ratPsblYn")val ratPsblYn: Boolean,
    @SerializedName("recuClCd")val recuClCd: Int,
    @SerializedName("rprtWorpClicFndtTgtYn")val rprtWorpClicFndtTgtYn: Boolean,
    @SerializedName("sgguNm")val sgguNm: String,
    @SerializedName("sidoNm")val sidoNm: String,
    @SerializedName("telno")val telno: String,
    @SerializedName("yadmNm")val yadmNm: String
){
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}

data class UpdateDataResult(
    @SerializedName("code")val code: Int,
    @SerializedName("message")val message: String,
    @SerializedName("result")val updatedate: UpdateDate
)

data class UpdateDate(
    @SerializedName("updated_date")val updated_date: String
)