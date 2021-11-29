package com.demo.carspends.domain.component

data class ComponentItem (
    val id: Int,
    val title: String,
    val startMileage: Int,
    val resourceMileage: Int,
    val date: Long
    )