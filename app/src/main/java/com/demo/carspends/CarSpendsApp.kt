package com.demo.carspends

import android.app.Application
import com.demo.carspends.di.DaggerApplicationComponent

class CarSpendsApp: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

}