package com.example.itda.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class PersonalInfoViewModel : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _age = MutableStateFlow("")
    val age: StateFlow<String> = _age

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _maritalStatus = MutableStateFlow("")
    val maritalStatus: StateFlow<String> = _maritalStatus

    private val _education = MutableStateFlow("")
    val education: StateFlow<String> = _education

    private val _householdSize = MutableStateFlow("")
    val householdSize: StateFlow<String> = _householdSize

    private val _householdIncome = MutableStateFlow("")
    val householdIncome: StateFlow<String> = _householdIncome

    private val _excludedKeywords = MutableStateFlow("")
    val excludedKeywords: StateFlow<String> = _excludedKeywords

    fun updateName(value: String) { _name.value = value }
    fun updateAge(value: String) { _age.value = value }
    fun updateGender(value: String) { _gender.value = value }
    fun updateAddress(value: String) { _address.value = value }
    fun updateMaritalStatus(value: String) { _maritalStatus.value = value }
    fun updateEducation(value: String) { _education.value = value }
    fun updateHouseholdSize(value: String) { _householdSize.value = value }
    fun updateHouseholdIncome(value: String) { _householdIncome.value = value }
    fun updateExcludedKeywords(value: String) { _excludedKeywords.value = value }

    fun savePersonalInfo() {
        // TODO: Repository에 저장하는 로직
        // 지금은 UI만 구현하므로 실제 저장 로직은 나중에 API 연동 시 추가
    }
}