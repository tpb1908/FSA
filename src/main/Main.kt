package main


class Main {

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            val parser = Parser()
            parser.parse("{state_1}{s} [{a}{state_2} {b}{state_3}], {state_2}{e} [], {state_3}{e} [{a}{state_1}]")
        }

    }

    /*
    Input
    {name}{se} [{trs}{name} {trs}{name} {trs}{name}],
     */
    /*
    Take an input of the FSA
    Parse it into a list of states and a table of transitions
    Run
     */

}