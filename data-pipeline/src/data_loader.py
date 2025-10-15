from abc import ABC, abstractmethod
from typing import Any, List, Set, Dict, Optional

import requests

from config import MAX_PAGE_NUM, YOUTH_CENTER_ENDPOINT


class APILoader(ABC):
    def __init__(self, api_key: str, previously_loaded_ids: Optional[Set[str]] = None):
        self.api_key = api_key
        self.previously_loaded_ids = previously_loaded_ids

    @abstractmethod
    def load(self) -> list[int]:
        raise NotImplementedError


class YouthCenterLoader(APILoader):
    def _find_duplicate(self, raw_programs: List[Dict[str, Any]]) -> int:
        if not self.previously_loaded_ids:
            return -1

        for i, raw_program in enumerate(raw_programs):
            if raw_program["plcyNo"] in self.previously_loaded_ids:
                return i

        return -1

    def _trim(self, raw_programs: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        # TODO
        return raw_programs

    def load(self) -> List[Dict[str, Any]]:
        results = []
        payload = {
            "apiKeyNm": self.api_key,
            "rtnType": "json",
            "pageNum": 1,
            "pageSize": 100,
            "pageType": 2,
        }

        while payload["pageNum"] <= MAX_PAGE_NUM:
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
