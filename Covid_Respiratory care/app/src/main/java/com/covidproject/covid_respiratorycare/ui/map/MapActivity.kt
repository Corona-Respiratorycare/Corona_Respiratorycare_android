package com.covidproject.covid_respiratorycare.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.data.HospitalDatabase
import com.covidproject.covid_respiratorycare.data.Hospitaldata
import com.covidproject.covid_respiratorycare.databinding.ActivityMapBinding
import com.covidproject.covid_respiratorycare.databinding.FragmentMappageBinding
import com.covidproject.covid_respiratorycare.ui.BaseActivity
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

class MapActivity : BaseActivity<ActivityMapBinding>(R.layout.activity_map), OnMapReadyCallback {

    lateinit var hospitalDB: HospitalDatabase
    private lateinit var naverMap : NaverMap
    private var latitude : Double = 0.0
    private var longitude  : Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Info 리스트
    val infoWindowlist = ArrayList<InfoWindow>()

    private var isgpson: MutableLiveData<Boolean> = MutableLiveData()
    private var isgpsondata = false
    private val gpsthread = GpsThread()

    private val mappingService = MappingService()

    override fun initView() {
        binding.mapMapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        isgpson.observe(this, androidx.lifecycle.Observer {
            isgpsondata = it
        })

        gpsthread.start()

        //현재위치 가져오기
        getLastKnownLocation()

        hospitalDB = HospitalDatabase.getInstance(this)!!
//        mappingService.setmappingView(this)
//        CoroutineScope(Dispatchers.IO).launch {
//            mappingService.getHospitalInfo()
//        }

    }

    override fun onStart() {
        super.onStart()
        binding.mapMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapMapView.onResume()
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
            .addOnSuccessListener { location->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.d("test : getLastKnownLoaction",latitude.toString()+" "+longitude.toString())
//                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude,longitude))
//                    naverMap.moveCamera(cameraUpdate)
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

    fun setMarker(y : Double, x : Double,name : String,addr : String, ratPsblYn:Boolean, pcrPsblYn:Boolean)
    {
        var marker = Marker()
        marker.position = LatLng(y,x)
//        marker.setIcon(OverlayImage.fromResource(R.drawable.icon_hospital))
        marker.setIcon(MarkerIcons.BLACK)
        marker.setIconTintColor(Color.GREEN)
        //눕혀도 정방향으로 보여지게
        marker.setIconPerspectiveEnabled(true)

        //캡션
        marker.setCaptionMinZoom(0.0)
        marker.setCaptionMaxZoom(16.0)
        marker.setCaptionText(name)

        marker.setOnClickListener {
            Log.d("Test", addr)
            true
        }

        //정보창
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "주소 : $addr\nRAT가능여부 $ratPsblYn\nPCR가능여부$pcrPsblYn"
            }
        }

        val listener = Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker
            if (marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker)
//                binding.mapNameTv.text = name
//                binding.mapIsratTv.text = ratPsblYn.toString()
//                binding.mapSelectLayoutBack.visibility = View.VISIBLE
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
//                binding.mapSelectLayoutBack.visibility = View.INVISIBLE
            }
            true
        }

        if (naverMap!=null){
            naverMap.setOnMapClickListener { pointF, latLng ->
                infoWindow.close()
//                binding.mapSelectLayoutBack.visibility = View.INVISIBLE
            }

            infoWindowlist.add(infoWindow)
            marker.onClickListener = listener

            //네이버맵 적용
            marker.map = naverMap
        }
    }

    inner class GpsThread : Thread(){
        private var isRunning = true
        override fun run() {
            while(isRunning){
                try {
                    sleep(1000)
                    if(isgpsondata){
                        Handler(Looper.getMainLooper()).post {
                            val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude,longitude))
                            naverMap.moveCamera(cameraUpdate)
                            isRunning = false
                            var hospitaldata = hospitalDB.HospitalInfoDao().getallHospital()
                            Log.d("병원정보",hospitaldata.toString())
                            for (i in hospitaldata){
                                if( latitude-0.03 <= i.YPosWgs84  && i.YPosWgs84 <= latitude+0.03 &&
                                    longitude-0.03 <= i.XPosWgs84 && i.XPosWgs84 <= longitude+0.03){
                                    setMarker(i.YPosWgs84,i.XPosWgs84,i.yadmNm,i.addr,i.ratPsblYn,i.pcrPsblYn)
                                }
                            }
                            naverMap.setOnMapClickListener { pointF: PointF, latLng: LatLng ->
                                for (i in infoWindowlist){
                                    i.close()
                                }
//                                binding.mapSelectLayoutBack.visibility = View.INVISIBLE
                            }
                        }
                    }
                }catch (e : Exception){
                    isRunning = false
                    this.interrupt()
                }
            }
        }
    }
}