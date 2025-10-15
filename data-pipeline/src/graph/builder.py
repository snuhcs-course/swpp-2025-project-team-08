from langgraph.graph import StateGraph, END
from .state import GraphState
from .node import trim_program

workflow = StateGraph(GraphState)

workflow.add_node("program_trimmer", trim_program)
workflow.set_entry_point("program_trimmer")

workflow.add_edge("program_trimmer", END)

graph = workflow.compile()
