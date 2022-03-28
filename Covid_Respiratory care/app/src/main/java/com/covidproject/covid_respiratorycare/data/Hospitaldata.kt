package com.covidproject.covid_respiratorycare.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Hospitaldata(val index: Int,
                        val ratPsblYn: String,
                        val XPosWgs84:  String? = null,
                        val telno: String,
                        val YPosWgs84:  String? = null,
                        val pcrPsblYn: String,
                        val yadmNm: String,
                        val YPos: String? = null,
                        val rprtWorpClicFndtTgtYn: String,
                        val recuClCd:  String? = null,
                        val XPos: String? = null,
                        val sidoCdNm: String,
                        val addr: String,
                        val mgtStaDd: String, //운영시작일자
                        val sgguCdNm: String,
                        val ykihoEnc: String
)