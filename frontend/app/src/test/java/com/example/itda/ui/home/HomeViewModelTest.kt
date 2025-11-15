//package com.example.itda.ui.home
//
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import app.cash.turbine.test
//import com.example.itda.data.model.DummyData
//import com.example.itda.data.repository.AuthRepository
//import com.example.itda.data.repository.ProgramRepository
//import com.example.itda.testing.MainDispatcherRule
//import com.google.common.truth.Truth.assertThat
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.runTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.mockito.junit.MockitoJUnitRunner
//
//@OptIn(ExperimentalCoroutinesApi::class)
//@RunWith(MockitoJUnitRunner::class)
//class HomeViewModelTest {
//
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val mainDispatcherRule = MainDispatcherRule()
//
//    @Mock
//    private lateinit var authRepository: AuthRepository
//
//    @Mock
//    private lateinit var programRepository: ProgramRepository
//
//    private lateinit var viewModel: HomeViewModel
//
//    private val dummyUser = DummyData.dummyUser[0]
//    private val dummyFeedItems = DummyData.dummyFeedItems
//
//    @Before
//    fun setup() = runTest {
//        // ProgramRepository 스텁 설정: loadHomeData() 에서 getFeedList()를 호출함
//        `when`(programRepository.getFeedList()).thenReturn(dummyFeedItems)
//
//        // AuthRepository 스텁 설정: 현재 더미 데이터를 사용하므로 (loadMyProfile 주석처리된 부분)
//        // 별도의 스텁이 필요 없으나, 미래의 API 호출에 대비하여 명시적으로 `getProfile`을 스텁할 수 있음.
//        // 현재 로직은 더미 데이터를 직접 사용하므로 Mockito 호출이 일어나지 않습니다.
//        // `when`(authRepository.getProfile()).thenReturn(Result.success(dummyUser.toProfileResponse()))
//
//        // ViewModel 생성. init 블록의 loadMyProfile()과 loadHomeData()가 실행됨.
//        viewModel = HomeViewModel(authRepository, programRepository)
//
//        // init 블록 내의 코루틴이 완료될 때까지 대기
//        advanceUntilIdle()
//    }
//
//    // ========================================
//    // Part 1: 사용자 프로필 로드 테스트 (loadMyProfile)
//    // ========================================
//
//    @Test
//    fun loadMyProfile_initialState_isCorrectlySet() = runTest {
//        // Then
//        viewModel.homeUi.test {
//            val state = awaitItem() // 초기 상태
//            assertThat(state.userId).isEqualTo(dummyUser.id)
//            assertThat(state.username).isEqualTo(dummyUser.name)
//            assertThat(state.isLoading).isFalse()
//            assertThat(state.generalError).isNull()
//            cancelAndIgnoreRemainingEvents()
//        }
//    }
//
//    // ========================================
//    // Part 2: 홈 데이터 로드 및 카테고리 테스트 (loadHomeData)
//    // ========================================
//
//    @Test
//    fun loadHomeData_feedItems_areCorrectlySet() = runTest {
//        // Then
//        viewModel.homeUi.test {
//            val state = awaitItem()
//            assertThat(state.feedItems).isEqualTo(dummyFeedItems)
//            assertThat(state.isLoading).isFalse()
//            cancelAndIgnoreRemainingEvents()
//        }
//    }
//
//    @Test
//    fun loadHomeData_categories_areCorrectlyAggregated() = runTest {
//        // Given: 더미 데이터에 포함된 모든 고유 카테고리 이름
//        val expectedCategoryNames = mutableSetOf<String>()
//        expectedCategoryNames.add("전체") // '전체' 카테고리가 수동으로 추가됨
//        dummyFeedItems.forEach { program ->
//            program.categories.forEach { category ->
//                expectedCategoryNames.add(category.name)
//            }
//        }
//        val expectedCategoryCount = expectedCategoryNames.size
//
//        // Then
//        viewModel.homeUi.test {
//            val state = awaitItem()
//            assertThat(state.categories.size).isEqualTo(expectedCategoryCount)
//            // 모든 예상 카테고리 이름이 목록에 포함되어 있는지 확인
//            val actualCategoryNames = state.categories.map { it.name }.toSet()
//            assertThat(actualCategoryNames).isEqualTo(expectedCategoryNames)
//            cancelAndIgnoreRemainingEvents()
//        }
//    }
//
//    @Test
//    fun loadHomeData_initialSelectedCategory_isOverall() = runTest {
//        // Then
//        viewModel.homeUi.test {
//            val state = awaitItem()
//            // HomeUiState의 기본값: DummyData.dummyCategories.first().name, 즉 "전체"
//            assertThat(state.selectedCategoryName).isEqualTo("전체")
//            cancelAndIgnoreRemainingEvents()
//        }
//    }
//
//    // ========================================
//    // Part 3: 카테고리 선택 로직 테스트 (onCategorySelected)
//    // ========================================
//
//    @Test
//    fun onCategorySelected_updatesStateCorrectly() = runTest {
//        // Given
//        val newCategoryName = "소비지원"
//
//        viewModel.homeUi.test {
//            // 1. setup()에서 로드된 초기 상태를 소비
//            awaitItem()
//
//            // When: 액션을 호출하여 상태 변경을 유도
//            viewModel.onCategorySelected(newCategoryName)
//
//            // Then: 액션에 의해 방출된 새로운 상태를 확인
//            val updatedState = awaitItem()
//            assertThat(updatedState.selectedCategoryName).isEqualTo(newCategoryName)
//            assertThat(updatedState.isLoading).isFalse() // 다른 상태는 변경되지 않아야 함
//            cancelAndIgnoreRemainingEvents()
//        }
//    }
//
//    @Test
//    fun onCategorySelected_updatesTwice_isLatest() = runTest {
//        // Given
//        val firstCategory = "대회"
//        val secondCategory = "교육"
//
//        viewModel.homeUi.test {
//            awaitItem() // 1. 초기 상태 소비
//
//            // When 1
//            viewModel.onCategorySelected(firstCategory)
//
//            // Then 1: 첫 번째 업데이트 확인
//            assertThat(awaitItem().selectedCategoryName).isEqualTo(firstCategory)
//
//            // When 2
//            viewModel.onCategorySelected(secondCategory)
//
//            // Then 2: 두 번째 업데이트 확인
//            assertThat(awaitItem().selectedCategoryName).isEqualTo(secondCategory)
//
//            cancelAndIgnoreRemainingEvents()
//        }
//    }
//}
