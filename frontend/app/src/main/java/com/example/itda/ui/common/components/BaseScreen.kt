package com.example.itda.ui.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

interface ScreenContract { val route: String; val title: String }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(contract: ScreenContract, onBack: (() -> Unit)? = null, content: @Composable (PaddingValues)->Unit) {
    Scaffold(
        topBar = {
            MainTopAppBar(
                title = contract.title,
                isBack = onBack != null,
                onBackClicked = onBack ?: {}
            )
         },
        content = content
    )
}