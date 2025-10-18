package com.example.itda.ui.main

// mainviewmodel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 뷰 상태를 정의하는 Sealed Interface/Class
// Compose에서 화면 구성을 분기하는 데 사용됩니다.
sealed interface MainViewState {
    // 초기 로딩 중
    data object Loading : MainViewState

    // 홈 화면 컨텐츠가 준비됨
    data object HomeContent : MainViewState

    // 에러 발생 시
    data class Error(val message: String) : MainViewState
}

class MainViewModel : ViewModel() {
    // 뷰 상태를 외부에 노출하는 StateFlow
    private val _viewState = MutableStateFlow<MainViewState>(MainViewState.Loading)
    val viewState: StateFlow<MainViewState> = _viewState

    init {
        // ViewModel 초기화 시 데이터 로드 시작
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // 실제 데이터 로딩 로직 (예: Repository 호출)
            // 여기서는 2초 후 HomeContent로 전환한다고 가정합니다.
            delay(2000)
            _viewState.value = MainViewState.HomeContent
        }
    }

    // UI에서 호출할 수 있는 함수 (예: 버튼 클릭 시 데이터 새로고침)
    fun refreshData() {
        _viewState.value = MainViewState.Loading
        loadInitialData()
    }
}