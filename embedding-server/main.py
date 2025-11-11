from fastapi import FastAPI
from app.api.apis import router

app = FastAPI(
    title="Embedding API",
    description="An API to generate text embeddings, protected by an API Key.",
)

app.include_router(router)


@app.get("/")
def read_root():
    return {"message": "Welcome to the Embedding API. See /docs for details."}
