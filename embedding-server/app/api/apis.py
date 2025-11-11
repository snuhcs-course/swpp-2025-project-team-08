from fastapi import APIRouter, Depends
from app.core.security import get_api_key
from app.schemas.embedding import EmbeddingResponse, TextPayload
from app.services.embedding import get_embedding, is_model_loaded

router = APIRouter(prefix="/v1")


@router.post(
    "/embed",
    response_model=EmbeddingResponse,
    dependencies=[Depends(get_api_key)],
)
async def create_embedding(payload: TextPayload):
    embedding = get_embedding(payload.text)
    return {"embedding": embedding}


@router.get("/health")
def health_check():
    return {"status": "ok", "model_loaded": is_model_loaded()}
