package com.covidproject.covid_respiratorycare.ui.main

import android.graphics.Color
import android.os.Bundle
import com.covidproject.covid_respiratorycare.databinding.FragmentMainBinding
import com.covidproject.covid_respiratorycare.ui.BaseFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    override fun initAfterBinding() {
//        showBarChart()
        setWeek(binding.mainDailyGraph)
        binding.mainDailyGraph.setBackgroundColor(Color.WHITE)
        binding.mainDailyGraph.axisRight.isEnabled = false

        setPichart(binding.mainAbroadPiechart)

    }

    private fun setPichart(pieChart: PieChart) {
        val NoOfEmp : ArrayList<PieEntry> = ArrayList()

        NoOfEmp.add(PieEntry(945f,))
        NoOfEmp.add(PieEntry(1040f, 14251f))
        NoOfEmp.add(PieEntry(1133f, 2f))
        NoOfEmp.add(PieEntry(1240f, 3f))
        NoOfEmp.add(PieEntry(1369f, 4f))
        NoOfEmp.add(PieEntry(1487f, 5f))
        NoOfEmp.add(PieEntry(1501f, 6f))
        NoOfEmp.add(PieEntry(1645f, 7f))
        NoOfEmp.add(PieEntry(1578f, 8f))
        NoOfEmp.add(PieEntry(1695f, 9f))


        val dataSet = PieDataSet(NoOfEmp, "Number Of Employees")
        val year = ArrayList<Any>()

        year.add("2008")
        year.add("2009")
        year.add("2010")
        year.add("2011")
        year.add("2012")
        year.add("2013")
        year.add("2014")
        year.add("2015")
        year.add("2016")
        year.add("2017")

        val data = PieData(dataSet)

        pieChart.data = data
        dataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        pieChart.animateXY(1000, 1000)
    }

    private fun showBarChart() {
        val valueList = ArrayList<Double>()
        val entries: ArrayList<Entry> = ArrayList()
        val title = "확진자 수"

        //input data
        for (i in 1..8) {
            valueList.add(i * 100.1)
        }

        //fit the data into a bar
        for (i in 0 until valueList.size) {
            val barEntry = Entry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }
        val linChartDataset = LineDataSet(entries, title)
        val data = LineData(linChartDataset)
        binding.mainDailyGraph2.setData(data)
//        binding.mainDailyGraph2.setBackgroundColor(Color.WHITE)
//        var xAxis = binding.mainDailyGraph2.xAxis
//        xAxis.setLabelCount(10, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
//        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE); //x 축 표시에 대한 위치 설정
//        xAxis.mLabelWidth = 5
//        xAxis.mAxisMaximum = 7F

//        linChartDataset = LineDataSet(values, "Sample Data")
//        linChartDataset.setDrawIcons(false)
//        linChartDataset.enableDashedLine(10f, 5f, 0f)
//        linChartDataset.enableDashedHighlightLine(10f, 5f, 0f)
//        linChartDataset.setColor(Color.DKGRAY)
//        linChartDataset.setCircleColor(Color.DKGRAY)
//        linChartDataset.setLineWidth(1f)
//        linChartDataset.setCircleRadius(3f)
//        linChartDataset.setDrawCircleHole(false)
//        linChartDataset.setValueTextSize(9f)
//        linChartDataset.setDrawFilled(true)
//        linChartDataset.setFormLineWidth(1f)
//        linChartDataset.setFormLineDashEffect(DashPathEffect(floatArrayOf(10f, 5f), 0f))
//        linChartDataset.setFormSize(15f)
//
//        xAxis.mAxisMinimum = 1F
//        xAxis.setAvoidFirstLastClipping(false)
        binding.mainDailyGraph2.axisRight.isEnabled = false
        binding.mainDailyGraph2.axisLeft.isEnabled = false
        binding.mainDailyGraph2.invalidate()
    }

    private fun initBarDataSet(barDataSet: BarDataSet) {
        //Changing the color of the bar
        barDataSet.color = Color.parseColor("#304567")
        //Setting the size of the form in the legend
        barDataSet.formSize = 15f
        //showing the value of the bar, default true if not set
        barDataSet.setDrawValues(false)
        //setting the text size of the value of the bar
        barDataSet.valueTextSize = 12f
    }

    private fun setWeek(barChart: BarChart) {
        initBarChart(barChart)
        barChart.setScaleEnabled(false) //Zoom In/Out

        val valueList = ArrayList<Double>()
        val entries: ArrayList<BarEntry> = ArrayList()
        val title = "확진자 수"

        //input data
        for (i in 1..8) {
            valueList.add(i * 100.1)
        }

        //fit the data into a bar
        for (i in 0 until valueList.size) {
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
        }
        val barDataSet = BarDataSet(entries, title)
        val data = BarData(barDataSet)
        barChart.data = data
        barChart.invalidate()
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
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false)
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false)


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
        legend.textSize = 11f
        legend.textColor = Color.BLUE
        //setting the alignment of legend toward the chart
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        //setting the stacking direction of legend
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(true)
    }

}