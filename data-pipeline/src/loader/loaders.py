import json
import re
import time

from abc import ABC, abstractmethod
from typing import Any, List, Set, Dict, Optional

import requests
from tqdm import tqdm


from utils.config import (
    BOKJIRO_ENDPOINT,
    BOKJIRO_UUID_ENDPOINT,
    BOKJIRO_SESSION_ENDPOINT,
    YOUTH_CENTER_ENDPOINT,
)


class Loader(ABC):
    def __init__(
        self,
        page_size: int,
        max_page_num: int,
        api_key: str = None,
        prev_uuids: Optional[Set[str]] = None,
    ):
        self.api_key = api_key
        self.prev_uuids = prev_uuids

        self.page_size = page_size
        self.max_page_num = max_page_num

    @abstractmethod
    def load(self) -> list[int]:
        raise NotImplementedError


class YouthCenterLoader(Loader):
    def _find_duplicate(self, programs: List[Dict[str, Any]]) -> int:
        if not self.prev_uuids:
            return -1

        for i, program in enumerate(programs):
            if program["plcyNo"] in self.prev_uuids:
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


class BokjiroLoader(Loader):
    def _start_session(self) -> requests.Session:
        headers = {
            "Accept": "*/*",
            "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
            "Content-Type": "application/json; charset=UTF-8",
            "Host": "www.bokjiro.go.kr",
            "Origin": "https://www.bokjiro.go.kr",
            "Referer": BOKJIRO_SESSION_ENDPOINT,
            "Sec-Fetch-Dest": "empty",
            "Sec-Fetch-Mode": "cors",
            "Sec-Fetch-Site": "same-origin",
            "X-Requested-With": "XMLHttpRequest",
            "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36",
        }

        session = requests.Session()
        session.headers.update(headers)

        response = session.get(BOKJIRO_SESSION_ENDPOINT)
        response.raise_for_status()

        return session

    def _load_uuids(
        self, session: requests.Session, page: int, targets: List[str]
    ) -> Dict[str, List[str]]:
        payload = {
            "dmSearchParam": {
                "page": str(page),
                "onlineYn": "",
                "searchTerm": "",
                "tabId": "1",
                "orderBy": "date",
                "bkjrLftmCycCd": "",
                "daesang": "",
                "period": ",".join(targets),
                "age": "",
                "region": "",
                "jjim": "",
                "subject": "",
                "favoriteKeyword": "",
                "sidoCd": "",
                "sggCd": "",
                "endYn": "",
            },
            "dmScr": {
                "curScrId": "tbu/app/twat/twata/twataa/TWAT52005M",
                "befScrId": "",
            },
        }

        response = session.post(
            BOKJIRO_UUID_ENDPOINT, data=json.dumps(payload), timeout=10
        )
        response.raise_for_status()
        response_json = response.json()

        central_services = response_json["dsServiceList1"]
        local_services = response_json["dsServiceList2"]

        central_uuids = [data["WLFARE_INFO_ID"] for data in central_services]
        local_uuids = [data["WLFARE_INFO_ID"] for data in local_services]

        uuids = {
            "local": local_uuids,
            "central": central_uuids,
        }

        return uuids

    def _load_programs(
        self, session: requests.Session, uuids: List[str], page: int, operating: str
    ) -> List[Dict[str, Any]]:
        programs = []

        for uuid in tqdm(uuids, desc=f"(BokjoroLoader) {operating}, page {page}"):
            response = session.get(BOKJIRO_ENDPOINT.format(uuid))
            response.raise_for_status()
            time.sleep(1)

            html_bytes = response.content
            html_text = html_bytes.decode("utf-8")

            match = re.search(
                r"cpr\.core\.Platform\.INSTANCE\.initParameter\((.*?)\);cpr\.core\.Platform\.INSTANCE\.lookup",
                html_text,
                re.DOTALL,
            )

            if not match:
                continue

            data_json = json.loads(match.group(1))
            program = json.loads(data_json["initValue"]["dmWlfareInfo"])

            program["program_operating_entity"] = operating

            programs.append(program)

        return programs

    def load(self) -> List[Dict[str, Any]]:
        programs = []

        session = self._start_session()

        page = 1
        targets = ["중장년", "노년"]

        while page <= self.max_page_num:
            uuids = self._load_uuids(session=session, page=page, targets=targets)
            time.sleep(1)

            central_program_batch = self._load_programs(
                session=session, uuids=uuids["central"], page=page, operating="central"
            )
            local_program_batch = self._load_programs(
                session=session, uuids=uuids["local"], page=page, operating="local"
            )

            programs.extend(central_program_batch)
            programs.extend(local_program_batch)

            page += 1

        return programs

    def __str__(self):
        return "BokjiroLoader"
