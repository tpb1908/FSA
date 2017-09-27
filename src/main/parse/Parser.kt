package main.parse

import main.FSA
import main.State

class Parser : FSAParser {

    private val states = ArrayList<Pair<State, String>>()
    private val transitions = LinkedHashMap<State, LinkedHashMap<String, ArrayList<State>>>()

    var debug = false

    /*TODO
    Change formatting to something more reasonable
    "state_name"s: [a:"state_name_2"], "state_name_2"e: []
     */

    @Throws(NFAParseException::class)
    override fun parse(input: String): FSA {
        states.clear()
        transitions.clear()
        val blocks = input.split(',')
        blocks.forEach { states.add(Pair(extractState(it), it)) }
        println("Found states ${states.map { it.first }}")
        verifyStates()
        states.forEach { buildTransitionTable(it) }

        return FSA(states.map { it.first }, transitions)
    }

    @Throws(NFAParseException::class)
    private fun verifyStates() {
        if (!states.any { it.first.start }) throw NFAParseException("At least one state must be a start state")
        if (!states.any {it.first.end}) throw NFAParseException("At least one state must be an end state")
    }

    @Throws(NFAParseException::class)
    private fun extractState(state: String): State {
        //TODO Rewrite with new format
        try {
            var sIndex = state.indexOf('{') + 1
            var eIndex = state.indexOf('}')
            val name = state.substring(sIndex, eIndex)
            sIndex = state.indexOf('{', eIndex)
            eIndex = state.indexOf('}', sIndex)
            val se = state.substring(sIndex, eIndex)
            return State(name, se.contains('s', true), se.contains('e', true))
        } catch (e: IndexOutOfBoundsException) {
            throw NFAParseException("Exception parsing state $state")
        }
    }

    @Throws(NFAParseException::class)
    private fun buildTransitionTable(state: Pair<State, String>) {
        //Transition written as [{key}{name} {key}{name} {key_2}{name} {key_2}{name_2}]
        val trs = StringBuilder() // Current transition key
        val name = StringBuilder() // State name
        var isName = false //Currently reading name

        val trsString = with(state.second) { substring(indexOf('[') + 1, indexOf(']')) }

        trsString.forEach { if (it != '{') { //Ignore opening brace
                if (it == '}') { //At the end of either a transition key or a name
                    if (isName) {
                        val trsState = findState(name.toString()) // Find the State for the key
                        // Set up key to State list map if it doesn't exist
                        if (!transitions.containsKey(state.first)) transitions[state.first] = LinkedHashMap()
                        val transitionKey = trs.toString()
                        if (transitions[state.first]?.containsKey(transitionKey) != true) {
                            transitions[state.first]?.set(transitionKey, ArrayList())
                        }
                        transitions[state.first]?.get(transitionKey)?.add(trsState)
                        //Clear the key and name
                        trs.setLength(0)
                        name.setLength(0)
                        isName = false
                    } else {
                        isName = true
                    }
                } else if (isName) name.append(it) else trs.append(it)
            }
        }
        if (debug) {
            transitions.forEach { t, u ->
                println("Transition(s) from ${t.name}: $u ")
            }
        }
    }

    private fun findState(name: String): State {
        return states.find { it.first.name == name }?.first ?: throw NFAParseException("State $name named in transition not found")
    }

    class NFAParseException(message: String) : Exception(message)

}