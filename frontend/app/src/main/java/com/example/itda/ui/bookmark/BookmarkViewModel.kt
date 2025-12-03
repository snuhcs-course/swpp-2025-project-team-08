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
     * 페이지 0 의 북마크 목록을 로드하고 상태를 초기화합니다. (초기 로드 및 새로고침)
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
     * 다음 페이지의 북마크 목록을 로드하고 기존 목록에 추가합니다.
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

                    _uiState.update { currentState ->
                        val updatedPrograms = currentState.allLoadedPrograms + newPrograms
                        val updatedIds = currentState.bookmarkIds + newPrograms.map { p -> p.id }

                        currentState.copy(
                            generalError = null,
                            allLoadedPrograms = updatedPrograms, // 전체 목록에 추가
                            bookmarkIds = updatedIds, // ID 목록에 추가
                            bookmarkItems = updatedPrograms,
                            isLastPage = response.isLast,
                            currentPage = nextPage,
                            isPaginating = false,
                        )
                    }
                }
        }
    }

    fun onSortSelected(sortOption: SortOption) {
        if (_uiState.value.selectedSort.apiValue == sortOption.apiValue) return

        _uiState.update { it.copy(selectedSort = sortOption) }

        // 정렬 기준 변경 시 페이지 0부터 새로 로드
        loadBookmarkData(isRefresh = true)
    }


    fun onFeedBookmarkClicked(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBookmark = true) }

            val isBookmarked = id in _uiState.value.bookmarkIds


            val apiCall = if (isBookmarked)
                programRepository.unbookmarkProgram(id)
            else
                programRepository.bookmarkProgram(id)

            apiCall
                .onFailure { exception ->
                    val apiError = ApiErrorParser.parseError(exception)
                    _uiState.update {
                        it.copy(
                            generalError = apiError.message,
                            isLoadingBookmark = false,
                            )
                    }
                }
                .onSuccess {

                    val currentPrograms = _uiState.value.allLoadedPrograms

                    if (isBookmarked) {
                        // 북마크 해제 성공: 목록 및 ID에서 아이템을 제거합니다.
                        val updatedPrograms = currentPrograms.filter { it.id != id }
                        val updatedIds = _uiState.value.bookmarkIds - id

                        _uiState.update {
                            it.copy(
                                generalError = null,
                                isLoadingBookmark = false,
                                bookmarkIds = updatedIds,
                                allLoadedPrograms = updatedPrograms,
                                bookmarkItems = updatedPrograms
                            )
                        }
                    } else {
                        // 북마크 설정 성공: ID만 추가하여 아이콘 변경을 유도하고 목록은 유지합니다.
                        _uiState.update {
                            it.copy(
                                generalError = null,
                                isLoadingBookmark = false,
                                bookmarkIds = it.bookmarkIds + id
                            )
                        }
                    }
                }
        }
    }
}