package com.demo.carspends.presentation.fragments.statistics.graphics.adapter

data class GraphItem(
    var title: String,
    var measure: String,
    var measureUnit: String,
    var drawableRes: Int,
    var labels: MutableList<String>,
    var data: MutableList<Int>
)
