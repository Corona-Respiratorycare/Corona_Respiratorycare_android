package com.covidproject.covid_respiratorycare.ui.splash

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.covidproject.covid_respiratorycare.data.HospitalDatabase
import com.covidproject.covid_respiratorycare.databinding.ActivitySplashBinding
import com.covidproject.covid_respiratorycare.ui.BaseActivity
import com.covidproject.covid_respiratorycare.ui.Service.mapping.HospitalInfo
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingService
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingView
import com.covidproject.covid_respiratorycare.ui.Service.mapping.UpdateMapView
import com.covidproject.covid_respiratorycare.ui.main.MainActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate), MappingView, UpdateMapView {

    private val mappingService = MappingService()
    lateinit var hospitalDB: HospitalDatabase

    override fun initAfterBinding() {
        tedPermission()
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

    }

    override fun onMappingSuccess(hopitalList: List<HospitalInfo>) {
        hospitalDB.HospitalInfoDao().deleteAllHospital()
        for(i in hopitalList){
            hospitalDB.HospitalInfoDao().insert(i)
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onMappingFailure(code: Int, message: String) {

    }

    private fun tedPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}
            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Log.d("test0","설정에서 권한을 허가 해주세요.")
                //todo
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("서비스 사용을 위해서 몇가지 권한이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 설정할 수 있습니다.")
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .check()
    }

    override fun onUpdateMapLoading() {
    }

    override fun onUpdateMapSuccess(date: String) {
        val spf = getSharedPreferences("DateInfo",MODE_PRIVATE)
        val spfdate = spf.getString("Update_date","no")
        if(date == spfdate){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }else{
            val editor: SharedPreferences.Editor = spf?.edit()!!
            editor.putString("Update_date",date)
            editor.apply()
            CoroutineScope(Dispatchers.IO).launch {
                mappingService.getHospitalInfo()
            }
        }
    }

    override fun onUpdateMapFailure(code: Int, message: String) {
    }
}