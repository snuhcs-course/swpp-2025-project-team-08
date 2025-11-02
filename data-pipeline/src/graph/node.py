import json
from dotenv import load_dotenv
from langchain_core.output_parsers import JsonOutputParser
from langchain_google_genai import ChatGoogleGenerativeAI

from graph.prompts import build_trim_program_prompt

from .state import GraphState

load_dotenv()

llm = ChatGoogleGenerativeAI(model="gemini-2.5-flash", temperature=0)
parser = JsonOutputParser()


def trim_program(state: GraphState):
    raw_program = state["raw_program"]

    prompt = build_trim_program_prompt(parser.get_format_instructions())
    chain = prompt | llm | parser

    trimmed_program = chain.invoke({"raw_program": json.dumps(raw_program)})

    return {"trimmed_program": trimmed_program}


def validate_program(state: GraphState):
    return {"is_valid": True}
