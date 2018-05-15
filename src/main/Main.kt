package main

import main.parse.JsonParser
import java.io.File


class Main {

    /*
    TODO
    - Add tests

     */

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            val parser = JsonParser()
            if (args.isNotEmpty()) {
                val fsaString = File(args.first()).readText()
                parser.parse(fsaString)
            } else {

            }

            val simple = "{state_1}{s} [{a}{state_2} {b}{state_3}], {state_2}{e} [], {state_3}{e} [{a}{state_1}]"

            //This format is horrible. Will fix it eventually

            val fifthchar = "{state_0}{s} [{0}{state_0}{1}{state_0}{1}{state_1}], " +
                    "{state_1} [{0}{state_2}{1}{state_2}], " +
                    "{state_2} [{0}{state_3}{1}{state_3}]," +
                    "{state_3} [{0}{state_4}{1}{state_4}]," +
                    "{state_4} [{0}{state_5}{1}{state_5}], " +
                    "{state_5}{e} []"

            val fifthcharJson =  """{
                    "states": [
                        {
                            "name": "state_0",
                            "start": true,
                            "transitions": [
                                {
                                    "key": "0",
                                    "state": "state_0"
                                },
                                {
                                    "key": "1",
                                    "state": "state_0"
                                },
                                {
                                    "key": "1",
                                    "state": "state_1"
                                }
                            ]
                        },
                        {
                            "name": "state_1",
                            "transitions": [
                                {
                                    "key": "1",
                                    "state": "state_2"
                                },
                                {
                                    "key": "0",
                                    "state": "state_2"
                                }
                            ]
                        },
                        {
                            "name": "state_2",
                            "transitions": [
                                {
                                    "key": "1",
                                    "state": "state_3"
                                },
                                {
                                    "key": "0",
                                    "state": "state_3"
                                }
                            ]
                        },
                        {
                            "name": "state_3",
                            "transitions": [
                                {
                                    "key": "1",
                                    "state": "state_4"
                                },
                                {
                                    "key": "0",
                                    "state": "state_4"
                                }
                            ]
                        },
                        {
                            "name": "state_4",
                            "transitions": [
                                {
                                    "key": "1",
                                    "state": "state_5"
                                },
                                {
                                    "key": "0",
                                    "state": "state_5"
                                }
                            ]
                        },
                        {
                            "name": "state_5",
                            "end": true,
                            "transitions": []
                        }
                    ]
                }"""

            //parser.debug = false
            val fsa = parser.parse(fifthcharJson)
            println("FSA is $fsa")
            println("\n\n\n")
            val runner  = Runner(fsa)
            runner.debug = true
            runner.saveStateHistory = false
            runner.run(arrayOf("0", "1", "1", "1", "1", "1"))
            runner.states.forEach {
                if (it is Runner.RunningState.HistoryRunningState) {
                    println("History for state ${it.state} is ${it.previous}")
                }
            }
        }

    }

}