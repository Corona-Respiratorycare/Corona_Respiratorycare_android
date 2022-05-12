package com.covidproject.covid_respiratorycare.ui.splash

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.data.HospitalDatabase
import com.covidproject.covid_respiratorycare.data.ResultX
import com.covidproject.covid_respiratorycare.databinding.ActivitySplashBinding
import com.covidproject.covid_respiratorycare.ui.BaseActivity
//import com.covidproject.covid_respiratorycare.ui.Service.mapping.HospitalInfo
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingService
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingView
import com.covidproject.covid_respiratorycare.ui.Service.mapping.UpdateMapView
import com.covidproject.covid_respiratorycare.ui.main.MainActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash), MappingView, UpdateMapView {

    private val mappingService = MappingService()
    lateinit var hospitalDB: HospitalDatabase

    override fun initView() {
//        tedPermission()
        hospitalDB = HospitalDatabase.getInstance(this)!!
        mappingService.setmappingView(this)
        mappingService.setupdateMapView(this)

//        val spf = getSharedPreferences("DateInfo",MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = spf?.edit()!!
//        editor.putString("Update_date","asdasd")
//        editor.apply()

        CoroutineScope(Dispatchers.IO).launch {
             mappingService.getUpdateInfo()
        }

    }

    override fun onMappingLoading() {
        binding.splashLoadingTv.text = "최신 정보 업데이트 중"
    }

    override fun onMappingSuccess(hopitalList: List<ResultX>) {
        hospitalDB.HospitalInfoDao().deleteAllHospital()
        for(i in hopitalList){
            Log.d("병원",i.toString())
            hospitalDB.HospitalInfoDao().insert(i)
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onMappingFailure(code: Int, message: String) {

    }

    override fun onUpdateMapLoading() {
//        binding.splashLoadingTv.text = "최신 정보 확인 중"
    }

    override fun onUpdateMapSuccess(date: String) {
        val spf = getSharedPreferences("DateInfo",MODE_PRIVATE)
        val spfdate = spf.getString("Update_date","no")
        Log.d("Result","1")
        if(date == spfdate){
            Log.d("Result","2")
//            CoroutineScope(Dispatchers.IO).launch {
//                mappingService.getHospitalInfo()
//            }
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                binding.splashLoadingTv.text = "최신 정보 업데이트 완료"
            }, 1000)
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                mappingService.getHospitalInfo()
            }
            val editor: SharedPreferences.Editor = spf.edit()
            editor.putString("Update_date",date)
            editor.apply()
            Log.d("Result","3")
        }
    }

    override fun onUpdateMapFailure(code: Int, message: String) {
    }
}