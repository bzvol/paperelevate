package me.bzvol.paperelevate.command.argparser

class FlaggedArgParser : ArgParser {
    val arguments = mutableListOf<FlaggedArgument<out Any>>()
    val flags by lazy { arguments.flatMap { listOf(it.shortFlag, it.longFlag) }.filterNotNull() }

    override val usage: String
        get() = arguments.joinToString(" ") { it.usage }

    inline fun <reified T : Any> add(
        placeholder: String,
        shortFlag: String,
        longFlag: String? = null,
        noinline allowedValues: (() -> List<T>)? = null,
        noinline parser: ((String?) -> T)? = null,
    ): FlaggedArgParser = apply {
        if (T::class == Boolean::class) addSwitch(placeholder, shortFlag, longFlag)
        else arguments.add(
            FlaggedArgument<T>(
                placeholder, shortFlag, longFlag, required = true,
                allowNull = false, allowedValues = allowedValues, parser = parser
            )
        )
    }

    inline fun <reified T : Any> addOptional(
        placeholder: String,
        shortFlag: String,
        longFlag: String? = null,
        default: T? = null,
        noinline allowedValues: (() -> List<T>)? = null,
        noinline parser: ((String?) -> T)? = null,
    ): FlaggedArgParser = apply {
        if (T::class == Boolean::class) addSwitch(placeholder, shortFlag, longFlag)
        else arguments.add(
            FlaggedArgument<T>(
                placeholder, shortFlag, longFlag, default = default, allowedValues = allowedValues, parser = parser
            )
        )
    }

    fun addSwitch(
        placeholder: String,
        shortFlag: String,
        longFlag: String? = null,
    ): FlaggedArgParser = apply {
        arguments.add(FlaggedArgument<Boolean>(placeholder, shortFlag, longFlag, default = false))
    }

    override fun parse(args: Array<String>): Map<String, *> = arguments.associate { arg ->
        val flagIdx = args.indexOfFirst { it == arg.shortFlag || it == arg.longFlag }
        if (flagIdx == -1) arg.placeholder to arg.parse(null)
        else if (arg.isBoolean) arg.placeholder to true
        else arg.placeholder to arg.parse(args.getOrNull(flagIdx + 1).let { if (it in flags) null else it })
    }

    override fun tabCompletions(args: Array<String>, argIndex: Int): List<String> =
        if (argIndex % 2 == 0) arguments.filter { it.shortFlag !in args && it.longFlag !in args }
            .flatMap { listOf(it.shortFlag, it.longFlag) }.filterNotNull()
        else arguments.find { it.shortFlag == args[argIndex - 1] || it.longFlag == args[argIndex - 1] }
            ?.allowedValues?.map { it.toString() }?.filter { " " !in it } ?: emptyList()
}