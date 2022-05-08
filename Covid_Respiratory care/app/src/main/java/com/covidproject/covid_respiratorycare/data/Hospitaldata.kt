package com.covidproject.covid_respiratorycare.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Hospitaldata(val index: Int,
                        val ratPsblYn: String, // rat가능여부
                        val XPosWgs84:  String? = null,
                        val telno: String,  // 전화 번호
                        val YPosWgs84:  String? = null,
                        val pcrPsblYn: String,  // pcr 가능한지
                        val yadmNm: String,
                        val YPos: String? = null,
                        val rprtWorpClicFndtTgtYn: String, // 호흡기 전담 클리닉 여부
                        val recuClCd:  String? = null, // 요양종별코드 11:종합병원 / 21:병원 / 31:의원
                        val XPos: String? = null,
                        val sidoCdNm: String,
                        val addr: String, // 주소
                        val mgtStaDd: String,
                        val sgguCdNm: String,
                        val ykihoEnc: String
)