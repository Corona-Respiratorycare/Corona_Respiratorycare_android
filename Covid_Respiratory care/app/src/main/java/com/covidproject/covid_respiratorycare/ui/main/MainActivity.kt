package com.covidproject.covid_respiratorycare.ui.main

import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.*
import com.covidproject.covid_respiratorycare.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.ui.BaseActivity
import com.covidproject.covid_respiratorycare.ui.map.MapActivity
import com.covidproject.covid_respiratorycare.ui.map.MappageFragment
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var navHostFragment: NavHostFragment
    var isDrawerOpen = false

    override fun initView() {
//        tedPermission()
        // 처음에는 MainFragment
        supportFragmentManager.beginTransaction().add(R.id.main_fragment_container,MainFragment()
        ).commit()
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // 네비게이션 설정
//        binding.mainBottomnavi.setOnNavigationItemSelectedListener {
//            replaceFragment(
//                when (it.itemId) {
//                    R.id.mainFragment -> MainFragment()
//                    else -> MappageFragment()
//                }
//            )
//            true
//        }

        // Drawer 열리게 설정
//        binding.mainDrawerBt.setOnClickListener {
//            binding.mainDrawerlayout.openDrawer(Gravity.LEFT)
//        }

        val clickListener = View.OnClickListener {
            when(it.id){
                R.id.main_menu_age_tv -> mainViewModel.updateScrollLocation(Pair(0,1700))
                R.id.main_menu_board_tv ->mainViewModel.updateScrollLocation(Pair(0,0))
                R.id.main_menu_daily_tv ->mainViewModel.updateScrollLocation(Pair(0,610))
                R.id.main_menu_naver_news_tv -> mainViewModel.updateScrollLocation(Pair(0,2850))
                R.id.main_menu_coronahospital_tv -> startActivity(Intent(this,MapActivity::class.java))
            }
            binding.mainDrawerlayout.closeDrawer(Gravity.LEFT)
        }

        binding.mainMenuAgeTv.setOnClickListener(clickListener)
        binding.mainMenuNaverNewsTv.setOnClickListener(clickListener)
        binding.mainMenuBoardTv.setOnClickListener(clickListener)
        binding.mainMenuDailyTv.setOnClickListener(clickListener)
        binding.mainMenuCoronahospitalTv.setOnClickListener(clickListener)

        
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment_container,fragment).commit()
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

}
