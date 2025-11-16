package com.example.itda.ui.bookmark

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

private const val PAGE_SIZE = 20

data class SortOption(val apiValue: String, val display: String)

val BOOKMARK_SORT_OPTIONS = listOf(
    SortOption("LATEST", "최신순"),
    SortOption("DEADLINE", "기한순")
)

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val programRepository: ProgramRepository,
) : ViewModel() {

    data class BookmarkUiState(
        val userId: String = "",
        val username: String = "",
        val categories: List<Category> = dummyCategories, // 필터 카테고리
        val selectedCategory: Category = Category("", "전체"), // 선택된 카테고리
        val sortOptions: List<SortOption> = BOOKMARK_SORT_OPTIONS,
        val selectedSort: SortOption = BOOKMARK_SORT_OPTIONS.first(), // 기본값: 최신순
        // 💡 페이지네이션 관련 필드 추가
        val currentPage: Int = 0,
        val isLastPage: Boolean = false,

        val allLoadedPrograms: List<ProgramResponse> = emptyList(), // 서버에서 로드된 전체 목록
        val bookmarkItems: List<ProgramResponse> = emptyList(), // 현재 선택된 카테고리로 필터링된 UI 목록
        val bookmarkIds: List<Int> = emptyList(), // 북마크 ID 목록 (토글 상태 관리용)

        val isPaginating: Boolean = false, // 다음 페이지 로딩 중
        val isLoading: Boolean = false, // 초기 로딩 중
        val isRefreshing: Boolean = false, // 새로고침 중
        val isLoadingBookmark: Boolean = false,
        val generalError: String? = null,
    )

    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadMyProfile()
            // 💡 초기 북마크 데이터 로드 (페이지 0)
            loadBookmarkData()
        }
    }


    fun refreshBookmarkData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            // 💡 새로고침 시 페이지 0부터 다시 로드
            loadBookmarkData(isRefresh = true)
            loadMyProfile()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun loadMyProfile() {
        // ... (기존 로직 유지) ...
        viewModelScope.launch {
            val user = authRepository.getProfile()
            user
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _uiState.update {
                        it.copy(
                            generalError = apiError.message,
                            username = "사용자",
                        )
                    }
                }
            user
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            userId = user.id,
                            username = user.name ?: "사용자",
                        )
                    }
                }
        }
    }

    /**
     * 💡 페이지 0 의 북마크 목록을 로드하고 상태를 초기화합니다. (초기 로드 및 새로고침)
     */
    fun loadBookmarkData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    // 로딩 시 기존 데이터 및 페이지 정보 초기화
                    allLoadedPrograms = emptyList(),
                    bookmarkItems = emptyList(),
                    currentPage = 0,
                    isLastPage = false,
                )
            }

            val sortType = _uiState.value.selectedSort.apiValue

            programRepository.getUserBookmarkPrograms(
                sort = sortType, // 최신순으로 가정
                page = 0,
                size = PAGE_SIZE
            )
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _uiState.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoading = false,
                        )
                    }
                }
                .onSuccess { response ->
                    val programs = response.content
                    val programIds = programs.map { it.id }

                    _uiState.update {
                        it.copy(
                            generalError = null,
                            allLoadedPrograms = programs, // 페이지 0 데이터
                            bookmarkIds = programIds,
                            bookmarkItems = programs, // 초기에는 로드된 전체 목록을 표시
                            isLastPage = response.isLast,
                            currentPage = 0,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    /**
     * 💡 다음 페이지의 북마크 목록을 로드하고 기존 목록에 추가합니다.
     */
    fun loadNextPage() {
        viewModelScope.launch {
            // 이미 로딩 중이거나, 마지막 페이지인 경우 스킵
            if (_uiState.value.isPaginating || _uiState.value.isLastPage) return@launch

            _uiState.update { it.copy(isPaginating = true) }

            val nextPage = _uiState.value.currentPage + 1
            val sortType = _uiState.value.selectedSort.apiValue

            programRepository.getUserBookmarkPrograms(
                sort = sortType,
                page = nextPage,
                size = PAGE_SIZE
            )
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _uiState.update {
                        it.copy(
                            generalError = apiError.message,
                            isPaginating = false,
                        )
                    }
                }
                .onSuccess { response ->
                    val newPrograms = response.content
                    val currentPrograms = _uiState.value.allLoadedPrograms
                    val currentIds = _uiState.value.bookmarkIds

                    _uiState.update {
                        it.copy(
                            generalError = null,
                            allLoadedPrograms = currentPrograms + newPrograms, // 전체 목록에 추가
                            bookmarkIds = currentIds + newPrograms.map { p -> p.id }, // ID 목록에 추가
                            isLastPage = response.isLast,
                            currentPage = nextPage,
                            isPaginating = false,
                        )
                    }
                    // 💡 새로운 데이터가 로드된 후, 현재 선택된 카테고리에 맞춰 필터링을 다시 적용
                    onCategorySelected(_uiState.value.selectedCategory)
                }
        }
    }

    fun onSortSelected(sortOption: SortOption) {
        if (_uiState.value.selectedSort.apiValue == sortOption.apiValue) return

        _uiState.update { it.copy(selectedSort = sortOption) }

        // 정렬 기준 변경 시 페이지 0부터 새로 로드
        loadBookmarkData(isRefresh = true)
    }

    /**
     * 💡 로드된 전체 목록을 기반으로 카테고리 필터링
     */
    fun onCategorySelected(category: Category) {
        _uiState.update {
            it.copy(
                selectedCategory = category
            )
        }

        loadBookmarkData()
    }

    fun onFeedBookmarkClicked(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBookmark = true) }

            val isBookmarked = id in _uiState.value.bookmarkIds

            // 1. UI 상태 업데이트를 위한 임시 데이터 계산
            val currentPrograms = _uiState.value.allLoadedPrograms
            val updatedIds = if (isBookmarked) {
                _uiState.value.bookmarkIds - id
            } else {
                _uiState.value.bookmarkIds + id
            }

            val updatedPrograms = if (isBookmarked) {
                // 북마크 해제 시, 전체 목록에서 해당 아이템을 제거
                currentPrograms.filter { it.id != id }
            } else {
                // 북마크 설정 시, 목록에 이미 있는 상태이므로 프로그램 목록은 유지
                currentPrograms
            }

            // 2. UI 상태를 먼저 업데이트하여 즉각적인 피드백 (아이콘 및 목록 제거)을 제공
            _uiState.update { it.copy(bookmarkIds = updatedIds, allLoadedPrograms = updatedPrograms) }
            onCategorySelected(_uiState.value.selectedCategory) // 필터링된 UI 목록에도 반영

            // 3. API 호출
            val apiCall = if (isBookmarked)
                programRepository.unbookmarkProgram(id)
            else
                programRepository.bookmarkProgram(id)

            apiCall
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    // 4. API 실패 시, UI 상태를 원래대로 되돌립니다.
                    _uiState.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoadingBookmark = false,
                            bookmarkIds = _uiState.value.bookmarkIds, // 원래 IDs로 롤백
                            allLoadedPrograms = currentPrograms, // 원래 프로그램 목록으로 롤백
                        )
                    }
                    onCategorySelected(_uiState.value.selectedCategory) // 롤백된 목록으로 필터링 재적용
                }
                .onSuccess {
                    // 5. API 성공 시, 로딩 상태만 해제합니다. (리스트는 이미 2번에서 업데이트됨)
                    _uiState.update { it.copy(generalError = null, isLoadingBookmark = false) }
                }
        }
    }
}