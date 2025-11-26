package com.example.itda.ui.profile.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.itda.ui.auth.components.isValidBirthDate
import com.example.itda.ui.common.theme.scaledSp
import com.example.itda.ui.common.util.BirthDateVisualTransformation

@Composable
fun BirthDateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(value) {
        localError = when {
            value.isEmpty() -> null
            value.length < 8 -> null
            value.length == 8 && !isValidBirthDate(value) -> "올바른 생년월일을 입력해주세요 (예: 19990101)"
            else -> null
        }
    }

    val displayError = errorMessage ?: localError

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.scaledSp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.scaledSp
                )
            },
            visualTransformation = BirthDateVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = displayError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(8.dp),
            supportingText = if (value.isNotEmpty() && value.length < 8) {
                {
                    Text(
                        text = "${value.length}/8",
                        fontSize = 12.scaledSp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else null
        )

        if (displayError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayError,
                fontSize = 12.scaledSp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}