package com.covidproject.covid_respiratorycare.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.data.HospitalDatabase
import com.covidproject.covid_respiratorycare.databinding.ActivityMapBinding
import com.covidproject.covid_respiratorycare.databinding.FragmentMappageBinding
import com.covidproject.covid_respiratorycare.ui.BaseActivity
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import android.content.pm.ResolveInfo
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat


class MapActivity : BaseActivity<ActivityMapBinding>(R.layout.activity_map), OnMapReadyCallback {

    lateinit var hospitalDB: HospitalDatabase
    private lateinit var naverMap: NaverMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Info 리스트
    val infoWindowlist = ArrayList<InfoWindow>()

    private lateinit var mapViewModel: MapViewModel
    private var isgpson: MutableLiveData<Boolean> = MutableLiveData()
    private var isgpsondata = false
    private val gpsthread = GpsThread()

    private val mappingService = MappingService()

    override fun initViewModel() {
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        binding.mapViewModel = mapViewModel
        binding.lifecycleOwner = this

        // 전화 열기
        mapViewModel.telEvent.eventObserve(this) { it ->
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + it.replace("-", ""))))
        }

        // 이벤트리스너로 네이버 맵 키기
        mapViewModel.naverAppEvent.eventObserve(this) { it->
            // 네이버 지도 url
            val url =
                "nmap://actionPath?parameter=value&appname={com.covidproject.covid_respiratorycare}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            // 인텐트 카테고리 브라우저를 사용해 설치된 앱 검색
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            val list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            // 설치되어 있지 않다면
            if (list == null || list.isEmpty()) {
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
//                        Uri.parse("nmap://search?query=${it}&appname=com.covidproject.covid_respiratorycare")
                        Uri.parse("nmap://route/public?dlat=${it.first}&dlng=${it.second}&dname=${it.third}&appname=com.covidproject.covid_respiratorycare")
                    )
                )
            }
        }

    }

    override fun initView() {
        tedPermission()
        binding.mapMapView.getMapAsync(this)
        binding.mapBackIv.setOnClickListener {
            finish()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        isgpson.observe(this, androidx.lifecycle.Observer {
            isgpsondata = it
        })

        gpsthread.start()

        //현재위치 가져오기
        getLastKnownLocation()
        hospitalDB = HospitalDatabase.getInstance(this)!!
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

    @SuppressLint("LongLogTag")
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
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.d(
                        "test : getLastKnownLoaction",
                        latitude.toString() + " " + longitude.toString()
                    )
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.mapMapView.onDestroy()
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0
        isgpson.value = true
    }

    //    i.telno,i.mgtStaDd,i.recuClCd
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
        var marker = Marker()
        marker.position = LatLng(y, x)
//        marker.setIcon(OverlayImage.fromResource(R.drawable.icon_hospital))
        marker.icon = MarkerIcons.BLACK
        marker.iconTintColor = Color.GREEN
        //눕혀도 정방향으로 보여지게
        marker.isIconPerspectiveEnabled = true

        //캡션
        marker.captionMinZoom = 0.0
        marker.captionMaxZoom = 16.0
        marker.captionText = name


        val listener = Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker
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
            mapViewModel.updateposition(y.toString(),x.toString())
            mapViewModel.updateispcr(if (pcrPsblYn) "가능" else "불가능")
            mapViewModel.updateisrat(if (ratPsblYn) "가능" else "불가능")
            mapViewModel.updatestartday("운영 시작 날짜 : " +  startday.substring(2..3)+"."
                    +startday.substring(4..5)+"."+startday.substring(6..7))
            mapViewModel.updatetelno(telno)
            mapViewModel.updateaddr(addr)

//            if (marker.infoWindow == null) {
//                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
////                infoWindow.open(marker)
//            } else {
//                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
////                infoWindow.close()
//            }
            true
        }


//        infoWindowlist.add(infoWindow)
        marker.onClickListener = listener

        //네이버맵 적용
        marker.map = naverMap
    }

    inner class GpsThread : Thread() {
        private var isRunning = true
        override fun run() {
            while (isRunning) {
                try {
                    sleep(1000)
                    if (isgpsondata) {
                        Handler(Looper.getMainLooper()).post {
                            val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
                            naverMap.moveCamera(cameraUpdate)
                            isRunning = false
                            val hospitaldata = hospitalDB.HospitalInfoDao().getallHospital()
                            Log.d("병원정보", hospitaldata.toString())
                            for (i in hospitaldata) {
                                if (latitude - 0.03 <= i.YPosWgs84 && i.YPosWgs84 <= latitude + 0.03 &&
                                    longitude - 0.03 <= i.XPosWgs84 && i.XPosWgs84 <= longitude + 0.03
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
                                for (i in infoWindowlist) {
                                    i.close()
                                }
                                binding.mapHospitalContainer.visibility = View.INVISIBLE
                            }
                        }
                    }
                } catch (e: Exception) {
                    isRunning = false
                    this.interrupt()
                }
            }
        }
    }
}