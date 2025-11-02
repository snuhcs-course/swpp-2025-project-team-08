from ast import List
from typing import TypedDict, Any, Dict, Optional


class GraphState(TypedDict):
    raw_program: Dict[str, Any]
    trimmed_program: Optional[Dict[str, Any]]
    is_valid: bool
