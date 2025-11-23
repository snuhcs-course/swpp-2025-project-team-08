package com.example.itda.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itda.data.model.Category
import com.example.itda.data.model.ProgramResponse
import com.example.itda.data.model.dummyCategories
import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.data.source.remote.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val programRepository: ProgramRepository,
) : ViewModel() {
    //    val programs = programRepository.getPrograms()
    data class HomeUiState(
        val userId: String = "", // 사용자 정보
        val username: String = "", // 사용자 정보
        val categories: List<Category> = dummyCategories, // 필터 카테고리
        val selectedCategory: Category = Category("","전체"), // 선택된 카테고리
        val feedItems: List<ProgramResponse> = emptyList(), // 메인 피드 목록 (ProgramRepository에서 가져올 데이터)
        val currentPage: Int = 0,               // 현재 페이지 번호 (0부터 시작)
        val isLastPage: Boolean = false,        // 마지막 페이지 여부
        val totalPages: Int = 0,                // 전체 페이지 수
        val totalElements : Int = 0,             // 전체 정책 수
        val isPaginating : Boolean = false,
        val isLoading: Boolean = false,
        val isLoadingBookmark : Boolean = false,
        val loadDataCount : Int = 0,
        val loadProfileCount : Int = 0,
        val loadNextCount : Int = 0,
        val isRefreshing: Boolean = false,
        val generalError: String? = null,
        val bookmarkPrograms : List<Int> = emptyList<Int>()
    )

    private val _homeUi = MutableStateFlow(HomeUiState())
    val homeUi: StateFlow<HomeUiState> = _homeUi.asStateFlow()

    init {
        viewModelScope.launch {
            loadHomeData()
            loadMyProfile()
            initBookmarkList()
        }
    }


    fun refreshHomeData() {
        viewModelScope.launch {
            _homeUi.update { it.copy(isRefreshing = true) }
            loadHomeData() // TODO - 그냥 load 가 아니라 메트릭을 조정해서 더 나은 결과 / 더 넓은 추천범위로 load 하는 방식 고민중
            initBookmarkList()
            _homeUi.update { it.copy(isRefreshing = false) }
        }
    }

    fun loadMyProfile() {

        viewModelScope.launch {
            _homeUi.update { it.copy() }

            val user = authRepository.getProfile()
            user
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _homeUi.update {
                        it.copy(
                            generalError = apiError.message,
                            username = "사용자",
                        )
                    }
                }
            user
                .onSuccess { user ->
                    _homeUi.update {
                        it.copy(
                            userId = user.id,
                            username = user.name ?: "사용자",
                        )
                    }
                }
        }
    }

    // 홈 화면에 필요한 모든 데이터를 로드
    private fun loadHomeData() {


        viewModelScope.launch {
            _homeUi.update { it.copy(
                loadDataCount = homeUi.value.loadDataCount + 1,
                isLoading = true,
                currentPage = 0,
                feedItems = emptyList()
            ) }

            val programs = programRepository.getPrograms(
                page = 0,
                size = 20,
                category = _homeUi.value.selectedCategory.category,
            )
            programs
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _homeUi.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoading = false,
                        )
                    }
                }
            programs
                .onSuccess { response ->
                    // 빈 list에 programs 의 categories 에 속하는 category 들의 합집합 집어넣어서 newCategories 구성하기
                    _homeUi.update {
                        it.copy(
                            feedItems = response.content,
                            currentPage = response.page,        // 현재 페이지 번호 저장
                            totalPages = response.totalPages,   // 전체 페이지 수 저장
                            isLastPage = response.isLast,
                            totalElements = response.totalElements,   // 전체 정책 수 저장
                            generalError = if (it.isRefreshing) null else it.generalError,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun loadNextPage() {
        val nextPageIndex = homeUi.value.currentPage + 1
        val isLast = homeUi.value.isLastPage

        // 1. 이미 로딩 중이거나 마지막 페이지면 더 이상 호출하지 않음
        if (homeUi.value.isPaginating || isLast) return


        viewModelScope.launch {
            _homeUi.update { it.copy(isPaginating = true, loadNextCount = homeUi.value.loadNextCount + 1) }

            programRepository.getPrograms(
                page = nextPageIndex,
                size = 20,
                category = _homeUi.value.selectedCategory.category
            )
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _homeUi.update {
                        it.copy(
                            generalError = apiError.message,
                            isPaginating = false
                        )
                    }
                }
                .onSuccess { response ->
                    _homeUi.update {
                        it.copy(
                            // 2. 기존 목록에 새로운 content를 추가
                            feedItems = it.feedItems + response.content,
                            currentPage = response.page,
                            isLastPage = response.isLast,
                            generalError = null,
                            isPaginating = false
                        )
                    }
                }
        }
    }

    // 카테고리 필터링 로직
    fun onCategorySelected(category: Category) {
        _homeUi.update {
            it.copy(
                // TODO - 현재는 Category.kt 에 저장해둔 dummy list 에서 찾지만 나중에 categories 찾는 api 구현 시 programRepository 통해 api 로 찾아서 저장해둔 category 에서 찾아오기
                selectedCategory = category
            )
        }

        loadHomeData()
    }

    fun initBookmarkList() {
        viewModelScope.launch {
            _homeUi.update { it.copy(
                isLoading = true,
                isLoadingBookmark = true,
                bookmarkPrograms = emptyList()
            ) }

            val allBookmarkPrograms = programRepository.getAllUserBookmarks()
            allBookmarkPrograms
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _homeUi.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoading = false,
                            isLoadingBookmark = false,
                        )
                    }
                }
                .onSuccess { response ->
                    _homeUi.update {
                        it.copy(
                            // generalError = null,
                            bookmarkPrograms = response.map { it.id },
                            isLoading = false,
                            isLoadingBookmark = false,
                        )
                    }
                }
        }
    }


    fun onFeedBookmarkClicked(id: Int) {
        viewModelScope.launch {
            _homeUi.update { it.copy(
                isLoadingBookmark = true, // 로딩 시작
            ) }

            // 롤백을 위해 API 호출 전의 현재 북마크 목록을 저장합니다.
            val originalBookmarkPrograms = homeUi.value.bookmarkPrograms

            // 1. UI 에서 즉시 북마크 상태를 토글합니다.
            val isBookmarked = id in originalBookmarkPrograms
            val updatedBookmarkPrograms = if (isBookmarked) {
                originalBookmarkPrograms - id // 북마크 해제 (리스트에서 제거)
            } else {
                originalBookmarkPrograms + id // 북마크 설정 (리스트에 추가)
            }

            // 2. UI 상태를 먼저 업데이트하여 즉각적인 피드백을 제공
            _homeUi.update { it.copy(bookmarkPrograms = updatedBookmarkPrograms) }


            // 3. API 호출
            val apiCall = if(isBookmarked)
                programRepository.unbookmarkProgram(id)
            else
                programRepository.bookmarkProgram(id)

            apiCall
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    // 4. API 실패 시, UI 상태를 원래대로 되돌립니다.
                    _homeUi.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoadingBookmark = false,
                            bookmarkPrograms = originalBookmarkPrograms,
                        )
                    }
                }
                .onSuccess { response ->
                    // 5. API 성공 시, 로딩 상태만 해제합니다. (리스트는 이미 2번에서 업데이트됨)
                    _homeUi.update {
                        it.copy(
                            generalError = null,
                            isLoadingBookmark = false,
                        )
                    }
                }
        }
    }
}