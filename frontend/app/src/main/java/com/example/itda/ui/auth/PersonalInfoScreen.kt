package com.example.itda.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.*

@Preview(showBackground = true)
@Composable
fun PersonalInfoScreen(){

    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var borndate by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // 회원가입 두 번째 페이지는 개발 중이었습니다!
    // 현재 이름, 닉네임, 번호, 주소, 생년월일을 받으려고 구상하였으나 추가적인 의견 있으시면 알려주세요!

}