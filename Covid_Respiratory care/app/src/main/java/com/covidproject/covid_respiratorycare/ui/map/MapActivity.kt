package com.covidproject.covid_respiratorycare.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.data.HospitalDatabase
import com.covidproject.covid_respiratorycare.data.HospitalViewModel
import com.covidproject.covid_respiratorycare.databinding.ActivityMapBinding
import com.covidproject.covid_respiratorycare.ui.BaseActivity
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.*

class MapActivity : BaseActivity<ActivityMapBinding>(R.layout.activity_map), OnMapReadyCallback {

//    lateinit var hospitalDB: HospitalDatabase
    private lateinit var naverMap: NaverMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var usermarker = Marker()

    lateinit var userpositionThread : UserPositionThread
    private lateinit var mapViewModel: MapViewModel
    private lateinit var hostpitalViewModel: HospitalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        binding.mapMapView.onCreate(savedInstanceState)
        initViewModel()
        initView()
        initListener()
    }

    override fun initViewModel() {
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        hostpitalViewModel = ViewModelProvider(this)[HospitalViewModel::class.java]
        binding.mapViewModel = mapViewModel
        binding.lifecycleOwner = this
//        userpositionThread = UserPositionThread()
        // 네이버 지도 동기화
        binding.mapMapView.getMapAsync(this)

        // 현재위치 가져오기
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // DB인스턴트 넣기
//        hospitalDB = HospitalDatabase.getInstance(this)!!

        // 전화 열기
        mapViewModel.telEvent.eventObserve(this) { it ->
            // it == String
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + it.replace("-", ""))))
        }

        // 이벤트리스너로 네이버 맵 키기
        mapViewModel.naverAppEvent.eventObserve(this) { it ->
            // 네이버 지도 url
            val url =
                "nmap://actionPath?parameter=value&appname={com.covidproject.covid_respiratorycare}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            // 인텐트 카테고리 브라우저를 사용해 설치된 앱 검색
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            val list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            // 설치되어 있지 않다면
            if (list.isEmpty()) {
                // 마켓으로 이동
                this.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.nhn.android.nmap")
                    )
                )
            } else {
                // URL 스킴 으로 네이버지도에서 검색
                this.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("nmap://route/public?dlat=${it.first}&dlng=${it.second}&dname=${it.third}&appname=com.covidproject.covid_respiratorycare")
                    )
                )
            }
        }

        // 유저포지션 업데이트 되면 그곳으로 카메라 바꾸고 마커찍기
        mapViewModel.userposition.observe(this,{
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(it.first, it.second))
            naverMap.moveCamera(cameraUpdate)
            setUserPositionMarker(it.first,it.second)
            setMarkerbyUserPosition()
        })

        // 사용자 위치 2초마다 바꾸는 쓰레드
//        userpositionThread.start()

    }

    fun setMarkerbyUserPosition() {
        try {
            runOnUiThread {
//                val hospitaldata = hospitalDB.HospitalInfoDao().getallHospital()
//                Log.d(TAG,hospitaldata.toString())
                for (i in hostpitalViewModel.getAll().value!!) {
                    if (mapViewModel.userposition.value!!.first - 0.03 <= i.YPosWgs84 && i.YPosWgs84 <= mapViewModel.userposition.value!!.first + 0.03 &&
                        mapViewModel.userposition.value!!.second - 0.03 <= i.XPosWgs84 && i.XPosWgs84 <= mapViewModel.userposition.value!!.second + 0.03
                    ) {
                        setMarker(
                            i.YPosWgs84,
                            i.XPosWgs84,
                            i.yadmNm,
                            i.addr,
                            i.ratPsblYn,
                            i.pcrPsblYn,
                            i.telno,
                            i.mgtStaDd,
                            i.recuClCd
                        )
                    }
                }
                naverMap.setOnMapClickListener { pointF: PointF, latLng: LatLng ->
                    binding.mapHospitalContainer.visibility = View.INVISIBLE
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun initView() {
        tedPermission()
        binding.mapBackIv.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapMapView.onResume()
    }

    private fun tedPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}
            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Log.d("test0", "설정에서 권한을 허가 해주세요.")
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapMapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapMapView.onLowMemory()
    }

    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d(TAG,"${location.latitude} + ${location.longitude}")
                        mapViewModel.updateuserposition(Pair(location.latitude,location.longitude))
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapMapView.onDestroy()
//        userpositionThread.interrupt()
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0
        getLastKnownLocation()
    }

    fun setUserPositionMarker(y : Double, x: Double){
        usermarker.map = null
        usermarker.position = LatLng(y, x)
        usermarker.icon = OverlayImage.fromResource(R.drawable.icon_user)
        //눕혀도 정방향으로 보여지게
        usermarker.isIconPerspectiveEnabled = true
        //캡션
        usermarker.captionMinZoom = 0.0
        usermarker.captionMaxZoom = 16.0
        usermarker.map = naverMap
    }


    fun setMarker(
        y: Double,
        x: Double,
        name: String,
        addr: String,
        ratPsblYn: Boolean,
        pcrPsblYn: Boolean,
        telno: String,
        startday: String,
        hospitalcode: Int
    ) {
        val marker = Marker()
        marker.position = LatLng(y, x)
        marker.icon = OverlayImage.fromResource(R.drawable.icon_hospital_marker)
//        marker.icon = MarkerIcons.BLACK
//        marker.iconTintColor = Color.GREEN
        //눕혀도 정방향으로 보여지게
        marker.isIconPerspectiveEnabled = true

        //캡션
        marker.captionMinZoom = 0.0
        marker.captionMaxZoom = 16.0
        marker.captionText = name

        val listener = Overlay.OnClickListener { overlay ->
            binding.mapHospitalContainer.visibility = View.VISIBLE
            mapViewModel.updatehospitalname(name)
            // 11:종합병원 / 21:병원 / 31:의원
            mapViewModel.updatehospitalcode(
                when (hospitalcode) {
                    11 -> {
                        "종합병원"
                    }
                    21 -> {
                        "병원"
                    }
                    else -> {
                        "의원"
                    }
                }
            )
            mapViewModel.updateposition(y.toString(), x.toString())
            mapViewModel.updateispcr(if (pcrPsblYn) "가능" else "불가능")
            mapViewModel.updateisrat(if (ratPsblYn) "가능" else "불가능")
            mapViewModel.updatestartday(
                "운영 시작 날짜 : " + startday.substring(2..3) + "."
                        + startday.substring(4..5) + "." + startday.substring(6..7)
            )
            mapViewModel.updatetelno(telno)
            mapViewModel.updateaddr(addr)

            // 좌표계산
            val locationA = Location("Point A")
            locationA.longitude = y
            locationA.latitude = x
            val locationB = Location("Point B")
            locationB.longitude = mapViewModel.userposition.value!!.first
            locationB.latitude = mapViewModel.userposition.value!!.second
            if(locationA.distanceTo(locationB).toInt() >= 1000){
                mapViewModel.updatedist(String.format("%.1f", (locationA.distanceTo(locationB) / 1000)) + "km")
            }else{
                mapViewModel.updatedist(locationA.distanceTo(locationB).toInt().toString() + "m")
            }
            true
        }

        marker.onClickListener = listener
        marker.map = naverMap
    }

    inner class UserPositionThread : Thread() {
        override fun run() {
            while(true){
                try{
                    sleep(2000)
                    getLastKnownLocation()
                }catch (e : java.lang.Exception){
                    break
                }
            }
        }
    }

    companion object {
        private const val TAG = "MapActivity"
    }
}