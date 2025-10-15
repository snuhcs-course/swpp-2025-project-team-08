from abc import ABC, abstractmethod
from typing import Any, List, Set, Dict, Optional

import requests

from config import YOUTH_CENTER_ENDPOINT


class APILoader(ABC):
    def __init__(
        self,
        api_key: str,
        page_size: int,
        max_page_num: int,
        previously_loaded_ids: Optional[Set[str]] = None,
    ):
        self.api_key = api_key
        self.previously_loaded_ids = previously_loaded_ids

        self.page_size = page_size
        self.max_page_num = max_page_num

    @abstractmethod
    def load(self) -> list[int]:
        raise NotImplementedError


class YouthCenterLoader(APILoader):
    def _find_duplicate(self, programs: List[Dict[str, Any]]) -> int:
        if not self.previously_loaded_ids:
            return -1

        for i, program in enumerate(programs):
            if program["plcyNo"] in self.previously_loaded_ids:
                return i

        return -1

    def _trim(self, programs: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        results = []

        for program in programs:
            results.append(
                {
                    "uuid": program["plcyNo"],
                    "기본계획차수": program["bscPlanCycl"],
                    "기본계획정책방향번호": program["bscPlanPlcyWayNo"],
                    "기본계획중점과제번호": program["bscPlanFcsAsmtNo"],
                    "기본계획과제번호": program["bscPlanAsmtNo"],
                    "제공기관그룹코드": program["pvsnInstGroupCd"],
                    "정책제공방법코드": program["plcyPvsnMthdCd"],
                    "정책승인상태코드": program["plcyAprvSttsCd"],
                    "정책명": program["plcyNm"],
                    "정책키워드명": program["plcyKywdNm"],
                    "정책설명내용": program["plcyExplnCn"],
                    "정책대분류명": program["lclsfNm"],
                    "정책중분류명": program["mclsfNm"],
                    "정책지원내용": program["plcySprtCn"],
                    "주관기관코드": program["sprvsnInstCd"],
                    "주관기관코드명": program["sprvsnInstCdNm"],
                    "주관기관담당자명": program["sprvsnInstPicNm"],
                    "운영기관코드": program["operInstCd"],
                    "운영기관코드명": program["operInstCdNm"],
                    "운영기관담당자명": program["operInstPicNm"],
                    "지원규모제한여부": program["sprtSclLmtYn"],
                    "신청기간구분코드": program["aplyPrdSeCd"],
                    "사업기간구분코드": program["bizPrdSeCd"],
                    "사업기간시작일자": program["bizPrdBgngYmd"],
                    "사업기간종료일자": program["bizPrdEndYmd"],
                    "사업기간기타내용": program["bizPrdEtcCn"],
                    "정책신청방법내용": program["plcyAplyMthdCn"],
                    "심사방법내용": program["srngMthdCn"],
                    "신청URL주소": program["aplyUrlAddr"],
                    "제출서류내용": program["sbmsnDcmntCn"],
                    "기타사항내용": program["etcMttrCn"],
                    "참고URL주소1": program["refUrlAddr1"],
                    "참고URL주소2": program["refUrlAddr2"],
                    "지원규모수": program["sprtSclCnt"],
                    "지원도착순서여부": program["sprtArvlSeqYn"],
                    "지원대상최소연령": program["sprtTrgtMinAge"],
                    "지원대상최대연령": program["sprtTrgtMaxAge"],
                    "지원대상연령제한여부": program["sprtTrgtAgeLmtYn"],
                    "결혼상태코드": program["mrgSttsCd"],
                    "소득조건구분코드": program["earnCndSeCd"],
                    "소득최소금액": program["earnMinAmt"],
                    "소득최대금액": program["earnMaxAmt"],
                    "소득기타내용": program["earnEtcCn"],
                    "추가신청자격조건내용": program["addAplyQlfcCndCn"],
                    "참여제안대상내용": program["ptcpPrpTrgtCn"],
                    "조회수": program["inqCnt"],
                    "등록자기관코드": program["rgtrInstCd"],
                    "등록자기관코드명": program["rgtrInstCdNm"],
                    "등록자상위기관코드": program["rgtrUpInstCd"],
                    "등록자상위기관코드명": program["rgtrUpInstCdNm"],
                    "등록자최상위기관코드": program["rgtrHghrkInstCd"],
                    "등록자최상위기관코드명": program["rgtrHghrkInstCdNm"],
                    "정책거주지역코드": program["zipCd"],
                    "정책전공요건코드": program["plcyMajorCd"],
                    "정책취업요건코드": program["jobCd"],
                    "정책학력요건코드": program["schoolCd"],
                    "신청기간": program["aplyYmd"],
                    "최초등록일시": program["frstRegDt"],
                    "최종수정일시": program["lastMdfcnDt"],
                }
            )

        return results

    def load(self) -> List[Dict[str, Any]]:
        results = []
        payload = {
            "apiKeyNm": self.api_key,
            "rtnType": "json",
            "pageNum": 1,
            "pageSize": self.page_size,
            "pageType": 2,
        }

        while payload["pageNum"] <= self.max_page_num:
            try:
                response = requests.get(
                    YOUTH_CENTER_ENDPOINT, params=payload, timeout=10
                )
                response.raise_for_status()
            except requests.exceptions.HTTPError as e:
                status_code = e.response.status_code
                reason = e.response.reason
                print(f"HTTP {status_code} on page {payload['pageNum']}: {reason}")
                break
            except requests.exceptions.RequestException as e:
                print(f"Unexpected Request Error on page {payload["pageNum"]}: {e}")
                break

            response_json = response.json()
            batch = response_json["result"]["youthPolicyList"]

            dup_index = self._find_duplicate(batch)
            if dup_index != -1:
                results.extend(batch[:dup_index])
                break

            results.extend(batch)
            payload["pageNum"] += 1

        results = self._trim(results)

        return results

    def __str__(self):
        return "YouthCenterLoader"
