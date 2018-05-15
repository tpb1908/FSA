package main
/**
Finite state automaton can be modelled as a quintuple:
- The input alphabet: A finite non-empty set
- The state set
- A set of initial states
- A transition function mapping a state and input symbol to a set of states
- A set of final states

Each [State] has a name and [State.start]/[State.end] flags.
Transitions are a map from a state to a map of Strings (from the input alphabet) to collections of [State]s
 */
data class FSA(private val states: Collection<State>, val transitions: Map<State, Map<String, Collection<State>>>) {

    val inputSymbols: Collection<String> by lazy { transitions.values.flatMap { it.keys } }

    val startStates: Collection<State> by lazy { states.filter { it.start } }

    val endStates: Collection<State> by lazy { states.filter { it.end } }

}