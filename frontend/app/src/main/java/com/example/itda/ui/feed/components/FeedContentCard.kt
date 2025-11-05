package com.example.itda.ui.feed.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itda.ui.common.theme.Primary10
import com.example.itda.ui.common.theme.Primary40
import com.example.itda.ui.common.theme.Primary95
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun FeedContentCard(
    content : String
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            Modifier
                .padding(12.dp)
        ) {
            Text("📋 상세내용", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            MarkdownText(
                modifier = Modifier.padding(8.dp),
                markdown = content,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    lineHeight = 10.sp,
                    textAlign = TextAlign.Justify,
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFeedContentCard() {
    // 미리보기를 위한 더미 함수
    FeedContentCard(
        """
        ## 프로그램 개요
        국가 암검진 사업을 통해 암을 조기 발견, 치료를 유도함으로써 암의 치료율을 높이고 암으로 인한 사망을 줄입니다.\\n\\n**지원 대상**\\n암 종류별 대상자 기준(표준 검진 연령 및 성별) 및 검진주기는 다음과 같습니다. (위암) 40세 이상의 남성과 여성 / 2년 (간암) 40세 이상의 해당연도 전 2년간 간암발생고위험군 해당자 / 6개월 (대장암) 50세 이상의 남성과 여성 / 1년 (유방암) 40세 이상의 여성 / 2년 (자궁경부암) 20세 이상의 여성 / 2년 (폐암) 54세~74세 중 30갑년 이상의 흡연력을 가진 흡연자 중 기준 충족하는 경우 / 2년 암검진비용 중 수검자 본인부담금 지원 대상자는 다음과 같습니다. 「의료급여법」에 따른 의료급여수급권자 건강보험가입자 및 피부양자로서 당해연도 검진대상자 중 보험료 부과기준으로 직장가입자는 월 127,500원 이하, 지역가입자는 월57,000원 이하인 자(2024년 11월 기준)\\n\\n**신청 방법**\\n당해연도 암검진대상자로 선정된 경우 지정 암검진기관에서 암검진 수검이 가능합니다. 검진기관 안내 : 건강검진은 주소지와 관계없이 지정된 검진기관 전국 어디서나 받을 수 있습니다. * 검진기관 사정에 따라 예약이 조기에 마감될 수 있으니 사전 확인 및 예약 후 검진을 받으시기 바랍니다. 검진기관 찾기 : 국민건강보험공단 홈페이지 (www.nhis.or.kr)또는 The건강보험 모바일앱에서 가능합니다. * \\\"국민건강보험 > 건강iN >검진기관/병원찾기\\\" 메뉴를 통해 찾아볼 수 있습니다.\\n\\n**지원 내용**\\n위암, 간암, 대장암, 자궁경부암, 유방암, 폐암의 6종에 대한 검진을 실시합니다. 수검자 자부담 10%에 대한 비용을 지원합니다. - 건강보험가입자 상위 50% 대장암, 자궁경부암 대상자 - 의료급여수급권자 - 건강보험가입자 하위 50%"
            
        """.trimIndent()
    )
}
