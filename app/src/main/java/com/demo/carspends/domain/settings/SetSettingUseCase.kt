package com.demo.carspends.domain.settings

import javax.inject.Inject

class SetSettingUseCase @Inject constructor(private val repository: SettingsRepository) {
    operator fun invoke(key: String, value: Int) = repository.setSetting(key, value)
    operator fun invoke(key: String, value: String) = repository.setSetting(key, value)
    operator fun invoke(key: String, value: Float) = repository.setSetting(key, value)
    operator fun invoke(key: String, value: Double) = repository.setSetting(key, value)
    operator fun invoke(key: String, value: Char) = repository.setSetting(key, value)
}
