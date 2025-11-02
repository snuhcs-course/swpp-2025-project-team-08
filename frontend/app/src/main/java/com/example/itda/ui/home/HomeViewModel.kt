package com.example.itda.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.Category
import com.example.itda.data.model.DummyData
import com.example.itda.data.model.Program
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.ProgramRepository
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
    private val authRepository: AuthRepository,
    private val programRepository: ProgramRepository
) : ViewModel() {
//    val programs = programRepository.getPrograms()
    data class HomeUiState(
    val userId: String = "", // 사용자 정보
    val username: String = "", // 사용자 정보
    val categories: List<Category> = emptyList<Category>(), // 필터 카테고리
    val selectedCategoryName: String = DummyData.dummyCategories.first().name, // 선택된 카테고리
    val feedItems: List<Program> = emptyList(), // 메인 피드 목록 (ProgramRepository에서 가져올 데이터)
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val generalError: String? = null
    )

    private val _homeUi = MutableStateFlow(HomeUiState())
    val homeUi: StateFlow<HomeUiState> = _homeUi.asStateFlow()

    init {
        viewModelScope.launch {
            loadMyProfile()
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

    private fun loadMyProfile() {

        viewModelScope.launch {
            _homeUi.update { it.copy(isLoading = true) }

            val user = DummyData.dummyUser[0]
            _homeUi.update {
                it.copy(
                    userId = user.id,
                    username = user.name ?: "사용자",
                    isLoading = false
                )
            }


            /* TODO : program api 완성 후 아래 함수로 변경
            val user = authRepository.getProfile()
            user
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _homeUi.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoading = false
                        )
                    }
                }
            user
                .onSuccess { user ->
                    _homeUi.update {
                        it.copy(
                            userId = user.id,
                            username = user.name,
                            isLoading = false
                        )
                    }
                }
             */
        }
    }

    // 홈 화면에 필요한 모든 데이터를 로드
    private fun loadHomeData() {
        viewModelScope.launch {
        _homeUi.update { it.copy(isLoading = true) }
        delay(2000L) // TODO - 실제 통신에서는 실제 딜레이가 있을거라 빼도 됨

        // 1. 피드 데이터 로드
        val programs = programRepository.getFeedList()
        val newCategories = mutableListOf<Category>(Category(0,  "전체")) // TODO - 실제 category 가 id = 0 이 전체가 아니라면 수정해야함.
        for (program in programs) {
            for (category in program.categories) {
                if (!newCategories.contains(category)) {
                    newCategories.add(category)
                }
            }
        }
        _homeUi.update {
            it.copy(
                categories = newCategories,
                feedItems = programs,
                isLoading = false,
                isRefreshing = false
            )
        }
        /* TODO : program api 완성 후 아래 함수로 변경

            val programs = programRepository.getPrograms()
            programs
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _homeUi.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoading = false
                        )
                    }
                }
            programs
                .onSuccess { programs ->

                    // 빈 list에 programs 의 categories 에 속하는 category 들의 합집합 집어넣어서 newCategories 구성하기
                    val newCategories = mutableListOf<Category>(Category(0,  "전체")) // TODO - 실제 category 가 id : 0 이 전체가 아니라면 수정해야함.
                    for (program in programs) {
                        for (category in program.categories) {
                            if (!newCategories.contains(category)) {
                                newCategories.add(category)
                            }
                        }
                    }

                    _homeUi.update {
                        it.copy(
                            categories = newCategories,
                            feedItems = programs,
                            isLoading = false
                        )
                    }
                }
             */

        }
    }

// 카테고리 필터링 로직
fun onCategorySelected(categoryName: String) {
_homeUi.update { it.copy(selectedCategoryName = categoryName) }
}
}