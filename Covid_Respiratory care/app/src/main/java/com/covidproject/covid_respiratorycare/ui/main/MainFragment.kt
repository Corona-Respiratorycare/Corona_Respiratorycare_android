package com.covidproject.covid_respiratorycare.ui.main

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import com.covidproject.covid_respiratorycare.databinding.FragmentMainBinding
import com.covidproject.covid_respiratorycare.ui.BaseFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.ui.Service.main.MainInfoView
import com.covidproject.covid_respiratorycare.ui.Service.main.MainService
import com.covidproject.covid_respiratorycare.ui.Service.main.NaverNews
import com.covidproject.covid_respiratorycare.ui.map.MapActivity

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet

import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main), MainInfoView {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainretrofitService: MainService
    val df: DateFormat = SimpleDateFormat("yyyyMMdd")
    private lateinit var navernewsRvAdapter : MainNaverNewsAdapter

    override fun initViewModel() {
        mainretrofitService = MainService()
        mainretrofitService.setInfoView(this)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        // 네이버 뉴스 Livedata가 업데이트 되면 어뎁터 설정
        mainViewModel.naverNews.observe(this, androidx.lifecycle.Observer {
            navernewsRvAdapter = mainViewModel.naverNews.value?.let { MainNaverNewsAdapter(it) }!!

            // 아이템 클릭 리스너 연결
            navernewsRvAdapter.setOnItemClickListener(object : MainNaverNewsAdapter.OnClickInterface{
                override fun onItemClick(v: View, news: NaverNews, pos: Int) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(news.link)))
                }
            })
            binding.mainNavernewsRv.adapter = navernewsRvAdapter
        })

        mainViewModel.scrollLocation.observe(this, androidx.lifecycle.Observer {
            binding.mainScrollview.scrollTo(it.first,it.second)
            Log.d(TAG,"클릭됨")
        })
        
    }

    override fun initView() {
        // 날짜 계산
        val cal = Calendar.getInstance()
        cal.time = Date()
        val now = df.format(cal.time).toString()
        cal.add(Calendar.DATE, -7)
        val before = df.format(cal.time).toString()

        // 일일확진자, 전공표 데이터 불러오기
        CoroutineScope(Dispatchers.IO).launch {
            mainretrofitService.getSeoulCovidMain(now)
            mainretrofitService.getSeoulCovidDaily(before, now)
            mainretrofitService.getCoronaNaverNews()
        }
        
        // RecyclerView 레이아웃 매니저 설정
        val lm = LinearLayoutManager(requireContext())
        binding.mainNavernewsRv.layoutManager = lm

        binding.mainFindHospitalTv.setOnClickListener {
            startActivity(Intent(requireContext(),MapActivity::class.java))
        }

        // 네이버 크롤링
        val crollingUrl = "https://www.seoul.go.kr/coronaV/coronaStatus.do"
        CoroutineScope(Dispatchers.IO).launch {
            // URL 웹사이트에 있는 html 코드를 다 끌어오기, Jsoup 라이브러리 사용
            val doc: Document = Jsoup.connect(crollingUrl).get()
            // cssQuery로 원하는 부분 가져오기
            val temele: Elements =
                doc.select(".table-scroll .tstyle-status tbody tr td") 
            //빼온 값 null체크
            val isEmpty = temele.isEmpty()
            //null값이 아니면 크롤링 실행
            if (!isEmpty) {
                val covidnumage = ArrayList<String>()
                for (i in 1..8) {
                    covidnumage.add(temele[i].text())
                }
                requireActivity().runOnUiThread {
                    setPieChar(covidnumage)
                }
            }
        }
    }

    override fun onInfoLoading() {
    }

    override fun onInfoSuccess(funcname: String, data: ArrayList<String>) {
        val activity = activity
        if (activity != null) {
            // Dipatcher.IO 여서 UI 못 만짐
            requireActivity().runOnUiThread {
                // 함수이름에 따라 다른 뷰 업데이트 실행
                if (funcname == "getSeoulCovidMain") {
                    mainViewModel.updatedefCnt(data[0])
                    mainViewModel.updateincCnt(data[1])
                    mainViewModel.updatelocalCnt(data[2])
                    mainViewModel.updateoverFlowCnt(data[3])
                    mainViewModel.updatestdDay(data[4])
                }
                if (funcname == "getSeoulCovidDaily") {
                    setWeek(binding.mainDailyGraph, data)
                }
            }
        }
    }

    override fun onNaverNewsSuccess(data: List<NaverNews>) {
        val activity = activity
        // activity가 살아 있으면
        if (activity != null) {
            requireActivity().runOnUiThread {
                // 데이터를 바꾸는 것도 IO에서 안되나 봄
                mainViewModel.updatenaverNews(data)
            }
        }
    }

    override fun onInfoFailure(message: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, message)
        }
    }

    private fun setPieChar(agearr: ArrayList<String>) {
        binding.mainDailyPiechart.setUsePercentValues(true)
        binding.mainDailyPiechart.getDescription().setEnabled(false)
        binding.mainDailyPiechart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.mainDailyPiechart.setDragDecelerationFrictionCoef(0.95f)
        binding.mainDailyPiechart.setDrawHoleEnabled(false)
        binding.mainDailyPiechart.setHoleColor(Color.WHITE)
        binding.mainDailyPiechart.setTransparentCircleRadius(61f)

        val yValues = ArrayList<PieEntry>()

        yValues.add(PieEntry(agearr[0].replace(",", "").toFloat(), "10세 이하"))
        yValues.add(PieEntry(agearr[1].replace(",", "").toFloat(), "10대"))
        yValues.add(PieEntry(agearr[2].replace(",", "").toFloat(), "20대"))
        yValues.add(PieEntry(agearr[3].replace(",", "").toFloat(), "30대"))
        yValues.add(PieEntry(agearr[4].replace(",", "").toFloat(), "40대"))
        yValues.add(PieEntry(agearr[5].replace(",", "").toFloat(), "50대"))
        yValues.add(PieEntry(agearr[6].replace(",", "").toFloat(), "60대"))
        yValues.add(PieEntry(agearr[7].replace(",", "").toFloat(), "70세 이상"))

//        val description = Description()
//        description.text = "세계 국가" //라벨
//        description.textSize = 15f
//        binding.mainDailyPiechart.setDescription(description)

        binding.mainDailyPiechart.animateY(1000) //애니메이션

        val dataSet = PieDataSet(yValues, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 10f
        val COLORS_BLUE = intArrayOf(
            Color.rgb(64, 116, 220), Color.rgb(37, 123, 203), Color.rgb(24, 113, 195),
            Color.rgb(58, 140, 216), Color.rgb(21, 84, 144), Color.rgb(99, 138, 219),
            Color.rgb(133, 214, 245)
        )
        dataSet.setColors(*COLORS_BLUE)
        val data = PieData(dataSet)
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.WHITE)

        binding.mainDailyPiechart.setData(data)
    }

    private fun setWeek(barChart: BarChart, resultdata: ArrayList<String>) {
        initBarChart(barChart)
        barChart.setScaleEnabled(false) //Zoom In/Out

        val valueList = ArrayList<Double>()
        val entries: ArrayList<BarEntry> = ArrayList()
        val title = "확진자 수"

        for (i in resultdata) {
            valueList.add(i.toDouble())
        }

        // 바엔트리에 데이터 채우기
        for (i in 0 until valueList.size) {
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, title)
        barDataSet.color = Color.parseColor("#91ABE1")
        val data = BarData(barDataSet)
        barChart.data = data
        barChart.invalidate()
        binding.mainDailyGraph.setBackgroundColor(Color.WHITE)
        binding.mainDailyGraph.axisRight.isEnabled = false
    }

    private fun initBarChart(barChart: BarChart) {
        //hiding the grey background of the chart, default false if not set
        barChart.setDrawGridBackground(false)
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false)
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false)

        //remove the description label text located at the lower right corner
        val description = Description()
        description.setEnabled(false)
        barChart.setDescription(description)

        //X, Y 바의 애니메이션 효과
        barChart.animateY(1000)
        barChart.animateX(1000)

        //바텀 좌표 값
        val xAxis: XAxis = barChart.getXAxis()
        //change the position of x-axis to the bottom
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //set the horizontal distance of the grid line
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK
        xAxis.textSize = 12f
        //hiding the x-axis line, default true if not set
        //xAxis.setDrawAxisLine(false)
        //hiding the vertical grid lines, default true if not set
        val week = ArrayList<String>()
        val cal = Calendar.getInstance()
        cal.time = Date()
        val df: DateFormat = SimpleDateFormat("yyyy.MM.dd")
        for (i in 0..7) {
            // 날짜를 05.02 형식으로 바꾸어서 week에 더하기
            week.add(df.format(cal.time).subSequence(5, 10).toString())
            cal.add(Calendar.DATE, -1)
        }
        // xAxis값 넣기
        xAxis.valueFormatter = IndexAxisValueFormatter(week.reversed())

        //좌측 값 hiding the left y-axis line, default true if not set
        val leftAxis: YAxis = barChart.getAxisLeft()
        leftAxis.setDrawAxisLine(false)
        leftAxis.textColor = Color.BLACK

        //우측 값 hiding the right y-axis line, default true if not set
        val rightAxis: YAxis = barChart.getAxisRight()
        rightAxis.setDrawAxisLine(true)
        rightAxis.textColor = Color.BLACK

        //바차트의 타이틀
        val legend: Legend = barChart.getLegend()
        //setting the shape of the legend form to line, default square shape
        legend.form = Legend.LegendForm.LINE
        //setting the text size of the legend
        legend.textSize = 12f
        legend.textColor = android.graphics.Color.BLACK
        //setting the alignment of legend toward the chart
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        //setting the stacking direction of legend
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(true)
    }

    companion object {
        private const val TAG = "MainFragment"
    }


}