package com.example.itda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itda.ui.auth.*
import com.example.itda.ui.auth.AuthViewModel
import com.example.itda.ui.main.MainViewModel
import com.example.itda.ui.main.MainViewState
import com.example.itda.ui.main.HomeView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // MaterialTheme 안에 앱의 UI를 정의합니다.
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // AuthFlow()
                    // ViewModel 인스턴스를 얻습니다.
                    val viewModel: MainViewModel = viewModel()
                    // ViewModel의 상태를 State로 수집하여 상태 변화를 감지합니다.
                    val viewState by viewModel.viewState.collectAsState()

                    // AuthViewModel 추가
//                    val authViewModel: AuthViewModel = viewModel()
//                    val authState by authViewModel.currentScreen.collectAsState()

                    // 뷰 상태(viewState)에 따라 화면을 분기합니다.
                    when (viewState) {
                        is MainViewState.Loading -> {
                            // 로딩 상태: 중앙에 로딩 인디케이터 표시
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is MainViewState.HomeContent -> {
                            // 홈 화면 상태: HomeView를 표시
                            HomeView(
                                onRefresh = viewModel::refreshData // 새로고침 액션 전달
                            )
                        }
                        is MainViewState.Error -> {
                            // 에러 상태: 에러 메시지 표시
                            val errorState = viewState as MainViewState.Error
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("오류 발생: ${errorState.message}", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthFlow() {
    // 로그인-회원가입 페이지 연결하는 로직 짜는 중이었습니다!
}