// src/test/java/com/example/itda/ui/profile/PersonalInfoViewModelTest.kt
package com.example.itda.ui.profile

import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class PersonalInfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var userRepository: com.example.itda.data.repository.UserRepository

    private lateinit var vm: PersonalInfoViewModel

    @Before
    fun setUp() {
        // init { loadUserData() } 무시
        runBlocking { whenever(userRepository.getMe()).thenThrow(RuntimeException("skip")) }
        vm = PersonalInfoViewModel(userRepository)
    }

    @Test
    fun returnsFalse_and_doesNotCallRepo_whenRequiredFieldsMissing() = runTest {
        val ok = vm.submitPersonalInfo()
        assertThat(ok).isFalse()

        // suspend 검증은 verifyBlocking 사용
        verifyBlocking(userRepository, never()) {
            updateProfile(
                name = anyOrNull<String>(),
                birthDate = anyOrNull<String>(),
                gender = anyOrNull<String>(),
                address = anyOrNull<String>(),
                maritalStatus = anyOrNull<String>(),
                educationLevel = anyOrNull<String>(),
                householdSize = anyOrNull<Int>(),
                householdIncome = anyOrNull<Int>(),
                employmentStatus = anyOrNull<String>()
            )
        }
    }

    @Test
    fun returnsTrue_and_callsRepo_withEnumConversion() = runTest {
        // 입력
        vm.onNameChange("Hong")
        vm.onBirthDateChange("1990-01-01")
        vm.onGenderChange("남성")
        vm.onAddressChange("12345")
        vm.onMaritalStatusChange("기혼")
        vm.onEducationChange("대졸")
        vm.onHouseholdSizeChange("4")
        vm.onHouseholdIncomeChange("6000")
        vm.onEmploymentStatusChange("재직자")

        // suspend 스텁은 코루틴 안에서 직접 whenever 호출
        whenever(
            userRepository.updateProfile(
                name = anyOrNull(),
                birthDate = anyOrNull(),
                gender = anyOrNull(),
                address = anyOrNull(),
                maritalStatus = anyOrNull(),
                educationLevel = anyOrNull(),
                householdSize = anyOrNull(),
                householdIncome = anyOrNull(),
                employmentStatus = anyOrNull()
            )
        ).thenReturn(Result.success(Unit))

        val ok = vm.submitPersonalInfo()
        assertThat(ok).isTrue()
        advanceUntilIdle()

        // suspend 검증
        verifyBlocking(userRepository) {
            updateProfile(
                name = eq("Hong"),
                birthDate = eq("1990-01-01"),
                gender = eq("MALE"),
                address = eq("12345"),
                maritalStatus = eq("MARRIED"),
                educationLevel = eq("BACHELOR"),
                householdSize = eq(4),
                householdIncome = eq(6000),
                employmentStatus = eq("EMPLOYED")
            )
        }
    }

    @Test
    fun returnsTrue_then_setsGeneralError_whenRepoFails() = runTest {
        vm.onNameChange("Hong")
        vm.onBirthDateChange("1990-01-01")
        vm.onGenderChange("남성")
        vm.onAddressChange("12345")

        whenever(
            userRepository.updateProfile(
                name = anyOrNull(),
                birthDate = anyOrNull(),
                gender = anyOrNull(),
                address = anyOrNull(),
                maritalStatus = anyOrNull(),
                educationLevel = anyOrNull(),
                householdSize = anyOrNull(),
                householdIncome = anyOrNull(),
                employmentStatus = anyOrNull()
            )
        ).thenReturn(Result.failure(IllegalStateException("boom")))

        val ok = vm.submitPersonalInfo()
        assertThat(ok).isTrue()
        advanceUntilIdle()
        assertThat(vm.personalInfoUi.value.generalError).isNotNull()
    }

    @Test
    fun optionalBlanks_becomeNulls_inRepoCall() = runTest {
        vm.onNameChange("Hong")
        vm.onBirthDateChange("1990-01-01")
        vm.onGenderChange("남성")
        vm.onAddressChange("12345")
        // 나머지 옵션 공란 유지

        whenever(
            userRepository.updateProfile(
                name = anyOrNull(),
                birthDate = anyOrNull(),
                gender = anyOrNull(),
                address = anyOrNull(),
                maritalStatus = anyOrNull(),
                educationLevel = anyOrNull(),
                householdSize = anyOrNull(),
                householdIncome = anyOrNull(),
                employmentStatus = anyOrNull()
            )
        ).thenReturn(Result.success(Unit))

        val ok = vm.submitPersonalInfo()
        assertThat(ok).isTrue()
        advanceUntilIdle()

        verifyBlocking(userRepository) {
            updateProfile(
                name = eq("Hong"),
                birthDate = eq("1990-01-01"),
                gender = eq("MALE"),
                address = eq("12345"),
                maritalStatus = isNull<String>(),
                educationLevel = isNull<String>(),
                householdSize = isNull<Int>(),
                householdIncome = isNull<Int>(),
                employmentStatus = isNull<String>()
            )
        }
    }

    @Test
    fun korean_ANY_maps_to_ENUM_ANY() = runTest {
        vm.onNameChange("Hong")
        vm.onBirthDateChange("1990-01-01")
        vm.onGenderChange("여성")
        vm.onAddressChange("12345")
        vm.onMaritalStatusChange("무관")
        vm.onEducationChange("무관")
        vm.onEmploymentStatusChange("무관")
        vm.onHouseholdSizeChange("2")
        vm.onHouseholdIncomeChange("100")

        whenever(
            userRepository.updateProfile(
                name = anyOrNull(),
                birthDate = anyOrNull(),
                gender = anyOrNull(),
                address = anyOrNull(),
                maritalStatus = anyOrNull(),
                educationLevel = anyOrNull(),
                householdSize = anyOrNull(),
                householdIncome = anyOrNull(),
                employmentStatus = anyOrNull()
            )
        ).thenReturn(Result.success(Unit))

        val ok = vm.submitPersonalInfo()
        assertThat(ok).isTrue()
        advanceUntilIdle()

        verifyBlocking(userRepository) {
            updateProfile(
                name = eq("Hong"),
                birthDate = eq("1990-01-01"),
                gender = eq("FEMALE"),
                address = eq("12345"),
                maritalStatus = eq("ANY"),
                educationLevel = eq("ANY"),
                householdSize = eq(2),
                householdIncome = eq(100),
                employmentStatus = eq("ANY")
            )
        }
    }

    @Test
    fun numericFilters_apply_toHouseholdFields() = runTest {
        vm.onHouseholdSizeChange("a1b2c3")
        vm.onHouseholdIncomeChange("6,0-0_0")
        assertThat(vm.personalInfoUi.value.householdSize).isEqualTo("12")
        assertThat(vm.personalInfoUi.value.householdIncome).isEqualTo("6000")
    }

    @Test
    fun loadingFlag_toggles_trueThenFalse_aroundSubmit() = runTest {
        vm.onNameChange("Hong")
        vm.onBirthDateChange("1990-01-01")
        vm.onGenderChange("남성")
        vm.onAddressChange("12345")

        whenever(
            userRepository.updateProfile(
                name = anyOrNull(),
                birthDate = anyOrNull(),
                gender = anyOrNull(),
                address = anyOrNull(),
                maritalStatus = anyOrNull(),
                educationLevel = anyOrNull(),
                householdSize = anyOrNull(),
                householdIncome = anyOrNull(),
                employmentStatus = anyOrNull()
            )
        ).thenReturn(Result.success(Unit))

        assertThat(vm.personalInfoUi.value.isLoading).isFalse()
        val ok = vm.submitPersonalInfo()
        assertThat(ok).isTrue()
        assertThat(vm.personalInfoUi.value.isLoading).isTrue()
        advanceUntilIdle()
        assertThat(vm.personalInfoUi.value.isLoading).isFalse()
    }

    @Test
    fun onAddressChange_filtersToDigitsAndMaxFive() = runTest {
        vm.onAddressChange("12a-3 45XYZ")
        assertThat(vm.personalInfoUi.value.address).isEqualTo("12345")

        vm.onAddressChange("987654321")
        assertThat(vm.personalInfoUi.value.address).isEqualTo("98765")
    }

    @Test
    fun submit_returnsFalse_when_addressNotFiveDigits() = runTest {
        vm.onNameChange("Hong")
        vm.onBirthDateChange("1990-01-01")
        vm.onGenderChange("남성")

        vm.onAddressChange("")            // 빈값
        assertThat(vm.submitPersonalInfo()).isFalse()
        assertThat(vm.personalInfoUi.value.addressError).isEqualTo("우편번호 5자리를 입력해주세요")

        vm.onAddressChange("12")          // 5자 미만
        assertThat(vm.submitPersonalInfo()).isFalse()
        assertThat(vm.personalInfoUi.value.addressError).isEqualTo("우편번호 5자리를 입력해주세요")
    }

}
