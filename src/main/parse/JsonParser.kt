package main.parse

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject

import main.FSA
import main.State

class JsonParser : FSAParser {

    private val states = ArrayList<State>()
    private val transitions = LinkedHashMap<State, LinkedHashMap<String, ArrayList<State>>>()


    override fun parse(input: String): FSA {

        //TODO Support multiple keys for the same state

        val stateJsonObjects = Json.parse(input).asObject().get("states").asArray()
        //Map each state we find to a list of its transitions
        val transitionJson = HashMap<State, List<JsonObject>>()

        stateJsonObjects.values().map { it.asObject() }.forEach {
            //TODO Better handling of states without names
            val name = it.getString("name", "unknown_state")
            val start = it.getBoolean("start",false)
            val end = it.getBoolean("end", false)
            val state = State(name, start, end)
            states.add(state)
            //Get the JSONArray and map each value to a JSON object
            transitionJson.put(state, it.get("transitions").asArray().map { it.asObject() })
        }
        //Building the transition table for each state
        transitionJson.forEach { state, trsList ->
            trsList.forEach {
                val key = it.getString("key", "unknown_key")
                //State to move to
                val endStateName = it.getString("state", "unknown_name")
                if (endStateName == "unknown_name") { //Multiple states for this key
                    val stateArray = it.get("states").asArray()
                    stateArray.forEach {
                        //Find the State matching the name given in the transition
                        val endState = states.find { it.name == endStateName }
                        if (endState != null) {
                            addTransition(state, key, endState)
                        } else {
                            System.err.println("Cant't find state $endStateName for transition")
                        }
                    }
                } else {
                    val endState = states.find { it.name == endStateName }
                    if (endState != null) {
                        addTransition(state, key, endState)
                    } else {
                        System.err.println("Cant't find state $endStateName for transition")
                    }

                }
            }
        }

        return FSA(states, transitions)
    }

    private fun addTransition(state: State, key: String, value: State) {
        if (transitions[state] == null) transitions[state] = LinkedHashMap()
        if (transitions[state]?.get(key) == null) transitions[state]?.set(key, ArrayList())
        transitions[state]?.get(key)?.add(value)

    }


}