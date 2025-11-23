package com.example.itda.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itda.ui.auth.components.AddressResult
import com.example.itda.ui.auth.components.KakaoAddressSearchDialog
import com.example.itda.ui.auth.components.isValidBirthDate
import com.example.itda.ui.common.enums.EducationLevel
import com.example.itda.ui.common.enums.EmploymentStatus
import com.example.itda.ui.common.enums.Gender
import com.example.itda.ui.common.enums.MaritalStatus
import com.example.itda.ui.common.theme.scaledSp
import com.example.itda.ui.profile.PersonalInfoViewModel
import com.example.itda.ui.auth.components.TagSelectionSection
import kotlinx.coroutines.launch

// 생년월일 VisualTransformation (20010101 -> 2001-01-01)
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

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    ui: PersonalInfoViewModel.PersonalInfoUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPostCodeChange: (String) -> Unit,
    onMaritalStatusChange: (String) -> Unit,
    onEducationChange: (String) -> Unit,
    onHouseholdSizeChange: (String) -> Unit,
    onHouseholdIncomeChange: (String) -> Unit,
    onEmploymentStatusChange: (String) -> Unit,
    onTagInputChange: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var showAddressDialog by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf<AddressResult?>(null) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var nameFieldY by remember { mutableFloatStateOf(0f) }
    var birthDateFieldY by remember { mutableFloatStateOf(0f) }
    var genderFieldY by remember { mutableFloatStateOf(0f) }
    var addressFieldY by remember { mutableFloatStateOf(0f) }

    val snackbarHostState = remember { SnackbarHostState() }

    // 서버에서 불러온 주소 초기화
    LaunchedEffect(ui.address, ui.postcode) {
        if (ui.address.isNotBlank() && ui.postcode.isNotBlank() && selectedAddress == null) {
            selectedAddress = AddressResult(
                address = ui.address,
                zonecode = ui.postcode
            )
        }
    }

    // 에러 발생 시 스크롤 + Snackbar
    LaunchedEffect(
        ui.nameError,
        ui.birthDateError,
        ui.genderError,
        ui.addressError,
        ui.generalError
    ) {
        val targetY = when {
            ui.nameError != null -> nameFieldY
            ui.birthDateError != null -> birthDateFieldY
            ui.genderError != null -> genderFieldY
            ui.addressError != null -> addressFieldY
            else -> null
        }

        if (targetY != null) {
            val scrollToY = (targetY - 100.dp.value).coerceAtLeast(0f)
            coroutineScope.launch {
                scrollState.animateScrollTo(scrollToY.toInt())
            }
        }

        val errorMessage = ui.nameError
            ?: ui.birthDateError
            ?: ui.genderError
            ?: ui.addressError
            ?: ui.generalError

        if (errorMessage != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "개인정보 수정",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.scaledSp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        actionColor = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    PersonalInfoFieldSimple(
                        label = "성함",
                        value = ui.name,
                        onValueChange = onNameChange,
                        placeholder = "성함을 입력해주세요",
                        errorMessage = ui.nameError,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            nameFieldY = coordinates.positionInParent().y
                        }
                    )

                    BirthDateField(
                        label = "생년월일",
                        value = ui.birthDate,
                        onValueChange = onBirthDateChange,
                        placeholder = "YYYY-MM-DD",
                        errorMessage = ui.birthDateError,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            birthDateFieldY = coordinates.positionInParent().y
                        }
                    )

                    PersonalInfoDropdown(
                        label = "성별",
                        value = Gender.entries.find { it.serverValue == ui.gender }?.korean ?: ui.gender,
                        options = Gender.entries.map { it.korean },
                        onValueChange = { korean ->
                            val serverValue = Gender.fromKorean(korean)?.serverValue ?: korean
                            onGenderChange(serverValue)
                        },
                        errorMessage = ui.genderError,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            genderFieldY = coordinates.positionInParent().y
                        }
                    )

                    // 주소 영역
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .onGloballyPositioned { coordinates ->
                                addressFieldY = coordinates.positionInParent().y
                            }
                    ) {
                        Text(
                            text = "주소",
                            fontSize = 14.scaledSp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAddressDialog = true }
                                .border(
                                    width = 1.dp,
                                    color = if (ui.addressError != null)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                if (selectedAddress != null) {
                                    Text(
                                        text = "[${selectedAddress!!.zonecode}]",
                                        fontSize = 14.scaledSp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = selectedAddress!!.address,
                                        fontSize = 14.scaledSp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Text(
                                        text = "주소를 검색해주세요",
                                        fontSize = 14.scaledSp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.alpha(0.6f)
                                    )
                                }
                            }
                        }

                        if (ui.addressError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ui.addressError,
                                fontSize = 12.scaledSp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showAddressDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "우편번호 찾기",
                                fontSize = 14.scaledSp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (ui.postcodeError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ui.postcodeError ?: "",
                                fontSize = 12.scaledSp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    PersonalInfoDropdown(
                        label = "결혼여부",
                        value = MaritalStatus.entries.find { it.serverValue == ui.maritalStatus }?.korean ?: ui.maritalStatus,
                        options = MaritalStatus.entries.map { it.korean },
                        onValueChange = { korean ->
                            val serverValue = MaritalStatus.fromKorean(korean)?.serverValue ?: korean
                            onMaritalStatusChange(serverValue)
                        }
                    )

                    PersonalInfoDropdown(
                        label = "학력",
                        value = EducationLevel.entries.find { it.serverValue == ui.education }?.korean ?: ui.education,
                        options = EducationLevel.entries.map { it.korean },
                        onValueChange = { korean ->
                            val serverValue = EducationLevel.fromKorean(korean)?.serverValue ?: korean
                            onEducationChange(serverValue)
                        }
                    )

                    PersonalInfoFieldSimple(
                        label = "가구원 수",
                        value = ui.householdSize,
                        onValueChange = onHouseholdSizeChange,
                        placeholder = "예: 4"
                    )

                    PersonalInfoFieldSimple(
                        label = "가구소득 (만원)",
                        value = ui.householdIncome,
                        onValueChange = onHouseholdIncomeChange,
                        placeholder = "예: 500"
                    )

                    PersonalInfoDropdown(
                        label = "취업상태",
                        value = EmploymentStatus.entries.find { it.serverValue == ui.employmentStatus }?.korean ?: ui.employmentStatus,
                        options = EmploymentStatus.entries.map { it.korean },
                        onValueChange = { korean ->
                            val serverValue = EmploymentStatus.fromKorean(korean)?.serverValue ?: korean
                            onEmploymentStatusChange(serverValue)
                        },
                        isLast = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    PersonalInfoTagSelectionSection(
                        selectedTags = ui.tags,
                        tagInput = ui.tagInput,
                        onTagInputChange = onTagInputChange,
                        onAddTag = onAddTag,
                        onRemoveTag = onRemoveTag,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (ui.generalError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ui.generalError,
                    fontSize = 12.scaledSp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !ui.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        "저장하기",
                        fontSize = 16.scaledSp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddressDialog) {
        KakaoAddressSearchDialog(
            onDismiss = { showAddressDialog = false },
            onAddressSelected = { result ->
                selectedAddress = result
                onAddressChange(result.address)
                onPostCodeChange(result.zonecode)
                showAddressDialog = false
            }
        )
    }
}

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

@Composable
fun PersonalInfoFieldSimple(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 24.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.scaledSp,
            fontWeight = FontWeight.Normal,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
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
            enabled = enabled,
            isError = errorMessage != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(8.dp)
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 12.scaledSp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoDropdown(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    errorMessage: String? = null,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 24.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.scaledSp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                placeholder = {
                    Text(
                        "선택해주세요",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.scaledSp
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                isError = errorMessage != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(8.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 12.scaledSp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalInfoTagSelectionSection(
    selectedTags: List<String>,
    tagInput: String,
    onTagInputChange: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val exampleTags = listOf(
        "독거노인",
        "저소득층",
        "장애인",
        "기초생활수급자",
        "국가유공자",
        "당뇨"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        // 직접 입력
        Text(
            text = "직접 입력",
            fontSize = 13.scaledSp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = tagInput,
                onValueChange = onTagInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "태그를 입력하세요",
                        fontSize = 14.scaledSp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            val canAdd = tagInput.trim().isNotEmpty()

            IconButton(
                onClick = { onAddTag(tagInput) },
                modifier = Modifier.size(48.dp),
                enabled = canAdd,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (canAdd)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "태그 추가"
                )
            }
        }

        // 예시 태그
        Text(
            text = "예시 태그",
            fontSize = 13.scaledSp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exampleTags.forEach { tag ->
                val isSelected = tag in selectedTags

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) onRemoveTag(tag) else onAddTag(tag)
                    },
                    label = {
                        Text(
                            text = tag,
                            fontSize = 14.scaledSp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                )
            }
        }

        // 선택된 태그
        if (selectedTags.isNotEmpty()) {
            Text(
                text = "추가한 태그",
                fontSize = 13.scaledSp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedTags.forEach { tag ->
                    AssistChip(
                        onClick = { onRemoveTag(tag) },
                        label = {
                            Text(
                                text = "#$tag",
                                fontSize = 14.scaledSp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "태그 제거",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            trailingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}
