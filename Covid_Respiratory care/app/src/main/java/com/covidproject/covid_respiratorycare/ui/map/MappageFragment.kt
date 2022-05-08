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
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.covidproject.covid_respiratorycare.data.Hospitaldata
import com.covidproject.covid_respiratorycare.ui.BaseFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment.newInstance
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.MarkerIcons
import androidx.fragment.app.FragmentManager
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.data.HospitalDatabase
import com.covidproject.covid_respiratorycare.databinding.FragmentMappageBinding
import com.covidproject.covid_respiratorycare.ui.Service.mapping.HospitalInfo
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingService
import com.covidproject.covid_respiratorycare.ui.Service.mapping.MappingView
import com.google.firebase.database.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MappageFragment : BaseFragment<FragmentMappageBinding>(R.layout.fragment_mappage), OnMapReadyCallback {

    lateinit var hospitalDB: HospitalDatabase
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

    private val mappingService = MappingService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMappageBinding.inflate(layoutInflater)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        isgpson.observe(this, androidx.lifecycle.Observer {
            isgpsondata = it
        })

        gpsthread.start()

        //현재위치 가져오기
        getLastKnownLocation()
        database = FirebaseDatabase.getInstance().reference
        hospitalDB = HospitalDatabase.getInstance(requireContext())!!
//        mappingService.setmappingView(this)
//        CoroutineScope(Dispatchers.IO).launch {
//            mappingService.getHospitalInfo()
//        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }



    @SuppressLint("LongLogTag")
    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
        binding.mapView.onDestroy()
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
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "주소 : $addr\nRAT가능여부 $ratPsblYn\nPCR가능여부$pcrPsblYn"
            }
        }

        val listener = Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker
            if (marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker)
                binding.mapNameTv.text = name
                binding.mapIsratTv.text = ratPsblYn.toString()
                binding.mapSelectLayoutBack.visibility = View.VISIBLE
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
                binding.mapSelectLayoutBack.visibility = View.INVISIBLE
            }
            true
        }

        if (naverMap!=null){
            naverMap.setOnMapClickListener { pointF, latLng ->
                infoWindow.close()
                binding.mapSelectLayoutBack.visibility = View.INVISIBLE
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
                                binding.mapSelectLayoutBack.visibility = View.INVISIBLE
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

    private fun SetHospitalDatabase() {
        hospitaldata = ArrayList<Hospitaldata>()
        //경우에 따라 서버의 업데이트된 값을 확인하는 대신 로컬 캐시의 값을 즉시 반환하고 싶을 수 있습니다.
        // 이 경우에는 addListenerForSingleValueEvent을 사용하여 로컬 디스크 캐시에서 데이터를 즉시 가져올 수 있습니다.
        database.child("response").child("body").child("items").child("item").addListenerForSingleValueEvent(object:
            ValueEventListener {
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
                for (i in hospitaldata){
                    if(i.YPosWgs84 == "null" || i.XPosWgs84 == "null"){
                        continue
                    }
                    if( latitude-0.05 <= i.YPosWgs84!!.toDouble()  && i.YPosWgs84!!.toDouble() <= latitude+0.05 &&
                        longitude-0.05 <= i.XPosWgs84!!.toDouble() && i.XPosWgs84!!.toDouble() <= longitude+0.05){
//                        setMarker(i.YPosWgs84!!.toDouble(),i.XPosWgs84!!.toDouble(),i.yadmNm,i.addr,i.ratPsblYn,i.pcrPsblYn)
                    }
                }
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
}