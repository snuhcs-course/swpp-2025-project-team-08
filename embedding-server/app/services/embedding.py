from typing import List
from sentence_transformers import SentenceTransformer

import logging
import torch

device = "cuda" if torch.cuda.is_available() else "cpu"
logger = logging.getLogger("uvicorn")

try:
    model = SentenceTransformer("BAAI/bge-m3", device=device)
    logger.info("Embedding model loaded successfully.")
except Exception as e:
    logger.error(f"Error loading embedding model: {e}")
    model = None


def get_embedding(text: str) -> List[float]:
    if model is None:
        raise RuntimeError("Embedding model is not loaded.")

    embedding_array = model.encode(text)
    return embedding_array.tolist()


def is_model_loaded() -> bool:
    return model is not None
