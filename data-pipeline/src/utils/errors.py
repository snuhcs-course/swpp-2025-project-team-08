from typing import Optional, Dict


class PipelineError(Exception):
    """Base error for the data pipeline."""

    def __init__(
        self,
        message: str = "",
        *,
        cause: Optional[BaseException] = None,
        details: Optional[Dict] = None,
    ):
        super().__init__(message)
        self.cause = cause
        self.details = details or {}

    def __str__(self) -> str:
        base = super().__str__()
        if self.cause:
            return f"{base} (cause={self.cause!r})"
        return base


class MismatchError(PipelineError):
    """Embedding UUID does not match program UUID."""
