package me.bzvol.paperelevate.command.argparser

interface ArgParser {
    val usage: String
    fun parse(args: Array<String>): Map<String, *>
    fun tabCompletions(args: Array<String>, argIndex: Int): List<String>
}