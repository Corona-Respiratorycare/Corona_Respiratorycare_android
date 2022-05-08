package com.covidproject.covid_respiratorycare.ui.main

import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.covidproject.covid_respiratorycare.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.ui.BaseActivity
import com.covidproject.covid_respiratorycare.ui.map.MappageFragment


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var navHostFragment: NavHostFragment
    var isDrawerOpen = false

    override fun initView() {
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
        binding.mainDrawerBt.setOnClickListener {
            binding.mainDrawerlayout.openDrawer(Gravity.LEFT)
        }

        binding.mainMenuAgeTv.setOnClickListener {
            mainViewModel.updateScrollLocation(Pair(0,15))
        }
        
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment_container,fragment).commit()
    }


}
