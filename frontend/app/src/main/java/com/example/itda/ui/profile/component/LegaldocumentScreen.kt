package com.example.itda.ui.profile.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.scaledSp

// Strategy Pattern: 약관 타입별 다른 렌더링 전략
sealed class LegalDocumentType {
    abstract val title: String
    abstract val sections: List<LegalSection>
    abstract val effectiveDate: String?
    abstract val highlightColor: androidx.compose.ui.graphics.Color

    data class Terms(
        override val title: String = "이용약관",
        override val sections: List<LegalSection>,
        override val effectiveDate: String? = "2025년 1월 1일",
        override val highlightColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF4A90E2)
    ) : LegalDocumentType()

    data class Privacy(
        override val title: String = "개인정보 처리방침",
        override val sections: List<LegalSection>,
        override val effectiveDate: String? = "2025년 1월 1일",
        override val highlightColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF50C878)
    ) : LegalDocumentType()

    data class Consent(
        override val title: String,
        override val sections: List<LegalSection>,
        override val effectiveDate: String? = null,
        override val highlightColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFFFF8C42)
    ) : LegalDocumentType()
}

// Builder Pattern: 약관 섹션 구조 생성
data class LegalSection(
    val title: String,
    val content: String,
    val subsections: List<LegalSubsection> = emptyList(),
    val isImportant: Boolean = false
)

data class LegalSubsection(
    val title: String,
    val items: List<String>
)

// Template Method Pattern: 공통 레이아웃
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentScreen(
    documentType: LegalDocumentType,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        documentType.title,
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 헤더 정보
            DocumentHeader(documentType)

            Spacer(modifier = Modifier.height(24.dp))

            // 섹션들
            documentType.sections.forEachIndexed { index, section ->
                LegalSectionCard(
                    section = section,
                    sectionNumber = index + 1,
                    highlightColor = documentType.highlightColor
                )

                if (index < documentType.sections.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DocumentHeader(documentType: LegalDocumentType) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = documentType.highlightColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = documentType.highlightColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Text(
                text = documentType.title,
                fontSize = 18.scaledSp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        documentType.effectiveDate?.let { date ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "시행일: $date",
                fontSize = 13.scaledSp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LegalSectionCard(
    section: LegalSection,
    sectionNumber: Int,
    highlightColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 섹션 번호
                Text(
                    text = "제${sectionNumber}조",
                    fontSize = 13.scaledSp,
                    fontWeight = FontWeight.Bold,
                    color = highlightColor,
                    modifier = Modifier
                        .background(
                            color = highlightColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.title,
                        fontSize = 15.scaledSp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (section.isImportant) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "필독",
                            fontSize = 11.scaledSp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "접기" else "펼치기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 메인 내용
                if (section.content.isNotEmpty()) {
                    Text(
                        text = section.content,
                        fontSize = 14.scaledSp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }

                // 서브섹션들
                section.subsections.forEach { subsection ->
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = subsection.title,
                        fontSize = 14.scaledSp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    subsection.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "•",
                                fontSize = 14.scaledSp,
                                color = highlightColor,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = item,
                                fontSize = 14.scaledSp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}