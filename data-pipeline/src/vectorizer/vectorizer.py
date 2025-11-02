from typing import List

import numpy.typing as NDArray
from sentence_transformers import SentenceTransformer
import torch


class Vectorizer:
    def __init__(self):
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        print(f"(Vectorizer) Using device: {self.device}")

        self.model = SentenceTransformer("BAAI/bge-m3", device=self.device)

    def run(self, texts: List[str]) -> NDArray:
        vectors = self.model.encode(
            texts,
            normalize_embeddings=True,
            show_progress_bar=False,
        )

        return vectors
