package com.example.itda.ui.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.itda.ui.common.theme.Neutral100

interface ScreenContract { val route: String; val title: String }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    contract: ScreenContract,
    onBack: (() -> Unit)? = null,
    topBarVisible: Boolean = true,
    content: @Composable (PaddingValues)->Unit,
) {
    Scaffold(
        topBar = {
            MainTopAppBar(
                title = contract.title,
                isBack = onBack != null,
                onBackClicked = onBack ?: {},
                visible = topBarVisible,
            )
         },
        containerColor = Neutral100,
        content = content
    )
}