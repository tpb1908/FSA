package main

import java.util.*

class Runner(FSA: FSA) {

    private val startStates = FSA.startStates
    private val endStates = FSA.endStates
    private val transitions = FSA.transitions

    var states = ArrayList<RunningState>()
        private set


    var debug = false
    var printTransitions = true //TODO Log levels
    var saveStateHistory = false

    fun run(input: Array<String>) {
        states.clear()
        states.addAll(startStates.map { createRunningState(it) }) //Wrap with RunningStates for logging

        if(debug) println("Transitions $transitions")

        input.forEach { str -> //Single pass through input string (string of strings)
            val newStates = ArrayList<RunningState>() //New set of states generated in pass
            //TODO Overwrite states as the pass is going on
            states.forEach { rs ->
                val trs = transitions[rs.state]?.get(str) //Collection of states we can move to
                newStates.addAll(trs?.map {
                    if(printTransitions) println("Transitioning with $str from ${rs.state} to $it")
                    updateRunningState(rs, it)
                } ?: listOf())
                if (trs?.isEmpty() == true) println("No transitions for ${rs.state} with $str")
            }

            println("\nTransition pass complete. New states are $newStates\n")
            states = newStates
            val endStates = checkEndStates()
            if (endStates.isNotEmpty()) {
                println("Reached end state(s): $endStates)")
            }
        }
        println("Reached input end: $states")
    }

    private fun checkEndStates(): Collection<State> {
        return states.map { it.state }.filter { endStates.contains(it) }
    }

    private fun createRunningState(state: State): RunningState {
        return if (saveStateHistory) RunningState.HistoryRunningState(state) else RunningState.SingleRunningState(state)
    }

    //TODO Find a better way of doing this
    private fun updateRunningState(runningState: RunningState, state: State): RunningState {
        if (runningState is RunningState.HistoryRunningState) {
            return RunningState.HistoryRunningState(state, runningState.previous)

        } else {
            return RunningState.SingleRunningState(state)
        }
    }

    sealed class RunningState {

        abstract var state: State

        class SingleRunningState(startState: State): RunningState() {
            override var state: State = startState

            override fun toString(): String {
                return "SRS(state=$state)"
            }
        }

        class HistoryRunningState(startState: State, val previous: MutableList<State> = arrayListOf()): RunningState() {
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

}