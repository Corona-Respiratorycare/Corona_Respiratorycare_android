package com.covidproject.covid_respiratorycare.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.data.HospitalDatabase
import com.covidproject.covid_respiratorycare.data.HospitalRepository
import com.covidproject.covid_respiratorycare.data.HospitalViewModel
import com.covidproject.covid_respiratorycare.data.ResultX
import com.covidproject.covid_respiratorycare.data.datastore.DataStoreHospitalUpdate
import com.covidproject.covid_respiratorycare.databinding.ActivitySplashBinding
import com.covidproject.covid_respiratorycare.ui.BaseActivity
//import com.covidproject.covid_respiratorycare.ui.Service.mapping.HospitalInfo
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingService
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingView
import com.covidproject.covid_respiratorycare.ui.Service.mapping.UpdateMapView
import com.covidproject.covid_respiratorycare.ui.main.MainActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash), MappingView,
    UpdateMapView {

    private val mappingService = MappingService()
    private lateinit var hospitalViewModel: HospitalViewModel
    private lateinit var hospitalUpdateManager: DataStoreHospitalUpdate

    override fun initView() {
//        tedPermission()
        binding.lifecycleOwner = this
        hospitalViewModel = ViewModelProvider(this).get(HospitalViewModel::class.java)
        hospitalUpdateManager = DataStoreHospitalUpdate(this)

        mappingService.setmappingView(this)
        mappingService.setupdateMapView(this)

        CoroutineScope(Dispatchers.IO).launch {
            mappingService.getUpdateInfo()
        }

    }

    override fun onMappingLoading() {
        binding.splashLoadingTv.text = "최신 정보 업데이트 중"
    }

    override fun onMappingSuccess(hopitalList: List<ResultX>) {
        hospitalViewModel.deleteAlldeleteAllHospital()
        for (i in hopitalList) {
            Log.d("병원", i.toString())
            hospitalViewModel.insert(i)
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onMappingFailure(code: Int, message: String) {

    }

    override fun onUpdateMapLoading() {
    }

    override fun onUpdateMapSuccess(date: String) {
//        val spf = getSharedPreferences("DateInfo",MODE_PRIVATE)
//        val spfdate = spf.getString("Update_date","no")
//        GlobalScope

        var tempdate = ""
        CoroutineScope(Dispatchers.IO).launch {
            hospitalUpdateManager.text.collect(){
                tempdate = it
            }
        }
        if (date == tempdate) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                binding.splashLoadingTv.text = "최신 정보 업데이트 완료"
            }, 1000)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                mappingService.getHospitalInfo()
                hospitalUpdateManager.setText(date)
            }
        }

    }

    override fun onUpdateMapFailure(code: Int, message: String) {
    }
}