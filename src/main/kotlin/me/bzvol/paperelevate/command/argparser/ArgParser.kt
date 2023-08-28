package me.bzvol.paperelevate.command.argparser

interface ArgParser {
    fun parse(args: Array<String>): Map<String, *>
    val usage: String
}