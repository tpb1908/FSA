package main

data class FSA(private val states: Collection<State>, val transitions: Map<State, Map<String, Collection<State>>>) {

    val startStates: Collection<State> by lazy { states.filter { it.start } }

    val endStates: Collection<State> by lazy { states.filter { it.end } }

}