package com.demo.carspends.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.carspends.CarSpendsApp
import com.demo.carspends.R
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import javax.inject.Inject

class SettingsActivity: AppCompatActivity() {
    @Inject
    lateinit var navigatorHolder: NavigatorHolder
    private val navigator = AppNavigator(this, R.id.settings_fragment_container)

    @Inject
    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as CarSpendsApp).component.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }


    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    companion object {
        fun getInstance(context: Context): Intent = Intent(context, SettingsActivity::class.java)
    }
}
