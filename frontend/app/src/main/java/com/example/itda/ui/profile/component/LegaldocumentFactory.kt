package com.example.itda.ui.profile.component

object LegalDocumentFactory {

    fun createTermsOfService(): LegalDocumentType.Terms {
        return LegalDocumentType.Terms(
            sections = listOf(
                LegalSection(
                    title = "목적",
                    content = "이 약관은 잇다(이하 \"회사\")가 제공하는 정책 추천 서비스의 이용과 관련하여 회사와 회원 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.",
                    isImportant = true
                ),
                LegalSection(
                    title = "정의",
                    content = "본 약관에서 사용하는 용어의 정의는 다음과 같습니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "주요 용어",
                            items = listOf(
                                "\"서비스\"란 회사가 제공하는 정부 정책 정보 제공 및 추천 서비스를 말합니다.",
                                "\"회원\"이란 본 약관에 동의하고 회사와 이용계약을 체결한 자를 말합니다.",
                                "\"아이디(ID)\"란 회원 식별과 서비스 이용을 위해 회원이 설정하고 회사가 승인한 이메일 주소를 말합니다."
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "약관의 효력 및 변경",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "본 약관은 서비스를 이용하고자 하는 모든 회원에 대하여 그 효력이 발생합니다.",
                                "회사는 필요한 경우 관련 법령을 위배하지 않는 범위 내에서 본 약관을 변경할 수 있습니다.",
                                "약관이 변경되는 경우 변경 내용을 시행일 7일 전부터 공지합니다."
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "서비스의 제공 및 변경",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "회사는 다음과 같은 서비스를 제공합니다",
                            items = listOf(
                                "정부 및 지자체 정책 정보 제공",
                                "개인 맞춤형 정책 추천",
                                "정책 검색 및 즐겨찾기",
                                "정책 알림 서비스"
                            )
                        ),
                        LegalSubsection(
                            title = "서비스 변경",
                            items = listOf(
                                "회사는 상당한 이유가 있는 경우 운영상, 기술상의 필요에 따라 제공하고 있는 서비스를 변경할 수 있습니다."
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "서비스의 중단",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "다음과 같은 경우 서비스 제공을 일시적으로 중단할 수 있습니다",
                            items = listOf(
                                "컴퓨터 등 정보통신설비의 보수점검, 교체 및 고장",
                                "통신의 두절 등의 사유가 발생한 경우",
                                "천재지변 또는 이에 준하는 불가항력"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "회원가입",
                    content = "",
                    isImportant = true,
                    subsections = listOf(
                        LegalSubsection(
                            title = "가입 절차",
                            items = listOf(
                                "이용자는 회사가 정한 가입 양식에 따라 회원정보를 기입한 후 이 약관에 동의한다는 의사표시를 함으로써 회원가입을 신청합니다."
                            )
                        ),
                        LegalSubsection(
                            title = "다음 각 호에 해당하는 경우 회원 등록을 거부할 수 있습니다",
                            items = listOf(
                                "가입신청자가 이전에 회원자격을 상실한 적이 있는 경우",
                                "실명이 아니거나 타인의 명의를 이용한 경우",
                                "허위의 정보를 기재하거나, 회사가 제시하는 내용을 기재하지 않은 경우"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "회원탈퇴 및 자격 상실",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "회원 탈퇴",
                            items = listOf(
                                "회원은 언제든지 탈퇴를 요청할 수 있으며, 회사는 즉시 회원탈퇴를 처리합니다."
                            )
                        ),
                        LegalSubsection(
                            title = "다음의 경우 회원자격을 제한 및 정지할 수 있습니다",
                            items = listOf(
                                "가입 신청 시에 허위 내용을 등록한 경우",
                                "다른 사람의 서비스 이용을 방해하거나 그 정보를 도용하는 경우",
                                "법령 또는 이 약관이 금지하거나 공서양속에 반하는 행위를 하는 경우"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "개인정보보호",
                    content = "회사는 관련 법령이 정하는 바에 따라 회원의 개인정보를 보호하기 위해 노력합니다. 개인정보의 보호 및 이용에 대해서는 관련 법령 및 회사의 개인정보처리방침이 적용됩니다.",
                    isImportant = true
                ),
                LegalSection(
                    title = "회사의 의무",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "회사는 법령과 본 약관이 금지하거나 공서양속에 반하는 행위를 하지 않으며 본 약관이 정하는 바에 따라 지속적이고, 안정적으로 서비스를 제공하기 위해 노력합니다.",
                                "회사는 회원이 안전하게 서비스를 이용할 수 있도록 개인정보 보호를 위한 보안시스템을 갖추어야 합니다."
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "회원의 의무",
                    content = "회원은 다음 행위를 하여서는 안 됩니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "금지 행위",
                            items = listOf(
                                "신청 또는 변경 시 허위내용의 등록",
                                "타인의 정보 도용",
                                "회사가 게시한 정보의 변경",
                                "회사 기타 제3자의 저작권 등 지적재산권에 대한 침해",
                                "회사 기타 제3자의 명예를 손상시키거나 업무를 방해하는 행위",
                                "외설 또는 폭력적인 메시지, 화상, 음성 등을 서비스에 공개 또는 게시하는 행위"
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "면책조항",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "회사는 천재지변 또는 이에 준하는 불가항력으로 인하여 서비스를 제공할 수 없는 경우에는 서비스 제공에 관한 책임이 면제됩니다.",
                                "회사는 회원의 귀책사유로 인한 서비스 이용의 장애에 대하여는 책임을 지지 않습니다.",
                                "회사는 회원이 서비스를 이용하여 기대하는 수익을 상실한 것에 대하여 책임을 지지 않습니다."
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "분쟁해결",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "회사는 이용자가 제기하는 정당한 의견이나 불만을 반영하고 그 피해를 보상처리하기 위하여 피해보상처리기구를 설치·운영합니다.",
                                "회사와 이용자 간에 발생한 분쟁에 관한 소송은 제소 당시의 이용자의 주소에 의하고, 주소가 없는 경우에는 거소를 관할하는 지방법원의 전속관할로 합니다."
                            )
                        )
                    )
                )
            )
        )
    }

    fun createPrivacyPolicy(): LegalDocumentType.Privacy {
        return LegalDocumentType.Privacy(
            sections = listOf(
                LegalSection(
                    title = "개인정보의 처리 목적",
                    content = "회사는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는 경우에는 별도의 동의를 받는 등 필요한 조치를 이행할 예정입니다.",
                    isImportant = true,
                    subsections = listOf(
                        LegalSubsection(
                            title = "회원 가입 및 관리",
                            items = listOf(
                                "회원 가입의사 확인",
                                "회원제 서비스 제공에 따른 본인 식별·인증",
                                "회원자격 유지·관리",
                                "서비스 부정이용 방지"
                            )
                        ),
                        LegalSubsection(
                            title = "맞춤형 서비스 제공",
                            items = listOf(
                                "이용자의 연령, 거주지역, 가구특성 등을 고려한 맞춤형 정책 정보 추천"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "개인정보의 처리 및 보유 기간",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "회사는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를 수집 시에 동의받은 개인정보 보유·이용기간 내에서 개인정보를 처리·보유합니다.",
                                "회원 가입 및 관리: 회원 탈퇴 시까지",
                                "단, 관계 법령 위반에 따른 수사·조사 등이 진행 중인 경우에는 해당 수사·조사 종료 시까지"
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "처리하는 개인정보의 항목",
                    content = "회사는 다음의 개인정보 항목을 처리하고 있습니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "필수항목",
                            items = listOf(
                                "이메일 주소",
                                "비밀번호",
                                "이름",
                                "생년월일",
                                "성별",
                                "주소",
                                "우편번호"
                            )
                        ),
                        LegalSubsection(
                            title = "선택항목",
                            items = listOf(
                                "결혼 여부",
                                "학력",
                                "가구원 수",
                                "가구 소득",
                                "취업 상태"
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "개인정보의 제3자 제공",
                    content = "회사는 정보주체의 개인정보를 제1조(개인정보의 처리 목적)에서 명시한 범위 내에서만 처리하며, 정보주체의 동의, 법률의 특별한 규정 등에 해당하는 경우에만 개인정보를 제3자에게 제공합니다."
                ),
                LegalSection(
                    title = "개인정보처리의 위탁",
                    content = "회사는 원활한 개인정보 업무처리를 위하여 다음과 같이 개인정보 처리업무를 위탁하고 있습니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "수탁업체 정보",
                            items = listOf(
                                "수탁업체: AWS (Amazon Web Services)",
                                "위탁업무 내용: 서버 운영 및 데이터 보관"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "정보주체의 권리·의무",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "정보주체는 다음의 권리를 행사할 수 있습니다",
                            items = listOf(
                                "개인정보 열람 요구",
                                "오류 등이 있을 경우 정정 요구",
                                "삭제 요구",
                                "처리정지 요구"
                            )
                        ),
                        LegalSubsection(
                            title = "권리 행사 방법",
                            items = listOf(
                                "서면, 전화, 전자우편 등을 통하여 하실 수 있으며 회사는 이에 대해 지체없이 조치하겠습니다."
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "개인정보의 파기",
                    content = "회사는 개인정보 보유기간의 경과, 처리목적 달성 등 개인정보가 불필요하게 되었을 때에는 지체없이 해당 개인정보를 파기합니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "파기 절차",
                            items = listOf(
                                "불필요하게 된 개인정보는 개인정보 보호책임자의 승인 절차를 거쳐 파기합니다."
                            )
                        ),
                        LegalSubsection(
                            title = "파기 방법",
                            items = listOf(
                                "전자적 파일 형태: 복원이 불가능한 방법으로 영구 삭제",
                                "기록물, 인쇄물, 서면 등: 분쇄 또는 소각"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "개인정보의 안전성 확보조치",
                    content = "회사는 개인정보의 안전성 확보를 위해 다음과 같은 조치를 취하고 있습니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "보안 조치",
                            items = listOf(
                                "관리적 조치: 내부관리계획 수립·시행, 정기적 직원 교육",
                                "기술적 조치: 접근권한 관리, 접근통제시스템 설치, 암호화, 보안프로그램 설치",
                                "물리적 조치: 전산실, 자료보관실 등의 접근통제"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "개인정보 보호책임자",
                    content = "회사는 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와 관련한 정보주체의 불만처리 및 피해구제 등을 위하여 아래와 같이 개인정보 보호책임자를 지정하고 있습니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "개인정보 보호책임자",
                            items = listOf(
                                "성명: 홍길동",
                                "직책: 개인정보보호팀장",
                                "연락처: privacy@itda.com"
                            )
                        )
                    )
                )
            )
        )
    }

    fun createPersonalInfoConsent(): LegalDocumentType.Consent {
        return LegalDocumentType.Consent(
            title = "개인정보 수집/이용 동의 (맞춤정책)",
            sections = listOf(
                LegalSection(
                    title = "수집·이용 목적",
                    content = "맞춤형 정책 추천 서비스 제공을 위해 개인정보를 수집·이용합니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "구체적 목적",
                            items = listOf(
                                "이용자의 특성에 맞는 맞춤형 정부 정책 정보 추천",
                                "정책 적용 대상자 판단",
                                "서비스 개선 및 통계 분석"
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "수집하는 개인정보 항목",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "필수항목",
                            items = listOf(
                                "이메일, 이름, 생년월일, 성별, 주소, 우편번호"
                            )
                        ),
                        LegalSubsection(
                            title = "선택항목",
                            items = listOf(
                                "결혼 여부, 학력, 가구원 수, 가구 소득, 취업 상태"
                            )
                        ),
                        LegalSubsection(
                            title = "안내사항",
                            items = listOf(
                                "선택항목을 제공하지 않아도 서비스 이용이 가능하나, 더욱 정확한 맞춤형 정책 추천을 위해서는 선택항목 제공을 권장합니다."
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "보유 및 이용 기간",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "회원 탈퇴 시 또는 동의 철회 시까지",
                                "단, 관련 법령에 따라 보존할 필요가 있는 경우 해당 기간 동안 보관"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "동의 거부 권리 및 불이익",
                    content = "귀하는 개인정보 수집·이용에 대한 동의를 거부할 권리가 있습니다. 다만, 필수항목의 수집·이용에 동의하지 않을 경우 회원가입 및 서비스 이용이 제한될 수 있습니다.",
                    isImportant = true
                )
            )
        )
    }

    fun createSensitiveInfoConsent(): LegalDocumentType.Consent {
        return LegalDocumentType.Consent(
            title = "민감정보 수집/이용 동의 (맞춤정책)",
            sections = listOf(
                LegalSection(
                    title = "수집·이용 목적",
                    content = "「개인정보 보호법」 제23조에 따라 민감정보를 수집·이용하고자 합니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "목적",
                            items = listOf(
                                "장애인, 노인, 저소득층 등 정책 대상자 파악",
                                "복지 및 지원 정책 맞춤 추천",
                                "정책 수혜 대상자 확인"
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "수집하는 민감정보 항목",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "선택항목",
                            items = listOf(
                                "건강 정보 (장애 여부, 질병 정보 등)",
                                "경제적 정보 (국민기초생활수급 여부, 차상위계층 여부 등)"
                            )
                        ),
                        LegalSubsection(
                            title = "안내사항",
                            items = listOf(
                                "본 항목은 선택사항이며, 동의하지 않아도 기본 서비스 이용이 가능합니다.",
                                "다만, 동의하지 않을 경우 일부 복지 정책 추천이 제한될 수 있습니다."
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "보유 및 이용 기간",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "회원 탈퇴 시 또는 동의 철회 시까지",
                                "관련 법령에 따라 보존할 필요가 있는 경우 해당 기간 동안 보관"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "민감정보의 안전성 확보 조치",
                    content = "회사는 민감정보 보호를 위해 다음과 같은 조치를 취하고 있습니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "보안 조치",
                            items = listOf(
                                "민감정보의 암호화 저장",
                                "접근 권한의 최소화 및 접근 통제",
                                "민감정보 접근 기록의 보관 및 점검"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "동의 거부 권리 및 불이익",
                    content = "귀하는 민감정보 수집·이용에 대한 동의를 거부할 권리가 있습니다. 민감정보 수집·이용에 동의하지 않아도 기본 서비스 이용이 가능하나, 일부 복지 정책 추천 서비스가 제한될 수 있습니다.",
                    isImportant = true
                )
            )
        )
    }

    fun createLocationConsent(): LegalDocumentType.Consent {
        return LegalDocumentType.Consent(
            title = "위치기반서비스 이용약관",
            sections = listOf(
                LegalSection(
                    title = "목적",
                    content = "본 약관은 잇다가 제공하는 위치기반서비스와 관련하여 회사와 이용자간의 권리·의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다."
                ),
                LegalSection(
                    title = "서비스의 내용",
                    content = "회사가 제공하는 위치기반서비스의 내용은 다음과 같습니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "제공 서비스",
                            items = listOf(
                                "이용자의 현재 위치를 기반으로 한 지역별 정책 정보 제공",
                                "이용자 거주 지역의 지자체 정책 추천",
                                "위치 기반 맞춤형 복지 서비스 안내"
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "서비스 이용요금",
                    content = "회사가 제공하는 위치기반서비스는 무료입니다. 단, 무선 서비스 이용 시 발생하는 데이터 통신료는 별도이며 이용자가 가입한 통신사의 정책에 따릅니다."
                ),
                LegalSection(
                    title = "개인위치정보의 이용 또는 제공",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "회사는 개인위치정보를 이용하여 서비스를 제공하고자 하는 경우 본 이용약관에 대한 동의를 받습니다.",
                                "회사는 이용자의 동의 없이 개인위치정보를 제3자에게 제공하지 않습니다."
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "개인위치정보주체의 권리",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "이용자의 권리",
                            items = listOf(
                                "언제든지 개인위치정보의 수집·이용·제공에 대한 동의를 철회할 수 있습니다.",
                                "언제든지 개인위치정보의 수집·이용·제공의 일시적인 중지를 요구할 수 있습니다.",
                                "개인위치정보 수집·이용·제공사실 확인자료에 대한 열람 또는 고지를 요구할 수 있습니다."
                            )
                        )
                    ),
                    isImportant = true
                )
            )
        )
    }

    fun createMarketingConsent(): LegalDocumentType.Consent {
        return LegalDocumentType.Consent(
            title = "마케팅 정보 수신 동의",
            sections = listOf(
                LegalSection(
                    title = "개인정보의 수집·이용 목적",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "마케팅 활용 목적",
                            items = listOf(
                                "신규 서비스 및 이벤트 정보 안내",
                                "맞춤형 서비스 및 상품 추천",
                                "정책 업데이트 알림",
                                "설문조사 및 이벤트 참여 기회 제공"
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "수집하는 개인정보 항목",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "이메일 주소",
                                "이름",
                                "서비스 이용 기록"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "개인정보의 보유 및 이용기간",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "",
                            items = listOf(
                                "동의일로부터 회원 탈퇴 시 또는 마케팅 동의 철회 시까지",
                                "단, 관계 법령에 따라 보존할 필요가 있는 경우 해당 법령에서 정한 기간 동안 보관"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "마케팅 정보 수신 방법",
                    content = "",
                    subsections = listOf(
                        LegalSubsection(
                            title = "수신 채널",
                            items = listOf(
                                "이메일",
                                "앱 푸시 알림",
                                "문자 메시지 (SMS/MMS)"
                            )
                        )
                    )
                ),
                LegalSection(
                    title = "마케팅 수신 동의 철회",
                    content = "이용자는 언제든지 다음의 방법으로 마케팅 정보 수신 동의를 철회할 수 있습니다.",
                    subsections = listOf(
                        LegalSubsection(
                            title = "철회 방법",
                            items = listOf(
                                "앱 내 설정 메뉴에서 수신 거부 설정",
                                "수신한 이메일 하단의 '수신거부' 링크 클릭",
                                "고객센터를 통한 수신 거부 요청"
                            )
                        )
                    ),
                    isImportant = true
                ),
                LegalSection(
                    title = "동의 거부 권리 및 불이익",
                    content = "이용자는 마케팅 정보 수신에 대한 동의를 거부할 권리가 있습니다. 동의를 거부하시는 경우에도 회사가 제공하는 서비스를 이용하실 수 있으나, 마케팅 정보를 받아보실 수 없습니다."
                )
            )
        )
    }
}