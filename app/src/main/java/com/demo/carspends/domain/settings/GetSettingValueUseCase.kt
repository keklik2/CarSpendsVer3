package com.demo.carspends.domain.settings

import javax.inject.Inject

class GetSettingValueUseCase @Inject constructor(private val repository: SettingsRepository) {
    operator fun invoke(key: String): Any = repository.getSettingValue(key)
}
