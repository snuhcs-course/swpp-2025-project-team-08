package com.example.itda.ui.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.example.itda.ui.common.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentsScreen (
    title: String,
    content: String,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Primary95,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

// 👇 Preview 추가
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContentsScreenPreview() {
    ContentsScreen(
        title = "이용약관",
        content = """
            제1조 (목적)
            이 약관은 회사가 제공하는 모바일 애플리케이션 서비스(이하 "서비스"라 합니다)의 이용과 관련하여 회사와 회원 간의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.
            
            제2조 (정의)
               1. "서비스"란 회사가 제공하는 모든 정보 및 기능을 말합니다.
               2. "회원"이란 본 약관에 따라 회사와 이용계약을 체결하고 서비스를 이용하는 고객을 말합니다.
               3. "아이디(ID)"란 회원의 식별과 서비스 이용을 위하여 회원이 정하고 회사가 승인하는 문자와 숫자의 조합을 말합니다.
            
            제3조 (약관의 게시와 개정)
               1. 회사는 본 약관의 내용을 회원이 쉽게 알 수 있도록 서비스 초기 화면에 게시합니다.
               2. 회사는 필요한 경우 관련 법령을 위배하지 않는 범위에서 본 약관을 개정할 수 있습니다.
        """.trimIndent(),
        onBack = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContentsScreenPreview_Privacy() {
    ContentsScreen(
        title = "개인정보 처리방침",
        content = """
            1. 개인정보의 처리 목적
            회사는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는 경우에는 개인정보 보호법에 따라 별도의 동의를 받는 등 필요한 조치를 이행할 예정입니다.
            
               가. 회원 가입 및 관리
               회원 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증, 회원자격 유지·관리, 제한적 본인확인제 시행에 따른 본인확인, 서비스 부정이용 방지, 각종 고지·통지 등을 목적으로 개인정보를 처리합니다.
            
               나. 재화 또는 서비스 제공
               물품배송, 서비스 제공, 맞춤 서비스 제공, 본인인증 등을 목적으로 개인정보를 처리합니다.
            
            2. 개인정보의 처리 및 보유기간
            회사는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를 수집시에 동의받은 개인정보 보유·이용기간 내에서 개인정보를 처리·보유합니다.
        """.trimIndent(),
        onBack = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ContentsScreenPreview_ShortContent() {
    ContentsScreen(
        title = "마케팅 이용동의",
        content = "마케팅 정보 수신에 동의하시면 다양한 혜택과 이벤트 정보를 받아보실 수 있습니다.",
        onBack = {}
    )
}