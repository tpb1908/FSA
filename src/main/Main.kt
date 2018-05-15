package main

import main.parse.JsonParser
import java.io.File


class Main {


     // TODO Mealy machine

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            val parser = JsonParser()
            if (args.isNotEmpty()) {
                val fsaString = File(args.first()).readText()
                parser.parse(fsaString)
            }

            val twentyfivepencemachine = """{
                "states": [
                    {
                        "name": "start",
                        "start": true,
                        "transitions": [
                            {
                                "key": "5p",
                                "state": "5"
                            },
                            {
                                "key": "10p",
                                "state": "10"
                            },
                            {
                                "key": "20p",
                                "state": "20"

                            }
                        ]
                    },
                    {
                        "name": "5",
                        "transitions": [
                            {
                                "key": "5p",
                                "state": "10"
                            },
                            {
                                "key": "10p",
                                "state": "15"
                            },
                            {
                                "key": "20p",
                                "state": "25"

                            }
                        ]
                    },
                    {
                        "name": "10",
                        "transitions": [
                            {
                                "key": "5p",
                                "state": "15"
                            },
                            {
                                "key": "10p",
                                "state": "20"
                            }
                        ]
                    },
                    {
                        "name": "15",
                        "transitions": [
                            {
                                "key": "5p",
                                "state": "20"
                            },
                            {
                                "key": "10p",
                                "state": "25"
                            }
                        ]
                    },
                    {
                        "name": "20",
                        "transitions": [
                            {
                                "key": "5p",
                                "state": "25"
                            }
                        ]
                    },
                    {
                        "name": "25",
                        "end": true,
                        "transitions": []
                    }
                ]
                }"""

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



            val fsa = parser.parse(twentyfivepencemachine)
            println("FSA $fsa")
            println("\n\n\n")
            val runner  = Runner(fsa)
            runner.debug = true
            runner.saveStateHistory = false
            runner.run(arrayOf("5p", "5p", "5p", "10p"))
            runner.states.forEach {
                if (it is Runner.RunningState.HistoryRunningState) {
                    println("History for state ${it.state} is ${it.previous}")
                }
            }
        }

    }

}