package com.example.itda.ui.auth.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.itda.data.model.AddressDocument
import com.example.itda.data.source.remote.KakaoApiClient
import com.example.itda.ui.common.theme.*
import kotlinx.coroutines.launch


/**
 * 카카오 로컬 API를 사용하는 주소 검색 다이얼로그
 */

data class AddressResult(
    val zonecode: String,    // 우편번호
    val address: String      // 도로명 주소
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KakaoAddressSearchDialog(
    onDismiss: () -> Unit,
    onAddressSelected: (AddressResult) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AddressDocument>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // 주소 검색 함수
    fun searchAddress(query: String) {
        if (query.isBlank()) {
            errorMessage = "검색어를 입력해주세요"
            return
        }

        scope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = KakaoApiClient.kakaoLocalService.searchAddress(
                    authorization = "KakaoAK ${KakaoApiClient.REST_API_KEY}",
                    query = query
                )

                searchResults = response.documents

                if (searchResults.isEmpty()) {
                    errorMessage = "검색 결과가 없습니다"
                }

                Log.d("AddressSearch", "검색 성공: ${searchResults.size}개")
            } catch (e: Exception) {
                Log.e("AddressSearch", "검색 실패", e)
                errorMessage = "주소 검색에 실패했습니다: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 상단 바
                TopAppBar(
                    title = {
                        Text(
                            text = "주소 검색",
                            fontSize = 18.scaledSp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // 검색창
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("예: 관악구 관악로 1") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "지우기"
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            searchAddress(searchQuery)
                            keyboardController?.hide()
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // 검색 버튼
                Button(
                    onClick = {
                        searchAddress(searchQuery)
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    enabled = !isLoading && searchQuery.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("검색", fontSize = 16.scaledSp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 검색 전 Tip 표시
                if (searchResults.isEmpty() && !isLoading && errorMessage == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💡",
                                    fontSize = 16.scaledSp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = "검색 Tip",
                                    fontSize = 14.scaledSp,
                                    fontWeight = FontWeight.Bold,
                                    color = Neutral10
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "정확한 검색을 위해 아래 형식으로 입력해주세요",
                                fontSize = 13.scaledSp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // 예시들
                            TipExample("도로명 + 건물번호", "판교역로 166")
                            Spacer(modifier = Modifier.height(6.dp))
                            TipExample("도로명 + 건물번호", "동일로 216길 92")
                            Spacer(modifier = Modifier.height(6.dp))
                            TipExample("동/리 + 번지", "백현동 532")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 에러 메시지
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.scaledSp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 검색 결과는 있지만 모두 우편번호가 없는 경우
                val hasResults = searchResults.isNotEmpty()
                val allResultsWithoutZipcode = hasResults && searchResults.all {
                    (it.roadAddress?.zoneNo.isNullOrBlank()) &&
                            (it.address?.zipCode.isNullOrBlank())
                }

                if (allResultsWithoutZipcode) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "⚠️ 우편번호를 찾을 수 없습니다",
                                fontSize = 13.scaledSp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "건물번호까지 입력해주세요 (예: 동일로 216길 49)",
                                fontSize = 12.scaledSp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // 검색 결과 리스트
                if (searchResults.isNotEmpty()) {
                    Text(
                        text = "검색 결과 (${searchResults.size}개)",
                        fontSize = 14.scaledSp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(searchResults) { document ->
                        AddressItem(
                            document = document,
                            onClick = {
                                // 도로명 주소 우선, 없으면 지번 주소
                                val roadAddress = document.roadAddress
                                val address = document.address

                                // 우편번호 확인
                                if (roadAddress != null && roadAddress.zoneNo.isNotBlank()) {
                                    // 도로명 주소 + 우편번호 있음
                                    onAddressSelected(
                                        AddressResult(
                                            zonecode = roadAddress.zoneNo,
                                            address = roadAddress.addressName
                                        )
                                    )
                                } else if (address != null && !address.zipCode.isNullOrBlank()) {
                                    // 지번 주소 + 우편번호 있음
                                    onAddressSelected(
                                        AddressResult(
                                            zonecode = address.zipCode,
                                            address = address.addressName
                                        )
                                    )
                                } else {
                                    // 우편번호 없음
                                    errorMessage = "이 주소는 우편번호 정보가 없습니다.\n건물번호까지 입력해주세요. (예: 동일로 216길 92)"
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

/**
 * 주소 검색 결과 아이템
 */
@Composable
private fun AddressItem(
    document: AddressDocument,
    onClick: () -> Unit
) {
    val hasZipCode = (document.roadAddress?.zoneNo?.isNotBlank() == true) ||
            (!document.address?.zipCode.isNullOrBlank())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = hasZipCode, onClick = onClick)
            .padding(16.dp)
            .alpha(if (hasZipCode) 1f else 0.6f)
    ) {
        // 도로명 주소
        document.roadAddress?.let { roadAddress ->
            Text(
                text = "[도로명] ${roadAddress.addressName}",
                fontSize = 15.scaledSp,
                fontWeight = FontWeight.Medium,
                color = Neutral10
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (roadAddress.zoneNo.isNotBlank()) {
                Text(
                    text = "우편번호: ${roadAddress.zoneNo}",
                    fontSize = 13.scaledSp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "⚠️ 우편번호 없음 (건물번호까지 입력하세요)",
                    fontSize = 12.scaledSp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // 지번 주소 (도로명이 없을 때만)
        if (document.roadAddress == null) {
            document.address?.let { address ->
                Text(
                    text = "[지번] ${address.addressName}",
                    fontSize = 15.scaledSp,
                    fontWeight = FontWeight.Medium,
                    color = Neutral10
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (!address.zipCode.isNullOrBlank()) {
                    Text(
                        text = "우편번호: ${address.zipCode}",
                        fontSize = 13.scaledSp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "⚠️ 우편번호 없음",
                        fontSize = 12.scaledSp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Tip 예시 컴포넌트
 */
@Composable
private fun TipExample(
    title: String,
    example: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            fontSize = 14.scaledSp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        )
        Column {
            Text(
                text = title,
                fontSize = 11.scaledSp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "예) $example",
                fontSize = 12.scaledSp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
