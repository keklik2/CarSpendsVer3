package com.demo.carspends.domain.settings

interface SettingsRepository {
    fun setSetting(key: String, value: Int)
    fun setSetting(key: String, value: String)
    fun setSetting(key: String, value: Float)
    fun setSetting(key: String, value: Double)
    fun setSetting(key: String, value: Char)

    fun getSettingValue(key: String): Any

    companion object {
        const val SETTING_FONT_SIZE = "setting_font_size"
        const val SETTING_STATISTIC_ONE = "setting_statistic_one"
        const val SETTING_STATISTIC_TWO = "setting_statistic_two"
        const val SETTING_IS_FIRST_MAIN_LAUNCH = "main_launch"
        const val SETTING_IS_FIRST_COMPONENT_LAUNCH = "component_launch"
        const val SETTING_IS_FIRST_STATISTICS_LAUNCH = "statistics_launch"
        const val SETTING_IS_FIRST_CAR_LAUNCH = "car_launch"

        const val FONT_SIZE_NORMAL = 1f
        const val FONT_SIZE_LARGE = 1.4f

        const val STATISTIC_AVG_FUEL = "avg_fuel"
        const val STATISTIC_MOMENT_FUEL = "moment_fuel"
        const val STATISTIC_ALL_FUEL = "all_fuel"
        const val STATISTIC_FUEL_PRICE = "fuel_price"
        const val STATISTIC_MILEAGE_PRICE = "mileage_price"
        const val STATISTIC_ALL_PRICE = "all_price"
        const val STATISTIC_ALL_MILEAGE = "all_mileage"
        const val FIRST_LAUNCH = 0
        const val NOT_FIRST_LAUNCH = 1
    }
}
