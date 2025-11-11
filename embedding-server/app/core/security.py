import secrets
from fastapi import Depends, HTTPException, status
from fastapi.security import APIKeyHeader

from app.core.config import API_KEY

api_key_header = APIKeyHeader(name="X-API-Key")


async def get_api_key(provided_key: str = Depends(api_key_header)):
    if not secrets.compare_digest(provided_key, API_KEY):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid API Key"
        )
    return provided_key
