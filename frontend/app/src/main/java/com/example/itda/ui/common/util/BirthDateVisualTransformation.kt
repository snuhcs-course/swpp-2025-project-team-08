package com.example.itda.ui.common.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class BirthDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(8)

        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                append(char)
                if (index == 3 || index == 5) append('-')
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset > digits.length) return formatted.length

                var transformedOffset = 0
                for (i in 0 until minOf(offset, digits.length)) {
                    transformedOffset++
                    if (i == 3 || i == 5) transformedOffset++
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0

                var originalOffset = 0
                var currentTransformed = 0

                while (currentTransformed < offset && originalOffset < digits.length) {
                    currentTransformed++
                    originalOffset++
                    if (originalOffset == 4 && currentTransformed < offset) currentTransformed++
                    if (originalOffset == 6 && currentTransformed < offset) currentTransformed++
                }

                return minOf(originalOffset, digits.length)
            }
        }

        return TransformedText(androidx.compose.ui.text.AnnotatedString(formatted), offsetMapping)
    }
}