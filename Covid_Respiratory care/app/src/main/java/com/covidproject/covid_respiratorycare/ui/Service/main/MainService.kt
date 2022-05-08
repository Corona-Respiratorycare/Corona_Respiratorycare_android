package com.covidproject.covid_respiratorycare.ui.Service.main

import android.util.Log
import com.covidproject.covid_respiratorycare.navernewsretrofit
import com.covidproject.covid_respiratorycare.openapiretrofit

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.lang.Exception
import kotlin.collections.ArrayList


class MainService {
    val mainService = openapiretrofit.create(MainRetrofitInterface::class.java)
    val naveNewsService = navernewsretrofit.create(MainRetrofitInterface::class.java)

    lateinit var mainInfoView : MainInfoView
    companion object {
        private const val ServiceKey = "uQEBqklQ8iRzL1OLrXwjYa6xIfWCRrOLfyo2HAr4hI8RvzDnTeWL5VqVJCYcIOYy+JqQBZSuD7hd86jJzep6/Q=="
        private const val NaverAPIClientId = "jwWWLVsAVg0CcNAxsR1Z"
        private const val NaverAPIClientSecret = "wZYJcW4dSD"
        private const val TAG = "MainService"
    }

    fun setInfoView(mainInfoView: MainInfoView) {
        this.mainInfoView = mainInfoView
    }
    suspend fun getCoronaNaverNews() {
        try{
            val result = naveNewsService.getCoronaNaverNews("코로나")
            mainInfoView.onNaverNewsSuccess(result.navernews)
        }catch (throwable : Throwable){
            Log.d(TAG,throwable.message.toString())
            mainInfoView.onInfoFailure("getCoronaNaverNews Failed"+throwable.message)
        }

    //        if(resultArray.size>=1){
//            mainInfoView.onInfoSuccess("getSeoulCovidMain",resultArray)
//        }else{
//        }
    }

    suspend fun getSeoulCovidMain(now:String) {
        val result = mainService.getSeoulCovidMain(
            ServiceKey,
            now.toInt(),now.toInt())
        val resultArray = xmlParse(result)
        if(resultArray.size>=1){
            mainInfoView.onInfoSuccess("getSeoulCovidMain",resultArray)
        }else{
            mainInfoView.onInfoFailure("getSeoulCovidMain Failed")
        }
    }

    suspend fun getSeoulCovidDaily(before :String, now:String) {

        val result = mainService.getSeoulCovidMain(
            ServiceKey,
            before.toInt(),now.toInt())

        val resultArray = xmlParse2(result)
        if(resultArray.size>=1){
            mainInfoView.onInfoSuccess("getSeoulCovidDaily",resultArray)
        }else{
            mainInfoView.onInfoFailure("getSeoulCovidDaily Failed")
        }

    }

    // 일일 확진자 그래프용 XMl 파싱 함수
    private fun xmlParse2(result: String) : ArrayList<String> {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(result))

        // 신규 확진자
        var incDec = false
        var incDec_num = ""
        // 서울인지 체크
        var isSeoul = false
        // 지역 명
        var gubun = false
        // 7일치 신규 확진자 배열
        var resultArray = ArrayList<String>()

        try {
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) { // 0
                } else if (eventType == XmlPullParser.START_TAG) { // 1 ...
                    if (xpp.name == "incDec") {
                        incDec = true
                    }else if (xpp.name == "gubun") {
                        gubun = true
                    }else if (xpp.name == "updateDt") {
                        if (isSeoul) {
                            resultArray.add(incDec_num)
                        }
                        isSeoul = false
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                    if (incDec) {
                        incDec_num = xpp.text
                        incDec = false
                    } else if (gubun) {
                        if (xpp.text == "서울") {
                            isSeoul = true
                        }
                        gubun = false
                    }
                }
                eventType = xpp.next()
            }
        } catch (e: Exception) {
            Log.e("Network APi Error", "getSeoulCovidMain2 Error")
        }
        return resultArray
    }

    // 전광판 용 XMl 파싱 함수
    private fun xmlParse(result: String) : ArrayList<String> {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(result))

        // 누적 확진자
        var defCnt = false
        var defCnt_num = ""
        // 신규 확진자
        var incDec = false
        var incDec_num = ""
        // 지역 확진자
        var localOccCnt = false
        var localOccCnt_num = ""
        // 외국 확진자
        var overFlowCnt = false
        var overFlowCnt_num = ""
        // 지역 명
        var gubun = false
        // 업데이트 날짜
        var stdDay = false
        var stdDay_content = ""

        // 서울인지 체크
        var isSeoul = false

        // 신규 확진자, 누적확진자, 지역 발생, 해외 유입, 업데이트 날짜
        var resultArray = ArrayList<String>()

        try {
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) { // 0
                } else if (eventType == XmlPullParser.START_TAG) { // 1 ...
                    if (xpp.name == "defCnt") {
                        defCnt = true
                    } else if (xpp.name == "incDec") {
                        incDec = true
                    } else if (xpp.name == "localOccCnt") {
                        localOccCnt = true
                    } else if (xpp.name == "overFlowCnt") {
                        overFlowCnt = true
                    } else if (xpp.name == "gubun") {
                        gubun = true
                    } else if (xpp.name == "stdDay") {
                        stdDay = true
                    } else if (xpp.name == "updateDt") {
                        if (isSeoul) {
                            resultArray.add(incDec_num)
                            resultArray.add(defCnt_num)
                            resultArray.add(localOccCnt_num)
                            resultArray.add(overFlowCnt_num)
                            resultArray.add(stdDay_content)
                        }
                        isSeoul = false
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                    if (defCnt) {
                        defCnt_num = xpp.text
                        defCnt = false
                    } else if (incDec) {
                        incDec_num = xpp.text
                        incDec = false
                    } else if (localOccCnt) {
                        localOccCnt_num = xpp.text
                        localOccCnt = false
                    } else if (overFlowCnt) {
                        overFlowCnt_num = xpp.text
                        overFlowCnt = false
                    } else if (stdDay) {
                        stdDay_content = xpp.text
                        stdDay = false
                    } else if (gubun) {
                        if (xpp.text == "서울") {
                            isSeoul = true
                        }
                        gubun = false
                    }
                }
                eventType = xpp.next()
            }
        } catch (e: Exception) {
            Log.e("Network APi Error", "getSeoulCovidMain Error")
        }
        return resultArray
    }


}