package com.example.itda.ui.profile.component

sealed class SettingDestination(val route: String) {
    data object Notice : SettingDestination("notice")
    data object FAQ : SettingDestination("faq")
    data object CustomerSupport : SettingDestination("customer_support")
    data object Terms : SettingDestination("terms")
    data object Privacy : SettingDestination("privacy")
    data object PersonalInfo : SettingDestination("personalinfo")
    data object SensitiveInfo : SettingDestination("sensitive_info")
    data object Location : SettingDestination("location")
    data object Marketing : SettingDestination("marketing")
}