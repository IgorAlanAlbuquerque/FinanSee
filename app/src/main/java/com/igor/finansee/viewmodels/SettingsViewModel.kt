package com.igor.finansee.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.datastore.ThemeOption
import com.igor.finansee.data.datastore.UserPreferencesRepository
import com.igor.finansee.data.datastore.UserPreferences
import com.igor.finansee.data.notifications.NotificationScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    val uiState: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(ThemeOption.SYSTEM, true, true, true, true, true, true, -1, -1, false)
        )

    fun setThemeOption(themeOption: ThemeOption) {
        viewModelScope.launch {
            userPreferencesRepository.updateThemeOption(themeOption)
        }
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateAnimationsEnabled(enabled)
        }
    }

    fun setAlertPendencies(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateAlertPendencies(enabled)
    }

    fun setReceiveNews(receive: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateReceiveNews(receive)
    }
    fun setReceiveFinancialAlerts(receive: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateReceiveFinancialAlerts(receive)
    }
    fun setReceivePremiumInfo(receive: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateReceivePremiumInfo(receive)
    }
    fun setReceivePartnerOffers(receive: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateReceivePartnerOffers(receive)
    }

    fun setAllEmailPreferences(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateReceiveNews(enabled)
        userPreferencesRepository.updateReceiveFinancialAlerts(enabled)
        userPreferencesRepository.updateReceivePremiumInfo(enabled)
        userPreferencesRepository.updateReceivePartnerOffers(enabled)
    }

    fun setDailyReminder(context: Context, hour: Int, minute: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateDailyReminder(hour, minute)
            NotificationScheduler.scheduleDailyReminder(context, hour, minute)
        }
    }

    fun disableDailyReminder(context: Context) {
        viewModelScope.launch {
            userPreferencesRepository.updateDailyReminder(-1, -1)
            NotificationScheduler.cancelDailyReminder(context)
        }
    }

    fun setAppLockEnabled(enabled: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateAppLockEnabled(enabled)
    }
}

class SettingsViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}