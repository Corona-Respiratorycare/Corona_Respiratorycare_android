package com.covidproject.covid_respiratorycare

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.covidproject.covid_respiratorycare.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.covidproject.covid_respiratorycare.data.Hospitaldata
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var naverMap : NaverMap
    private var latitude : Double = 0.0
    private var longitude  : Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //파이어베이스 접근하기 위한 객체
    private lateinit var database: DatabaseReference
    //전체 Count 및 Database 버젼 관리
    private var totalcount : Int = 0
    //임시 데이터리스트
    private lateinit var hospitaldata: ArrayList<Hospitaldata>
    //Info 리스트
    val infoWindowlist = ArrayList<InfoWindow>()

    private var isgpson: MutableLiveData<Boolean> = MutableLiveData()
    private var isgpsondata = false
    private val gpsthread = GpsThread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // LiveData의 value의 변경을 감지하고 호출
        isgpson.observe(this, androidx.lifecycle.Observer {
            isgpsondata = it
        })
        gpsthread.start()

        tedPermission()
        binding = ActivityMainBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        database = FirebaseDatabase.getInstance().reference

        //현재위치 가져오기
        getLastKnownLocation()

        //토탈카운트 들고오기
        database.child("response").child("body").child("totalCount").get().addOnSuccessListener {
            totalcount = Integer.parseInt(it.value as String)
            SetHospitalDatabase()
        }.addOnFailureListener{
        }

        setContentView(binding.root)
    }

    private fun SetHospitalDatabase() {
        hospitaldata = ArrayList<Hospitaldata>()
        //경우에 따라 서버의 업데이트된 값을 확인하는 대신 로컬 캐시의 값을 즉시 반환하고 싶을 수 있습니다.
        // 이 경우에는 addListenerForSingleValueEvent을 사용하여 로컬 디스크 캐시에서 데이터를 즉시 가져올 수 있습니다.
        database.child("response").child("body").child("items").child("item").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(it: DataSnapshot) {
                for (i in 0 until totalcount) {
                    hospitaldata.add(
                        Hospitaldata(
                            Integer.parseInt(it.child(i.toString()).child("rnum").value.toString()),
                            it.child(i.toString()).child("ratPsblYn").value.toString(),
                            it.child(i.toString()).child("XPosWgs84").value.toString(),
                            it.child(i.toString()).child("telno").value.toString(),
                            it.child(i.toString()).child("YPosWgs84").value.toString(),
                            it.child(i.toString()).child("pcrPsblYn").value.toString(),
                            it.child(i.toString()).child("yadmNm").value.toString(),
                            it.child(i.toString()).child("YPos").value.toString(),
                            it.child(i.toString()).child("rprtWorpClicFndtTgtYn").value.toString(),
                            it.child(i.toString()).child("recuClCd").value.toString(),
                            it.child(i.toString()).child("XPos").value.toString(),
                            it.child(i.toString()).child("sidoCdNm").value.toString(),
                            it.child(i.toString()).child("addr").value.toString(),
                            it.child(i.toString()).child("mgtStaDd").value.toString(),
                            it.child(i.toString()).child("sgguCdNm").value.toString(),
                            it.child(i.toString()).child("ykihoEnc").value.toString(),
                        )
                    )
                }
//                1) 위도만 0.01바꾸었을 경우 거리  1110m 정도 바뀜 latitude 위도
//                2) 경도만 0.01바꾸었을 경우 거리  890m 정도 바뀜
//                val startTime = System.currentTimeMillis()
                for (i in hospitaldata){
                    if(i.YPosWgs84 == "null" || i.XPosWgs84 == "null"){
                        continue
                    }
                    if( latitude-0.05 <= i.YPosWgs84!!.toDouble()  && i.YPosWgs84!!.toDouble() <= latitude+0.05 &&
                        longitude-0.05 <= i.XPosWgs84!!.toDouble() && i.XPosWgs84!!.toDouble() <= longitude+0.05){
                        setMarker(i.YPosWgs84!!.toDouble(),i.XPosWgs84!!.toDouble(),i.yadmNm,i.addr,i.ratPsblYn,i.pcrPsblYn)
                    }
                }
//                val endTime = System.currentTimeMillis()
//                Log.d("test",startTime.toString()+"   "+endTime.toString())
                naverMap.setOnMapClickListener { pointF: PointF, latLng: LatLng ->
                    for (i in infoWindowlist){
                        i.close()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("test", "Error getting data"+error.toString())
            }
        })

//        database.child("response").child("body").child("items").child("item").get()
//            .addOnSuccessListener { it ->
//                for (i in 0 until totalcount) {
//                    hospitaldata.add(
//                        Hospitaldata(
//                            Integer.parseInt(it.child(i.toString()).child("rnum").value.toString()),
//                            it.child(i.toString()).child("ratPsblYn").value.toString(),
//                            it.child(i.toString()).child("XPosWgs84").value.toString(),
//                            it.child(i.toString()).child("telno").value.toString(),
//                            it.child(i.toString()).child("YPosWgs84").value.toString(),
//                            it.child(i.toString()).child("pcrPsblYn").value.toString(),
//                            it.child(i.toString()).child("yadmNm").value.toString(),
//                            it.child(i.toString()).child("YPos").value.toString(),
//                            it.child(i.toString()).child("rprtWorpClicFndtTgtYn").value.toString(),
//                            it.child(i.toString()).child("recuClCd").value.toString(),
//                            it.child(i.toString()).child("XPos").value.toString(),
//                            it.child(i.toString()).child("sidoCdNm").value.toString(),
//                            it.child(i.toString()).child("addr").value.toString(),
//                            it.child(i.toString()).child("mgtStaDd").value.toString(),
//                            it.child(i.toString()).child("sgguCdNm").value.toString(),
//                            it.child(i.toString()).child("ykihoEnc").value.toString(),
//                        )
//                    )
//                }
//            }.addOnFailureListener {
//                Log.e("test", "Error getting data", it)
//            }
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
                    Log.d("test : getLastKnownLoaction",latitude.toString()+longitude.toString())
//                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude,longitude))
//                    naverMap.moveCamera(cameraUpdate)
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
//        naverMap.maxZoom = 18.0
//        naverMap.
        naverMap.minZoom = 10.0
        isgpson.value = true
    }

    fun setMarker(y : Double, x : Double,name : String,addr : String, ratPsblYn:String, pcrPsblYn:String)
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
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }
            true
        }
        naverMap.setOnMapClickListener { pointF, latLng ->
            infoWindow.close()
        }
        infoWindowlist.add(infoWindow)
        marker.onClickListener = listener

        //네이버맵 적용
        marker.map = naverMap
    }

    private fun tedPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {}
            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Log.d("test0","설정에서 권한을 허가 해주세요.")
                finish()
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
