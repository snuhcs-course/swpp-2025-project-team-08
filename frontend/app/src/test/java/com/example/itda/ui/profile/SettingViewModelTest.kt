package com.example.itda.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.source.local.SettingsDataStore
import com.example.itda.testing.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SettingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var settingsDataStore: SettingsDataStore

    private lateinit var viewModel: SettingsViewModel

    private val darkModeFlow = MutableStateFlow(false)
    private val fontSizeFlow = MutableStateFlow(SettingsViewModel.FontSize.MEDIUM)

    @Before
    fun setup() {
        `when`(settingsDataStore.darkModeFlow).thenReturn(darkModeFlow)
        `when`(settingsDataStore.fontSizeFlow).thenReturn(fontSizeFlow)
    }

    // ========================================
    // Part 1: 초기화 및 설정 로딩 테스트
    // ========================================

    @Test
    fun init_loadsSettings_withDefaultValues() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.settingsUi.test {
            val state = awaitItem()
            assertThat(state.darkMode).isFalse()
            assertThat(state.fontSize).isEqualTo(SettingsViewModel.FontSize.MEDIUM)
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_loadsSettings_withDarkModeEnabled() = runTest {
        darkModeFlow.value = true

        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.settingsUi.test {
            val state = awaitItem()
            assertThat(state.darkMode).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_loadsSettings_withLargeFontSize() = runTest {
        fontSizeFlow.value = SettingsViewModel.FontSize.LARGE

        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.settingsUi.test {
            val state = awaitItem()
            assertThat(state.fontSize).isEqualTo(SettingsViewModel.FontSize.LARGE)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_loadsSettings_withAllCustomValues() = runTest {
        darkModeFlow.value = true
        fontSizeFlow.value = SettingsViewModel.FontSize.EXTRA_LARGE

        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.settingsUi.test {
            val state = awaitItem()
            assertThat(state.darkMode).isTrue()
            assertThat(state.fontSize).isEqualTo(SettingsViewModel.FontSize.EXTRA_LARGE)
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 2: 다크모드 토글 테스트
    // ========================================

    @Test
    fun toggleDarkMode_turnsOn_whenOff() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.toggleDarkMode()
        advanceUntilIdle()

        verify(settingsDataStore).setDarkMode(true)
    }

    @Test
    fun toggleDarkMode_turnsOff_whenOn() = runTest {
        darkModeFlow.value = true
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.toggleDarkMode()
        advanceUntilIdle()

        verify(settingsDataStore).setDarkMode(false)
    }

    @Test
    fun toggleDarkMode_updatesUI_reactively() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.toggleDarkMode()
        darkModeFlow.value = true
        advanceUntilIdle()

        viewModel.settingsUi.test {
            val state = awaitItem()
            assertThat(state.darkMode).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 3: 폰트 크기 변경 테스트
    // ========================================

    @Test
    fun setFontSize_updatesTo_SMALL() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.setFontSize(SettingsViewModel.FontSize.SMALL)
        advanceUntilIdle()

        verify(settingsDataStore).setFontSize(SettingsViewModel.FontSize.SMALL)
    }

    @Test
    fun setFontSize_updatesTo_LARGE() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.setFontSize(SettingsViewModel.FontSize.LARGE)
        advanceUntilIdle()

        verify(settingsDataStore).setFontSize(SettingsViewModel.FontSize.LARGE)
    }

    @Test
    fun setFontSize_updatesTo_EXTRA_LARGE() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.setFontSize(SettingsViewModel.FontSize.EXTRA_LARGE)
        advanceUntilIdle()

        verify(settingsDataStore).setFontSize(SettingsViewModel.FontSize.EXTRA_LARGE)
    }

    @Test
    fun setFontSize_multipleTimes_callsDataStore() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.setFontSize(SettingsViewModel.FontSize.SMALL)
        advanceUntilIdle()
        viewModel.setFontSize(SettingsViewModel.FontSize.LARGE)
        advanceUntilIdle()
        viewModel.setFontSize(SettingsViewModel.FontSize.EXTRA_LARGE)
        advanceUntilIdle()

        verify(settingsDataStore).setFontSize(SettingsViewModel.FontSize.SMALL)
        verify(settingsDataStore).setFontSize(SettingsViewModel.FontSize.LARGE)
        verify(settingsDataStore).setFontSize(SettingsViewModel.FontSize.EXTRA_LARGE)
    }

    @Test
    fun setFontSize_updatesUI_reactively() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.setFontSize(SettingsViewModel.FontSize.LARGE)
        fontSizeFlow.value = SettingsViewModel.FontSize.LARGE
        advanceUntilIdle()

        viewModel.settingsUi.test {
            val state = awaitItem()
            assertThat(state.fontSize).isEqualTo(SettingsViewModel.FontSize.LARGE)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ========================================
    // Part 4: 로그아웃 테스트
    // ========================================

    @Test
    fun logout_callsAuthRepository() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.logout()
        advanceUntilIdle()

        verify(authRepository).logout()
    }

    // ========================================
    // Part 5: FontSize Enum 테스트
    // ========================================

    @Test
    fun fontSize_SMALL_hasCorrectValues() {
        val fontSize = SettingsViewModel.FontSize.SMALL
        assertThat(fontSize.displayName).isEqualTo("작게")
        assertThat(fontSize.scale).isEqualTo(0.9f)
    }

    @Test
    fun fontSize_MEDIUM_hasCorrectValues() {
        val fontSize = SettingsViewModel.FontSize.MEDIUM
        assertThat(fontSize.displayName).isEqualTo("보통")
        assertThat(fontSize.scale).isEqualTo(1.0f)
    }

    @Test
    fun fontSize_LARGE_hasCorrectValues() {
        val fontSize = SettingsViewModel.FontSize.LARGE
        assertThat(fontSize.displayName).isEqualTo("크게")
        assertThat(fontSize.scale).isEqualTo(1.1f)
    }

    @Test
    fun fontSize_EXTRA_LARGE_hasCorrectValues() {
        val fontSize = SettingsViewModel.FontSize.EXTRA_LARGE

        // 줄바꿈 있든 없든 동일하게 보도록 정규화
        val normalizedDisplayName = fontSize.displayName.replace("\n", " ").trim()

        assertThat(normalizedDisplayName).isEqualTo("매우 크게")
        assertThat(fontSize.scale).isEqualTo(1.2f)
    }

    @Test
    fun fontSize_allValues_exist() {
        val values = SettingsViewModel.FontSize.values()
        assertThat(values).hasLength(4)
        assertThat(values).asList().containsExactly(
            SettingsViewModel.FontSize.SMALL,
            SettingsViewModel.FontSize.MEDIUM,
            SettingsViewModel.FontSize.LARGE,
            SettingsViewModel.FontSize.EXTRA_LARGE
        )
    }

    // ========================================
    // Part 6: 복합 시나리오 테스트
    // ========================================

    @Test
    fun changingMultipleSettings_updatesCorrectly() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        viewModel.toggleDarkMode()
        darkModeFlow.value = true
        advanceUntilIdle()

        viewModel.setFontSize(SettingsViewModel.FontSize.LARGE)
        fontSizeFlow.value = SettingsViewModel.FontSize.LARGE
        advanceUntilIdle()

        viewModel.settingsUi.test {
            val state = awaitItem()
            assertThat(state.darkMode).isTrue()
            assertThat(state.fontSize).isEqualTo(SettingsViewModel.FontSize.LARGE)
            cancelAndIgnoreRemainingEvents()
        }

        verify(settingsDataStore).setDarkMode(true)
        verify(settingsDataStore).setFontSize(SettingsViewModel.FontSize.LARGE)
    }

    @Test
    fun settingsFlow_reactsToExternalChanges() = runTest {
        viewModel = SettingsViewModel(authRepository, settingsDataStore)
        advanceUntilIdle()

        darkModeFlow.value = true
        fontSizeFlow.value = SettingsViewModel.FontSize.EXTRA_LARGE
        advanceUntilIdle()

        viewModel.settingsUi.test {
            val state = awaitItem()
            assertThat(state.darkMode).isTrue()
            assertThat(state.fontSize).isEqualTo(SettingsViewModel.FontSize.EXTRA_LARGE)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
