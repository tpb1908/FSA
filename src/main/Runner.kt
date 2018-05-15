package main

import java.awt.Color
import java.util.*

class Runner(FSA: FSA) {

    private val inputSymbols = FSA.inputSymbols
    private val startStates = FSA.startStates
    private val endStates = FSA.endStates
    private val transitions = FSA.transitions

    var states = ArrayList<RunningState>()
        private set

    private val logger = Logger()

    var debug = false
    var printTransitions = true //TODO Log levels
    var saveStateHistory = false

    fun run(input: Array<String>) {
        states.clear()
        //Wrap with RunningStates for logging
        //HistoryRunningState keeps track of the path to the state
        states.addAll(startStates.map { createRunningState(it) })

        if (debug) logger.printTransitions(transitions)

        input.forEach { str ->
            //Single pass through input string (string of strings)
            if (str !in inputSymbols) {
                throw IllegalArgumentException("Input $str not in input set $inputSymbols")
            }

            // TODO: Replace each state in place
            val newStates = ArrayList<RunningState>() // New set of states generated in pass
            states.forEach { rs ->
                val trs = transitions[rs.state]?.get(str) //Collection of states we can move to
                if (trs != null) {
                    newStates.addAll(trs.map { updateRunningState(rs, it) })
                    logger.printTransition(rs.state, str, trs)
                }
            }

            states = newStates
            logger.printNewStates(newStates.map { it.state })

            val finalStates = states.map { it.state }.union(endStates)
            if (finalStates.isNotEmpty()) {
                logger.printFinalStates(finalStates)
            }
        }
        println("Reached input end: $states")
    }

    private fun createRunningState(state: State): RunningState {
        return if (saveStateHistory) RunningState.HistoryRunningState(state) else RunningState.SingleRunningState(state)
    }

    //TODO Find a better way of doing this
    private fun updateRunningState(runningState: RunningState, state: State): RunningState {
        return if (runningState is RunningState.HistoryRunningState) {
            RunningState.HistoryRunningState(state, runningState.previous)
        } else {
            RunningState.SingleRunningState(state)
        }
    }

    sealed class RunningState {

        abstract var state: State

        class SingleRunningState(startState: State) : RunningState() {
            override var state: State = startState

            override fun toString(): String {
                return "SRS(state=$state)"
            }
        }

        class HistoryRunningState(startState: State, val previous: MutableList<State> = arrayListOf()) : RunningState() {
            init {
                previous.add(startState)
            }

            override var state: State = startState
                set(value) {
                    previous.add(value)
                    field = value
                }

            override fun toString(): String {
                return "HRS(state$state, history=$previous)"
            }
        }

    }

    private class Logger {

        private val ESCAPE = '\u001B'
        private val RESET = "$ESCAPE[0m"

        var printTransitions = true //TODO Log levels

        fun printTransitions(transitions: Map<State, Map<String, Collection<State>>>) {
            print("Transitions:\n", 92)
            transitions.forEach { state, map ->
                // For each state
                print("${state.name} -> \n " + // Transition from it to
                        map.map { entry ->
                            // For each input symbol to list of states
                            entry.key + ": " + entry.value.map { it.name }.toString() + "\n"
                        }.toString()
                                .removePrefix("[") // Remove braces on state list
                                .removeSuffix("]")
                                .replace(", ", " "), // Remove comma separation
                 92)
            }
        }

        fun printTransition(current: State, symbol: String, transitions: Collection<State>) {
            if (!printTransitions) return
            println("Transitioning from ${current.name} with $symbol to: ${transitions.map { it.name }}")
        }

        fun printNewStates(newStates: Collection<State>) {
            print("Transition complete. New states ${newStates.map { it.name }}\n", 32)
        }

        fun printFinalStates(finalStates: Collection<State>) {
            print("Reached final states: ${finalStates.map { it.name }}\n\n", 32)
        }

        private fun print(out: String, foreground: Int) {
            println("$ESCAPE[${foreground}m$out$RESET")
        }

    }

}