package com.demo.carspends.utils.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import com.demo.carspends.R
import com.demo.carspends.domain.settings.SettingsRepository


class ExtendedTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    private val sp = context.getSharedPreferences(
        context.getString(R.string.settings_file_name),
        Context.MODE_PRIVATE
    )

    init {
        initSize()
    }

    private fun initSize() {
        val curSize = textSize
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX, curSize *
                    (sp.getFloat(
                        SettingsRepository.SETTING_FONT_SIZE,
                        SettingsRepository.FONT_SIZE_NORMAL
                    ) ?: SettingsRepository.FONT_SIZE_NORMAL)
        )
    }
}
