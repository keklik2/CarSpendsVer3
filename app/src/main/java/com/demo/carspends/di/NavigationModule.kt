package com.demo.carspends.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {
    private var cicerone: Cicerone<Router> = Cicerone.create(Router())

    @Provides
    fun provideRouter(): Router {
        return cicerone.router
    }

    @Provides
    fun provideHolder(): NavigatorHolder {
        return cicerone.getNavigatorHolder()
    }
}