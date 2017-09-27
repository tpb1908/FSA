package main.parse

import main.FSA

interface FSAParser {

    fun parse(input: String): FSA

}