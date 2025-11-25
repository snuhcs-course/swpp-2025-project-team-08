package com.example.itda.ui.common.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

val ColorScheme.buttonBackground: Color
    @Composable @ReadOnlyComposable get() = primary

val ColorScheme.buttonText: Color
    @Composable @ReadOnlyComposable get() = onPrimary

val ColorScheme.cardBackground: Color
    @Composable @ReadOnlyComposable get() = primaryContainer

val ColorScheme.cardText: Color
    @Composable @ReadOnlyComposable get() = onPrimaryContainer

val ColorScheme.screenBackground: Color
    @Composable @ReadOnlyComposable get() = background

val ColorScheme.mainText: Color
    @Composable @ReadOnlyComposable get() = onBackground

val ColorScheme.secondaryText: Color
    @Composable @ReadOnlyComposable get() = onSurfaceVariant

val ColorScheme.iconColor: Color
    @Composable @ReadOnlyComposable get() = tertiary

val ColorScheme.borderColor: Color
    @Composable @ReadOnlyComposable get() = outline

val ColorScheme.inputBackground: Color
    @Composable @ReadOnlyComposable get() = surfaceVariant

val ColorScheme.sectionHeaderBackground: Color
    @Composable @ReadOnlyComposable get() = secondaryContainer

val ColorScheme.sectionHeaderText: Color
    @Composable @ReadOnlyComposable get() = onSecondaryContainer