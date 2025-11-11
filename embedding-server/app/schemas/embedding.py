from typing import List
from pydantic import BaseModel


class TextPayload(BaseModel):
    text: str


class EmbeddingResponse(BaseModel):
    embedding: List[float]
