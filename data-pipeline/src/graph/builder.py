from langgraph.graph import StateGraph, END
from .state import GraphState
from .node import trim_program, validate_program

workflow = StateGraph(GraphState)

workflow.add_node("trim", trim_program)
workflow.add_node("validate", validate_program)

workflow.set_entry_point("trim")

workflow.add_edge("trim", "validate")
workflow.add_edge("validate", END)

graph = workflow.compile()
