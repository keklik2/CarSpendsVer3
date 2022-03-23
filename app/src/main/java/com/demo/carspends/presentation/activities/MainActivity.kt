package com.demo.carspends.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.demo.carspends.R
import com.demo.carspends.databinding.ActivityMainBinding
import com.demo.carspends.presentation.CarSpendsApp
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val navigator = AppNavigator(this, R.id.nav_host_fragment_activity_main)

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CarSpendsApp).component.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Setting bottom menu for activity
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        val navView: BottomNavigationView = binding.mainNavView
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.removeNavigator()
    }

    override fun onPause() {
        navigatorHolder.setNavigator(navigator)
        super.onPause()
    }


}