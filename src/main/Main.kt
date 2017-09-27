package main

import java.io.File


class Main {

    /*
    TODO
    - Add tests

     */

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            val parser = Parser()
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


            parser.debug = false
            val fda = parser.parse(fifthchar)
            println("FSA is $fda")
            println("\n\n\n")
            val runner  = Runner(fda)
            runner.saveStateHistory = true
            runner.run(arrayOf("0", "1", "1", "1", "1"))
            runner.states.forEach {
                if (it is Runner.RunningState.HistoryRunningState) {
                    println("History for state ${it.state} is ${it.previous}")
                }
            }
        }

    }

}