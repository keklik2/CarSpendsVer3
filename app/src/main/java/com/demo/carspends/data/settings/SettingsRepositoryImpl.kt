package com.demo.carspends.data.settings

import android.app.Application
import android.content.Context
import com.demo.carspends.R
import com.demo.carspends.domain.settings.SettingsRepository
import java.lang.Exception
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val app: Application
) : SettingsRepository {

    private val sp = app.getSharedPreferences(
        app.getString(R.string.settings_file_name),
        Context.MODE_PRIVATE
    )

    init { sp.edit().commit() }

    override fun setSetting(key: String, value: Int) {
        sp.edit().apply {
            putInt(key, value)
        }.apply()
    }

    override fun setSetting(key: String, value: String) =
        sp.edit().apply {
            putString(key, value)
        }.apply()

    override fun setSetting(key: String, value: Float) {
        sp.edit().apply {
            putFloat(key, value)
        }.apply()
    }

    override fun setSetting(key: String, value: Double) =
        sp.edit().apply {
            putFloat(key, value.toFloat())
        }.apply()

    override fun setSetting(key: String, value: Char) =
        sp.edit().apply {
            putString(key, value.toString())
        }.apply()

    override fun getSettingValue(key: String): Any {
        return when (key) {
            SettingsRepository.SETTING_FONT_SIZE -> sp.getFloat(
                key,
                SettingsRepository.FONT_SIZE_NORMAL
            )
            SettingsRepository.SETTING_STATISTIC_ONE -> sp.getString(
                key,
                SettingsRepository.STATISTIC_MOMENT_FUEL
            ).toString()
            SettingsRepository.SETTING_STATISTIC_TWO -> sp.getString(
                key,
                SettingsRepository.STATISTIC_MILEAGE_PRICE
            ).toString()
            SettingsRepository.SETTING_IS_FIRST_MAIN_LAUNCH -> sp.getInt(
                key,
                SettingsRepository.FIRST_LAUNCH
            )
            SettingsRepository.SETTING_IS_FIRST_COMPONENT_LAUNCH -> sp.getInt(
                key,
                SettingsRepository.FIRST_LAUNCH
            )
            SettingsRepository.SETTING_IS_FIRST_STATISTICS_LAUNCH -> sp.getInt(
                key,
                SettingsRepository.FIRST_LAUNCH
            )
            SettingsRepository.SETTING_IS_FIRST_CAR_LAUNCH -> sp.getInt(
                key,
                SettingsRepository.FIRST_LAUNCH
            )
            else -> throw Exception("Unknown key for application settings")
        }
    }

    companion object {

    }
}
