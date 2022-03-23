package com.demo.carspends.presentation

import android.app.Application
import com.demo.carspends.di.DaggerApplicationComponent
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class CarSpendsApp: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

}