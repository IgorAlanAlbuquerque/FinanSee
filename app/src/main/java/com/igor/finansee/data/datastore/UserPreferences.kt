package com.igor.finansee.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val isDarkMode: Boolean,
    val areAnimationsEnabled: Boolean,
    val showCalculator: Boolean,
    val showAutocomplete: Boolean,
    val showBudgetSummary: Boolean,
    val alertPendencies: Boolean,
    val receiveNews: Boolean,
    val receiveFinancialAlerts: Boolean,
    val receivePremiumInfo: Boolean,
    val receivePartnerOffers: Boolean,
    val dailyReminderHour: Int,
    val dailyReminderMinute: Int
)

class UserPreferencesRepository(private val context: Context) {
    private object PreferencesKeys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val ARE_ANIMATIONS_ENABLED = booleanPreferencesKey("are_animations_enabled")
        val SHOW_CALCULATOR = booleanPreferencesKey("show_calculator")
        val SHOW_AUTOCOMPLETE = booleanPreferencesKey("show_autocomplete")
        val SHOW_BUDGET_SUMMARY = booleanPreferencesKey("show_budget_summary")
        val ALERT_PENDENCIES = booleanPreferencesKey("alert_pendencies")
        val RECEIVE_NEWS = booleanPreferencesKey("receive_news")
        val RECEIVE_FINANCIAL_ALERTS = booleanPreferencesKey("receive_financial_alerts")
        val RECEIVE_PREMIUM_INFO = booleanPreferencesKey("receive_premium_info")
        val RECEIVE_PARTNER_OFFERS = booleanPreferencesKey("receive_partner_offers")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: false
            val areAnimationsEnabled = preferences[PreferencesKeys.ARE_ANIMATIONS_ENABLED] ?: true
            val showCalculator = preferences[PreferencesKeys.SHOW_CALCULATOR] ?: true
            val showAutocomplete = preferences[PreferencesKeys.SHOW_AUTOCOMPLETE] ?: true
            val showBudgetSummary = preferences[PreferencesKeys.SHOW_BUDGET_SUMMARY] ?: false
            val alertPendencies = preferences[PreferencesKeys.ALERT_PENDENCIES] ?: true
            val receiveNews = preferences[PreferencesKeys.RECEIVE_NEWS] ?: true
            val receiveFinancialAlerts = preferences[PreferencesKeys.RECEIVE_FINANCIAL_ALERTS] ?: true
            val receivePremiumInfo = preferences[PreferencesKeys.RECEIVE_PREMIUM_INFO] ?: true
            val receivePartnerOffers = preferences[PreferencesKeys.RECEIVE_PARTNER_OFFERS] ?: true
            val dailyReminderHour = preferences[PreferencesKeys.DAILY_REMINDER_HOUR] ?: -1
            val dailyReminderMinute = preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] ?: -1

            UserPreferences(
                isDarkMode = isDarkMode,
                areAnimationsEnabled = areAnimationsEnabled,
                showCalculator = showCalculator,
                showAutocomplete = showAutocomplete,
                showBudgetSummary = showBudgetSummary,
                alertPendencies = alertPendencies,
                receiveNews = receiveNews,
                receiveFinancialAlerts = receiveFinancialAlerts,
                receivePremiumInfo = receivePremiumInfo,
                receivePartnerOffers = receivePartnerOffers,
                dailyReminderHour = dailyReminderHour,
                dailyReminderMinute = dailyReminderMinute
            )
        }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_DARK_MODE] = isDarkMode }
    }

    suspend fun updateAnimationsEnabled(areEnabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ARE_ANIMATIONS_ENABLED] = areEnabled }
    }

    suspend fun updateShowCalculator(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_CALCULATOR] = show }
    }

    suspend fun updateShowAutocomplete(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_AUTOCOMPLETE] = show }
    }

    suspend fun updateShowBudgetSummary(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_BUDGET_SUMMARY] = show }
    }

    suspend fun updateAlertPendencies(showAlerts: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ALERT_PENDENCIES] = showAlerts }
    }

    suspend fun updateReceiveNews(receive: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.RECEIVE_NEWS] = receive }
    }
    suspend fun updateReceiveFinancialAlerts(receive: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.RECEIVE_FINANCIAL_ALERTS] = receive }
    }
    suspend fun updateReceivePremiumInfo(receive: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.RECEIVE_PREMIUM_INFO] = receive }
    }
    suspend fun updateReceivePartnerOffers(receive: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.RECEIVE_PARTNER_OFFERS] = receive }
    }

    suspend fun updateDailyReminder(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_HOUR] = hour
            preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] = minute
        }
    }
}