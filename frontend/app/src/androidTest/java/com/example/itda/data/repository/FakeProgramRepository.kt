package com.example.itda.data.repository

import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.model.ProgramResponse

class FakeProgramRepository : ProgramRepository {

    // --- Program 조회 및 검색 결과 변수 ---

    var getProgramsResult: Result<ProgramPageResponse> = Result.success(
        ProgramPageResponse(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            page = 0,
            isFirst = true,
            isLast = true
        )
    )
    var getProgramDetailsResult: Result<ProgramDetailResponse> = Result.success(
        ProgramDetailResponse(
            id = 1,
            uuid = "test-uuid-001",
            category = "employment",
            categoryValue = "고용, 일자리",
            title = "테스트 프로그램",
            details = "테스트 상세 내용",
            summary = "테스트 요약",
            preview = "테스트 미리보기",
            applicationMethod = "온라인 신청",
            applyUrl = "https://example.com/apply",
            referenceUrl = "https://example.com/ref",
            eligibilityMinAge = 20,
            eligibilityMaxAge = 39,
            eligibilityMinHousehold = 1,
            eligibilityMaxHousehold = 4,
            eligibilityMinIncome = 0,
            eligibilityMaxIncome = 5000,
            eligibilityRegion = "서울시",
            eligibilityGender = "무관",
            eligibilityMaritalStatus = "무관",
            eligibilityEducation = "무관",
            eligibilityEmployment = "무관",
            applyStartAt = "2024-01-01",
            applyEndAt = "2024-12-31",
            createdAt = "2024-01-01T00:00:00",
            operatingEntity = "서울시청",
            operatingEntityType = "지방자치단체",
            likeStatus = null,
            isBookmarked = false
        )
    )
    var getExamplesResult: Result<List<ProgramResponse>> = Result.success(emptyList())
    var getExampleDetailsResult: Result<ProgramDetailResponse> = Result.success(
        ProgramDetailResponse(
            id = 1,
            uuid = "test-uuid-001",
            category = "employment",
            categoryValue = "고용, 일자리",
            title = "테스트 예시",
            details = "테스트 상세 내용",
            summary = "테스트 요약",
            preview = "테스트 미리보기",
            applicationMethod = "온라인 신청",
            applyUrl = "https://example.com/apply",
            referenceUrl = "https://example.com/ref",
            eligibilityMinAge = 20,
            eligibilityMaxAge = 39,
            eligibilityMinHousehold = 1,
            eligibilityMaxHousehold = 4,
            eligibilityMinIncome = 0,
            eligibilityMaxIncome = 5000,
            eligibilityRegion = "서울시",
            eligibilityGender = "무관",
            eligibilityMaritalStatus = "무관",
            eligibilityEducation = "무관",
            eligibilityEmployment = "무관",
            applyStartAt = "2024-01-01",
            applyEndAt = "2024-12-31",
            createdAt = "2024-01-01T00:00:00",
            operatingEntity = "서울시청",
            operatingEntityType = "지방자치단체",
            likeStatus = null,
            isBookmarked = false
        )
    )

    var searchByRankResult: PageResponse<ProgramResponse> = PageResponse(
        content = emptyList(),
        totalPages = 0,
        totalElements = 0,
        size = 20,
        number = 0,
        first = true,
        last = true,
        numberOfElements = 0,
        empty = true
    )
    var searchByLatestResult: PageResponse<ProgramResponse> = PageResponse(
        content = emptyList(),
        totalPages = 0,
        totalElements = 0,
        size = 20,
        number = 0,
        first = true,
        last = true,
        numberOfElements = 0,
        empty = true
    )

    // --- Program 호출 여부 확인 변수 ---

    var getProgramsCalled = false
    var getProgramDetailsCalled = false
    var getExamplesCalled = false
    var getExampleDetailsCalled = false
    var searchByRankCalled = false
    var searchByLatestCalled = false

    // --- Program 마지막 호출 파라미터 변수 ---

    var lastGetProgramsPage: Int? = null
    var lastGetProgramsSize: Int? = null
    var lastGetProgramsCategory: String? = null
    var lastGetProgramDetailsId: Int? = null
    var lastGetExampleDetailsId: Int? = null
    var lastSearchByRankQuery: String? = null
    var lastSearchByRankPage: Int? = null
    var lastSearchByRankSize: Int? = null
    var lastSearchByRankCategory: String? = null
    var lastSearchByLatestQuery: String? = null
    var lastSearchByLatestPage: Int? = null
    var lastSearchByLatestSize: Int? = null
    var lastSearchByLatestCategory: String? = null

    // --- Bookmark 관련 결과 변수 ---

    var getUserBookmarkProgramsResult: Result<ProgramPageResponse> = Result.success(
        ProgramPageResponse(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            page = 0,
            isFirst = true,
            isLast = true
        )
    )
    var getAllUserBookmarksResult: Result<List<ProgramResponse>> = Result.success(emptyList())
    var bookmarkProgramResult: Result<Unit> = Result.success(Unit)
    var unbookmarkProgramResult: Result<Unit> = Result.success(Unit)

    // --- Bookmark 관련 호출 여부 확인 변수 ---

    var getUserBookmarkProgramsCalled = false
    var getAllUserBookmarksCalled = false
    var bookmarkProgramCalled = false
    var unbookmarkProgramCalled = false

    // --- Bookmark 관련 마지막 호출 파라미터 변수 ---

    var lastGetUserBookmarkProgramsSort: String? = null
    var lastGetUserBookmarkProgramsPage: Int? = null
    var lastGetUserBookmarkProgramsSize: Int? = null
    var lastBookmarkProgramId: Int? = null
    var lastUnbookmarkProgramId: Int? = null

    // --- Like/Dislike 관련 결과 변수 (추가) ---

    var likeLikeProgramResult: Result<Unit> = Result.success(Unit)
    var unlikeLikeProgramResult: Result<Unit> = Result.success(Unit)
    var likeDislikeProgramResult: Result<Unit> = Result.success(Unit)
    var unlikeDislikeProgramResult: Result<Unit> = Result.success(Unit)

    // --- Like/Dislike 관련 호출 여부 확인 변수 (추가) ---

    var likeLikeProgramCalled = false
    var unlikeLikeProgramCalled = false
    var likeDislikeProgramCalled = false
    var unlikeDislikeProgramCalled = false

    // --- Like/Dislike 관련 마지막 호출 파라미터 변수 (추가) ---

    var lastLikeLikeProgramId: Int? = null
    var lastUnlikeLikeProgramId: Int? = null
    var lastLikeDislikeProgramId: Int? = null
    var lastUnlikeDislikeProgramId: Int? = null

    // ------------------------------------
    // --- ProgramRepository 구현 시작 ---
    // ------------------------------------

    override suspend fun getPrograms(page: Int, size: Int, category: String): Result<ProgramPageResponse> {
        getProgramsCalled = true
        lastGetProgramsPage = page
        lastGetProgramsSize = size
        lastGetProgramsCategory = category
        return getProgramsResult
    }

    override suspend fun getProgramDetails(programId: Int): Result<ProgramDetailResponse> {
        getProgramDetailsCalled = true
        lastGetProgramDetailsId = programId
        return getProgramDetailsResult
    }

    override suspend fun getExamples(): Result<List<ProgramResponse>> {
        getExamplesCalled = true
        return getExamplesResult
    }

    override suspend fun getExampleDetails(exampleId: Int): Result<ProgramDetailResponse> {
        getExampleDetailsCalled = true
        lastGetExampleDetailsId = exampleId
        return getExampleDetailsResult
    }

    override suspend fun searchByRank(
        query: String,
        category: String,
        page: Int,
        size: Int
    ): PageResponse<ProgramResponse> {
        searchByRankCalled = true
        lastSearchByRankQuery = query
        lastSearchByRankCategory = category
        lastSearchByRankPage = page
        lastSearchByRankSize = size
        return searchByRankResult
    }

    override suspend fun searchByLatest(
        query: String,
        category: String,
        page: Int,
        size: Int
    ): PageResponse<ProgramResponse> {
        searchByLatestCalled = true
        lastSearchByLatestQuery = query
        lastSearchByLatestCategory = category
        lastSearchByLatestPage = page
        lastSearchByLatestSize = size
        return searchByLatestResult
    }

    override suspend fun getUserBookmarkPrograms(sort: String, page: Int, size: Int): Result<ProgramPageResponse> {
        getUserBookmarkProgramsCalled = true
        lastGetUserBookmarkProgramsSort = sort
        lastGetUserBookmarkProgramsPage = page
        lastGetUserBookmarkProgramsSize = size
        return getUserBookmarkProgramsResult
    }

    // ProgramRepositoryImpl.kt의 getAllUserBookmarks() 로직을 참고하여 (페이징을 처리하는 로직은 Fake 이므로 단순화)
    override suspend fun getAllUserBookmarks(): Result<List<ProgramResponse>> {
        getAllUserBookmarksCalled = true
        // 실제 구현에서는 ProgramRepositoryImpl처럼 페이징 로직이 있지만, Fake에서는 단순화하여
        // 첫 페이지 (page=0)의 결과를 반환하거나, 별도로 설정된 결과를 반환하도록 구현할 수 있습니다.
        // 여기서는 ProgramRepositoryImpl의 로직을 대체하여 설정된 결과를 반환합니다.
        return getAllUserBookmarksResult
    }

    override suspend fun bookmarkProgram(programId: Int): Result<Unit> {
        bookmarkProgramCalled = true
        lastBookmarkProgramId = programId
        return bookmarkProgramResult
    }

    override suspend fun unbookmarkProgram(programId: Int): Result<Unit> {
        unbookmarkProgramCalled = true
        lastUnbookmarkProgramId = programId
        return unbookmarkProgramResult
    }

    // --- Like/Dislike 관련 함수 구현 (추가) ---

    override suspend fun likeLikeProgram(programId: Int): Result<Unit> {
        likeLikeProgramCalled = true
        lastLikeLikeProgramId = programId
        return likeLikeProgramResult
    }

    override suspend fun unlikeLikeProgram(programId: Int): Result<Unit> {
        unlikeLikeProgramCalled = true
        lastUnlikeLikeProgramId = programId
        return unlikeLikeProgramResult
    }

    override suspend fun likeDislikeProgram(programId: Int): Result<Unit> {
        likeDislikeProgramCalled = true
        lastLikeDislikeProgramId = programId
        return likeDislikeProgramResult
    }

    override suspend fun unlikeDislikeProgram(programId: Int): Result<Unit> {
        unlikeDislikeProgramCalled = true
        lastUnlikeDislikeProgramId = programId
        return unlikeDislikeProgramResult
    }


    // --------------------------------
    // --- 초기화 (reset) 함수 시작 ---
    // --------------------------------

    fun reset() {
        // 프로그램 결과 초기화
        getProgramsResult = Result.success(
            ProgramPageResponse(
                content = emptyList(),
                totalPages = 0,
                totalElements = 0,
                size = 20,
                page = 0,
                isFirst = true,
                isLast = true
            )
        )
        getProgramDetailsResult = Result.success(
            ProgramDetailResponse(
                id = 1,
                uuid = "test-uuid-001",
                category = "employment",
                categoryValue = "고용, 일자리",
                title = "테스트 프로그램",
                details = "테스트 상세 내용",
                summary = "테스트 요약",
                preview = "테스트 미리보기",
                applicationMethod = "온라인 신청",
                applyUrl = "https://example.com/apply",
                referenceUrl = "https://example.com/ref",
                eligibilityMinAge = 20,
                eligibilityMaxAge = 39,
                eligibilityMinHousehold = 1,
                eligibilityMaxHousehold = 4,
                eligibilityMinIncome = 0,
                eligibilityMaxIncome = 5000,
                eligibilityRegion = "서울시",
                eligibilityGender = "무관",
                eligibilityMaritalStatus = "무관",
                eligibilityEducation = "무관",
                eligibilityEmployment = "무관",
                applyStartAt = "2024-01-01",
                applyEndAt = "2024-12-31",
                createdAt = "2024-01-01T00:00:00",
                operatingEntity = "서울시청",
                operatingEntityType = "지방자치단체",
                likeStatus = null,
                isBookmarked = false
            )
        )
        getExamplesResult = Result.success(emptyList())
        getExampleDetailsResult = Result.success(
            ProgramDetailResponse(
                id = 1,
                uuid = "test-uuid-001",
                category = "employment",
                categoryValue = "고용, 일자리",
                title = "테스트 예시",
                details = "테스트 상세 내용",
                summary = "테스트 요약",
                preview = "테스트 미리보기",
                applicationMethod = "온라인 신청",
                applyUrl = "https://example.com/apply",
                referenceUrl = "https://example.com/ref",
                eligibilityMinAge = 20,
                eligibilityMaxAge = 39,
                eligibilityMinHousehold = 1,
                eligibilityMaxHousehold = 4,
                eligibilityMinIncome = 0,
                eligibilityMaxIncome = 5000,
                eligibilityRegion = "서울시",
                eligibilityGender = "무관",
                eligibilityMaritalStatus = "무관",
                eligibilityEducation = "무관",
                eligibilityEmployment = "무관",
                applyStartAt = "2024-01-01",
                applyEndAt = "2024-12-31",
                createdAt = "2024-01-01T00:00:00",
                operatingEntity = "서울시청",
                operatingEntityType = "지방자치단체",
                likeStatus = null,
                isBookmarked = false
            )
        )
        searchByRankResult = PageResponse(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )
        searchByLatestResult = PageResponse(
            content = emptyList(),
            totalPages = 0,
            totalElements = 0,
            size = 20,
            number = 0,
            first = true,
            last = true,
            numberOfElements = 0,
            empty = true
        )

        // 북마크 결과 초기화
        getUserBookmarkProgramsResult = Result.success(
            ProgramPageResponse(
                content = emptyList(),
                totalPages = 0,
                totalElements = 0,
                size = 20,
                page = 0,
                isFirst = true,
                isLast = true
            )
        )
        getAllUserBookmarksResult = Result.success(emptyList())
        bookmarkProgramResult = Result.success(Unit)
        unbookmarkProgramResult = Result.success(Unit)

        // 좋아요/싫어요 결과 초기화 (추가)
        likeLikeProgramResult = Result.success(Unit)
        unlikeLikeProgramResult = Result.success(Unit)
        likeDislikeProgramResult = Result.success(Unit)
        unlikeDislikeProgramResult = Result.success(Unit)

        // 호출 여부 초기화
        getProgramsCalled = false
        getProgramDetailsCalled = false
        getExamplesCalled = false
        getExampleDetailsCalled = false
        searchByRankCalled = false
        searchByLatestCalled = false
        getUserBookmarkProgramsCalled = false
        getAllUserBookmarksCalled = false
        bookmarkProgramCalled = false
        unbookmarkProgramCalled = false
        likeLikeProgramCalled = false // 추가
        unlikeLikeProgramCalled = false // 추가
        likeDislikeProgramCalled = false // 추가
        unlikeDislikeProgramCalled = false // 추가

        // 마지막 호출 파라미터 초기화
        lastGetProgramsPage = null
        lastGetProgramsSize = null
        lastGetProgramsCategory = null
        lastGetProgramDetailsId = null
        lastGetExampleDetailsId = null
        lastSearchByRankQuery = null
        lastSearchByRankPage = null
        lastSearchByRankSize = null
        lastSearchByRankCategory = null
        lastSearchByLatestQuery = null
        lastSearchByLatestPage = null
        lastSearchByLatestSize = null
        lastSearchByLatestCategory = null
        lastGetUserBookmarkProgramsSort = null
        lastGetUserBookmarkProgramsPage = null
        lastGetUserBookmarkProgramsSize = null
        lastBookmarkProgramId = null
        lastUnbookmarkProgramId = null
        lastLikeLikeProgramId = null // 추가
        lastUnlikeLikeProgramId = null // 추가
        lastLikeDislikeProgramId = null // 추가
        lastUnlikeDislikeProgramId = null // 추가
    }
}