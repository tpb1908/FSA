package main.parse

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject

import main.FSA
import main.State

class JsonParser : FSAParser {

    private val states = ArrayList<State>()
    private val transitions = LinkedHashMap<State, LinkedHashMap<String, ArrayList<State>>>()


    override fun parse(input: String): FSA {


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
            transitionJson[state] = it.get("transitions").asArray().map { it.asObject() }
        }


        //Building the transition table for each state
        transitionJson.forEach { state, trsList ->
            trsList.forEach { transition ->

                // We either have a single key, or a list of keys to move to a state or list of states
                var keys = listOf(transition.getString("key", "unknown_key"))
                if (keys[0] == "unknown_key") {
                    keys = transition.get("keys").asArray().map { it.asString() }
                }

                //State or states to move to
                var endStateNames = listOf(transition.getString("state", "unknown_name"))
                if (endStateNames[0] == "unknown_name") {
                    endStateNames = transition.get("states").asArray().map { it.asString() }
                }

                // For each of the end states and each of the keys, add the transition
                endStateNames.forEach { endStateName ->
                    //Find the State matching the name given in the transition
                    val endState = states.find { it.name == endStateName }
                    if (endState != null) {
                        keys.forEach { key -> addTransition(state, key, endState) }
                    } else {
                        System.err.println("Cant't find state $endStateName for transition with key $keys")
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