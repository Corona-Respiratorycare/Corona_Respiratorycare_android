package com.covidproject.covid_respiratorycare.ui.main

import com.covidproject.covid_respiratorycare.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.covidproject.covid_respiratorycare.R
import com.covidproject.covid_respiratorycare.ui.BaseActivity
import com.covidproject.covid_respiratorycare.ui.map.MappageFragment


class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var navHostFragment: NavHostFragment

    override fun initAfterBinding() {
        supportFragmentManager.beginTransaction().add(R.id.main_fragment_container,MainFragment()
        ).commit()
        binding.mainBottomnavi.setOnNavigationItemSelectedListener {
            replaceFragment(
                when (it.itemId) {
                    R.id.mainFragment -> MainFragment()
                    else -> MappageFragment()
                }
            )
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_fragment_container,fragment).commit()
    }


}
