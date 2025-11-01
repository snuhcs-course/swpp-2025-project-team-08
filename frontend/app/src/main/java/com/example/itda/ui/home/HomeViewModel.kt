package com.example.itda.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.Category
import com.example.itda.data.model.DummyData
import com.example.itda.data.model.FeedItem
import com.example.itda.data.model.User
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val programRepository: ProgramRepository
) : ViewModel() {
//    val programs = programRepository.getPrograms()
    data class HomeUiState(
        val user: User = DummyData.dummyUser[0], // 사용자 정보
        val categories: List<Category> = DummyData.dummyCategories, // 필터 카테고리
        val selectedCategoryName: String = DummyData.dummyCategories.first().name, // 선택된 카테고리
        val feedItems: List<FeedItem> = emptyList(), // 메인 피드 목록 (ProgramRepository에서 가져올 데이터)
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false
    )



    private val _homeUi = MutableStateFlow(HomeUiState())
    val homeUi: StateFlow<HomeUiState> = _homeUi.asStateFlow()

    init {
        viewModelScope.launch {

            loadHomeData()

        }
    }

    fun refreshHomeData() {
        viewModelScope.launch {
            _homeUi.update { it.copy(isRefreshing = true) }
            loadHomeData() // TODO - 그냥 load 가 아니라 메트릭을 조정해서 더 나은 결과 / 더 넓은 추천범위로 load 하는 방식 고민중
            _homeUi.update { it.copy(isRefreshing = false) }
        }
    }

    // 홈 화면에 필요한 모든 데이터를 로드
    private fun loadHomeData() {
        viewModelScope.launch {
            _homeUi.update { it.copy(isLoading = true) }

            delay(2000L) // TODO - 실제 통신에서는 실제 딜레이가 있을거라 빼도 됨

            // 1. 피드 데이터 로드
            val programs = // TODO: programRepository.getPrograms() 구현 후 호출
                programRepository.getFeedList()

            val user =
                userRepository.getMe()


            _homeUi.update {
                it.copy(
                    user = user,
                    feedItems = programs,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    // 카테고리 필터링 로직
    fun onCategorySelected(categoryName: String) {
        _homeUi.update { it.copy(selectedCategoryName = categoryName) }
    }
}