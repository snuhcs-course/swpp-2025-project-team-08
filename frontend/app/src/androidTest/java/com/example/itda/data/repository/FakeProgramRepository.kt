package com.example.itda.data.repository

import com.example.itda.data.model.Category
import com.example.itda.data.model.PageResponse
import com.example.itda.data.model.Program
import com.example.itda.data.model.ProgramDetailResponse
import com.example.itda.data.model.ProgramPageResponse
import com.example.itda.data.model.ProgramResponse

class FakeProgramRepository : ProgramRepository {

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
            operatingEntityType = "지방자치단체"
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
            operatingEntityType = "지방자치단체"
        )
    )
    var feedListData: List<Program> = emptyList()
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

    var getProgramsCalled = false
    var getProgramDetailsCalled = false
    var getExamplesCalled = false
    var getExampleDetailsCalled = false
    var getFeedListCalled = false
    var searchByRankCalled = false
    var searchByLatestCalled = false

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

    override fun getFeedList(): List<Program> {
        getFeedListCalled = true
        return feedListData
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

    var getUserBookmarkProgramsCalled = false
    var getAllUserBookmarksCalled = false
    var bookmarkProgramCalled = false
    var unbookmarkProgramCalled = false

    var lastGetUserBookmarkProgramsSort: String? = null
    var lastGetUserBookmarkProgramsPage: Int? = null
    var lastGetUserBookmarkProgramsSize: Int? = null
    var lastBookmarkProgramId: Int? = null
    var lastUnbookmarkProgramId: Int? = null

    override suspend fun getUserBookmarkPrograms(sort: String, page: Int, size: Int): Result<ProgramPageResponse> {
        getUserBookmarkProgramsCalled = true
        lastGetUserBookmarkProgramsSort = sort
        lastGetUserBookmarkProgramsPage = page
        lastGetUserBookmarkProgramsSize = size
        return getUserBookmarkProgramsResult
    }

    override suspend fun getAllUserBookmarks(): Result<List<ProgramResponse>> {
        getAllUserBookmarksCalled = true
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

    fun reset() {
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
                operatingEntityType = "지방자치단체"
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
                operatingEntityType = "지방자치단체"
            )
        )
        feedListData = emptyList()
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

        getProgramsCalled = false
        getProgramDetailsCalled = false
        getExamplesCalled = false
        getExampleDetailsCalled = false
        getFeedListCalled = false
        searchByRankCalled = false
        searchByLatestCalled = false
        getUserBookmarkProgramsCalled = false
        getAllUserBookmarksCalled = false
        bookmarkProgramCalled = false
        unbookmarkProgramCalled = false

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
    }
}