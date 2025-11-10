package com.example.itda.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.itda.ui.auth.components.AddressResult
import com.example.itda.ui.auth.components.KakaoAddressSearchDialog
import com.example.itda.ui.common.theme.*
import com.example.itda.ui.common.theme.Neutral30
import kotlinx.coroutines.launch
import com.example.itda.ui.profile.PersonalInfoViewModel
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import com.example.itda.ui.auth.components.isValidBirthDate

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
    onSubmit: () -> Unit
) {
    // 주소 검색 다이얼로그 표시 여부
    var showAddressDialog by remember { mutableStateOf(false) }
    // 선택된 주소 정보
    var selectedAddress by remember { mutableStateOf<AddressResult?>(null) }

    // 스크롤 상태
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // 각 필드의 Y 위치를 저장
    var nameFieldY by remember { mutableFloatStateOf(0f) }
    var birthDateFieldY by remember { mutableFloatStateOf(0f) }
    var genderFieldY by remember { mutableFloatStateOf(0f) }
    var addressFieldY by remember { mutableFloatStateOf(0f) }

    // Snackbar 호스트
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔧 서버에서 불러온 주소가 있으면 selectedAddress 초기화
    LaunchedEffect(ui.address, ui.postcode) {
        if (ui.address.isNotBlank() && ui.postcode.isNotBlank() && selectedAddress == null) {
            selectedAddress = AddressResult(
                address = ui.address,
                zonecode = ui.postcode
            )
        }
    }

    // ✨ 에러 발생 시 자동 스크롤 및 Snackbar 표시
    LaunchedEffect(
        ui.nameError,
        ui.birthDateError,
        ui.genderError,
        ui.addressError,
        ui.generalError
    ) {
        // 첫 번째 에러 필드로 스크롤
        val targetY = when {
            ui.nameError != null -> nameFieldY
            ui.birthDateError != null -> birthDateFieldY
            ui.genderError != null -> genderFieldY
            ui.addressError != null -> addressFieldY
            else -> null
        }

        if (targetY != null) {
            // 약간 위쪽 여백을 두고 스크롤 (100dp)
            val scrollToY = (targetY - 100.dp.value).coerceAtLeast(0f)
            coroutineScope.launch {
                scrollState.animateScrollTo(scrollToY.toInt())
            }
        }

        // Snackbar로 에러 메시지 표시
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
                    // ✨ 위치 추적이 추가된 필드들
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

                    // 성별 드롭다운
                    PersonalInfoDropdown(
                        label = "성별",
                        value = ui.gender,
                        options = listOf("남성", "여성"),
                        onValueChange = onGenderChange,
                        errorMessage = ui.genderError,
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            genderFieldY = coordinates.positionInParent().y
                        }
                    )

                    // 주소 검색 영역
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

                        // 주소 표시 카드
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
<<<<<<< HEAD
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
=======
                                        fontSize = 14.scaledSp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
>>>>>>> e17001b (feat(fe) #122: implement fontsize variant)
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
<<<<<<< HEAD
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
=======
                                        fontSize = 14.scaledSp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.alpha(0.6f)
>>>>>>> e17001b (feat(fe) #122: implement fontsize variant)
                                    )
                                }
                            }
                        }

<<<<<<< HEAD
                        if (ui.addressError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ui.addressError,
                                fontSize = 12.sp,
=======
                        // 우편번호 찾기 버튼
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
>>>>>>> e17001b (feat(fe) #122: implement fontsize variant)
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    PersonalInfoDropdown(
                        label = "결혼여부",
                        value = ui.maritalStatus,
                        options = listOf("미혼", "기혼", "이혼/사별"),
                        onValueChange = onMaritalStatusChange
                    )

                    PersonalInfoDropdown(
                        label = "학력",
                        value = ui.education,
                        options = listOf(
                            "초등학생", "중학생", "고등학생", "대학생",
                            "초졸", "중졸", "고졸", "전문대졸", "대졸"
                        ),
                        onValueChange = onEducationChange
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
                        value = ui.employmentStatus,
                        options = listOf("재직자", "미취업자", "자의업자"),
                        onValueChange = onEmploymentStatusChange,
                        isLast = true
                    )
                }
            }

<<<<<<< HEAD
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
=======
                    if (ui.generalError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ui.generalError ?: "",
                            fontSize = 12.scaledSp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            onAddressChange(selectedAddress!!.address)
                            onPostCodeChange(selectedAddress!!.zonecode)
                            onSubmit()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !ui.isLoading
                    ) {
                        if (ui.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "완료",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 16.scaledSp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
>>>>>>> e17001b (feat(fe) #122: implement fontsize variant)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // 주소 검색 다이얼로그
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
    // ✨ 실시간 유효성 검사
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
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else null
        )

        if (displayError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
<<<<<<< HEAD
                text = displayError,
                fontSize = 12.sp,
=======
                text = errorMessage,
                fontSize = 12.scaledSp,
>>>>>>> e17001b (feat(fe) #122: implement fontsize variant)
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
    modifier: Modifier = Modifier  // ✨ modifier 추가
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
    modifier: Modifier = Modifier  // ✨ modifier 추가
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