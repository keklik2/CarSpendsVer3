package com.demo.carspends.utils

import android.content.Context
import android.content.Intent
import com.demo.carspends.presentation.activities.DetailElementsActivity
import com.demo.carspends.presentation.fragments.carAddOrEditFragment.CarAddOrEditFragment
import com.demo.carspends.presentation.fragments.componentsListFragment.ComponentsListFragment
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun CarEditOrAdd() = ActivityScreen {
        DetailElementsActivity.newAddOrEditCarIntent(it)
    }

    fun CarEditOrAdd(id: Int) = ActivityScreen {
        DetailElementsActivity.newAddOrEditCarIntent(it, id)
    }

    fun ComponentsList() = FragmentScreen {
        ComponentsListFragment()
    }

}