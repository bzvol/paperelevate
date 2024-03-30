package me.bzvol.paperelevate.command.argparser

class SimpleArgParser : ArgParser {
    val arguments = mutableListOf<SimpleArgument<out Any>>()

    override val usage: String
        get() = arguments.joinToString(" ") { it.usage }

    inline fun <reified T : Any> add(
        placeholder: String,
        noinline allowedValues: (() -> List<T>)? = null,
        noinline parser: ((String?) -> T)? = null,
    ): SimpleArgParser = apply {
        arguments.add(
            SimpleArgument<T>(
                placeholder, required = true, allowNull = false, allowedValues = allowedValues, parser = parser
            )
        )
    }

    inline fun <reified T : Any> addOptional(
        placeholder: String,
        default: T? = null,
        noinline allowedValues: (() -> List<T>)? = null,
        noinline parser: ((String?) -> T)? = null,
    ): SimpleArgParser = apply {
        arguments.add(SimpleArgument<T>(placeholder, default = default, allowedValues = allowedValues, parser = parser))
    }

    override fun parse(args: Array<String>): Map<String, *> {
        val requiredArgs = arguments.filter { it.required }
        if (args.size < requiredArgs.size)
            throw IllegalArgumentException("Not enough arguments provided. " +
                    "Required: ${requiredArgs.size}, provided: ${args.size}")

        return arguments.sortedByDescending { it.required }.mapIndexed { idx, arg ->
            arg.placeholder to arg.parse(args.getOrNull(idx))
        }.toMap()
    }

    override fun tabCompletions(args: Array<String>, argIndex: Int): List<String> =
        arguments.sortedByDescending { it.required }.getOrNull(argIndex)
            ?.allowedValues?.map { it.toString() }?.filter { " " !in it }
            ?: emptyList()
}