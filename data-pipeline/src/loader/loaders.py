import json
import re
import time

from abc import ABC, abstractmethod
from typing import Any, List, Set, Dict, Optional

from bs4 import BeautifulSoup
import requests


from utils.config import (
    BOKJIRO_PROGRAM_ENDPOINT,
    BOKJIRO_UUID_ENDPOINT,
    BOKJIRO_SESSION_ENDPOINT,
    SUBSIDY24_PROGRAM_ENDPOINT,
    SUBSIDY24_UUID_ENDPOINT,
)


class Loader(ABC):
    def __init__(
        self,
        max_page: int,
        api_key: str = None,
        prev_uuids: Optional[Set[str]] = None,
    ):
        self.api_key = api_key
        self.prev_uuids = prev_uuids
        self.max_page = max_page

    @abstractmethod
    def load(self, page: int) -> list[int]:
        raise NotImplementedError


class BokjiroLoader(Loader):
    def __init__(self, max_page, api_key=None, prev_uuids=None):
        super().__init__(max_page, api_key, prev_uuids)

        self.session = self._start_session()

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

    def _load_uuids(self, page: int, targets: List[str]) -> Dict[str, List[str]]:
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

        response = self.session.post(
            BOKJIRO_UUID_ENDPOINT, data=json.dumps(payload), timeout=10
        )
        time.sleep(1)
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

    def _load_programs(self, uuids: List[str], operating: str) -> List[Dict[str, Any]]:
        programs = []

        for uuid in uuids:
            endpoint = BOKJIRO_PROGRAM_ENDPOINT.format(uuid)

            response = self.session.get(endpoint)
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
            program["reference_url"] = endpoint

            programs.append(program)

        return programs

    def load(self, page: int) -> List[Dict[str, Any]]:
        programs = []

        targets = ["중장년", "노년"]

        uuids = self._load_uuids(page=page, targets=targets)

        central_program_batch = self._load_programs(
            uuids=uuids["central"], operating="central"
        )
        local_program_batch = self._load_programs(
            uuids=uuids["local"], operating="local"
        )

        programs.extend(central_program_batch)
        programs.extend(local_program_batch)

        return programs

    def __str__(self):
        return "BokjiroLoader"


class Subsidy24Loader(Loader):
    def _load_uuids(self, page: int):
        params = {
            "sort": "DATE",
            "query": "노인 어르신 고령",
            "startCount": (page - 1) * 12,
        }

        response = requests.get(SUBSIDY24_UUID_ENDPOINT, params=params, timeout=10)
        time.sleep(1)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, "html.parser")
        card_links = soup.select("a.card-title")

        uuids = set()

        for link in card_links:
            href = link.get("href")

            if not href:
                continue

            last_part = href.split("/")[-1]
            service_id = last_part.split("?")[0]

            if service_id:
                uuids.add(service_id)

        return list(uuids)

    def _get_clean_text(self, soup_obj: BeautifulSoup, selector: str):
        element = soup_obj.select_one(selector)
        if element:
            return element.get_text(strip=True)
        return None

    def _get_multiline_text(self, soup_obj: BeautifulSoup, selector: str):
        element = soup_obj.select_one(selector)
        if element:
            lines = [
                line.strip() for line in element.get_text(separator="\n").splitlines()
            ]
            return "\n".join(line for line in lines if line)
        return None

    def _trim(self, response: requests.Response):
        soup = BeautifulSoup(response.text, "html.parser")

        program = {}

        program["title"] = self._get_clean_text(soup, "h2.service-title")
        program["description"] = self._get_clean_text(soup, "p.service-desc")
        program["category"] = self._get_clean_text(soup, "div.tag-wrap > span.chip")

        panel1 = soup.select_one("div#panel1")
        if panel1:
            program["application_period"] = self._get_clean_text(
                panel1, "li.term > span"
            )
            program["phone_inquiry"] = self._get_clean_text(panel1, "li.call > span")
            program["receiving_institution"] = self._get_clean_text(
                panel1, "li.reception > span"
            )
            program["support_type"] = self._get_clean_text(panel1, "li.support > span")

        program["support_target"] = self._get_multiline_text(
            soup, "div#panel2 pre.detail-desc"
        )

        program["support_details"] = self._get_multiline_text(
            soup, "div#panel3 pre.detail-desc"
        )

        program["application_method_details"] = self._get_multiline_text(
            soup, "div#panel4 li.method > span"
        )

        doc_section = soup.select_one("div#panel4 strong.document")
        if doc_section:
            doc_wrapper = doc_section.find_parent("div", class_="detail-wrap")
            if doc_wrapper:
                doc_pre_tags = doc_wrapper.find_all("pre", class_="detail-desc")
                if len(doc_pre_tags) > 0:
                    program["required_docs_applicant"] = self._get_clean_text(
                        doc_pre_tags[0], "pre"
                    )
                if len(doc_pre_tags) > 1:
                    program["required_docs_internal"] = self._get_clean_text(
                        doc_pre_tags[1], "pre"
                    )

        program["managing_institution"] = self._get_clean_text(
            soup, "div.info-ins > span"
        )
        program["last_updated"] = self._get_clean_text(soup, "div.info-date > span")

        return program

    def _load_programs(self, uuids: List[str]) -> List[Dict[str, Any]]:
        programs = []

        for uuid in uuids:
            endpoint = SUBSIDY24_PROGRAM_ENDPOINT.format(uuid)

            response = requests.get(endpoint, timeout=10)
            time.sleep(1)
            response.raise_for_status()

            program = self._trim(response)
            program["uuid"] = uuid
            program["reference_url"] = endpoint

            programs.append(program)

        return programs

    def load(self, page: int) -> List[Dict[str, Any]]:
        uuids = self._load_uuids(page)
        programs = self._load_programs(uuids)

        return programs

    def __str__(self):
        return "Subsidy24Loader"
