package com.demo.carspends.presentation.fragments.statistics.graphics

data class GraphItem(
    var title: String,
    var measure: String,
    var maxHeight: Int,
    var drawableRes: Int,
    var bottomTextList: MutableList<String>,
    var dataList: MutableList<Int>
)
