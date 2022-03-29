package com.demo.carspends.presentation.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.demo.carspends.CarSpendsApp
import com.demo.carspends.R
import com.demo.carspends.Screens
import com.demo.carspends.databinding.ActivityMainBinding
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationBar: BottomNavigationBar

    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private val navigator = AppNavigator(this, R.id.nav_host_fragment_activity_main)

    @Inject
    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CarSpendsApp).component.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar = findViewById<View>(R.id.ab_bottom_navigation_bar) as BottomNavigationBar

        initViews()
    }

    private fun initViews() {
        bottomNavigationBar
            .addItem(BottomNavigationItem(R.drawable.ic_home_black_24dp, R.string.menu_home))
            .addItem(BottomNavigationItem(R.drawable.ic_baseline_av_timer_24, R.string.menu_resources))
            .addItem(BottomNavigationItem(R.drawable.ic_baseline_bar_chart_24, R.string.menu_statistics))
            .initialise()
        bottomNavigationBar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                when (position) {
                    0 -> router.replaceScreen(Screens.NotesList())
                    1 -> router.replaceScreen(Screens.ComponentsList())
                }
                bottomNavigationBar.selectTab(position, false)
            }

            override fun onTabUnselected(position: Int) {}

            override fun onTabReselected(position: Int) {
                onTabSelected(position)
            }
        })
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }


    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}
