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
        val isLoading: Boolean = false
    )

    private val _homeUi = MutableStateFlow(HomeUiState())
    val homeUi: StateFlow<HomeUiState> = _homeUi.asStateFlow()

    init {
        loadHomeData()
    }

    // 홈 화면에 필요한 모든 데이터를 로드
    private fun loadHomeData() {
        viewModelScope.launch {
            _homeUi.update { it.copy(isLoading = true) }

            // 1. 피드 데이터 로드
            val programs = // TODO: programRepository.getPrograms() 구현 후 호출
                programRepository.getFeedList()

            val user =
                userRepository.getMe()


            _homeUi.update {
                it.copy(
                    user = user,
                    feedItems = programs,
                    isLoading = false
                )
            }
        }
    }

    // 카테고리 필터링 로직
    fun onCategorySelected(categoryName: String) {
        _homeUi.update { it.copy(selectedCategoryName = categoryName) }
    }
}