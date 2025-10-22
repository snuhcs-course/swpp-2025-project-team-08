package com.example.itda.ui.main

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.itda.ui.auth.AuthViewModel

// mainscreen 에서 login 여부 보고 분기 -> homescreen / loginscreen

@Composable
fun MainScreen(
    authViewModel: AuthViewModel
) {

    val navController = rememberNavController()



}