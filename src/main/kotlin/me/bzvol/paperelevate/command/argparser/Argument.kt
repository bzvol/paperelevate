package me.bzvol.paperelevate.command.argparser

import me.bzvol.paperelevate.command.UsageColors
import kotlin.reflect.KClass

abstract class Argument<T : Any>(
    private val type: KClass<T>,
    val placeholder: String,
    protected val default: T?,
    val required: Boolean,
    protected val allowNull: Boolean,
    allowedValuesProvider: (() -> List<T>)?,
    protected val parser: ((String?) -> T?)?,
) {
    val allowedValues: List<T>? by lazy(allowedValuesProvider ?: {
        @Suppress("UNCHECKED_CAST")
        if (type == Boolean::class) listOf(true as T, false as T) else null
    })

    open val usage: String
        get() = if (required) "${UsageColors.otherColor}<${UsageColors.argumentColor}$placeholder" +
                "${UsageColors.otherColor}>${UsageColors.resetColor}"
        else "${UsageColors.otherColor}[<${UsageColors.optionalArgumentColor}$placeholder" +
                "${UsageColors.otherColor}>]${UsageColors.resetColor}"

    @Suppress("UNCHECKED_CAST")
    fun parse(arg: String?): T? {
        if (arg == null) {
            if (required) throw IllegalArgumentException("Argument $placeholder is required")
            if (default != null) return default
            if (allowNull) return null
        }

        if (parser != null) return parser.invoke(arg).also(::checkIfAllowed)

        if (type.java.isAssignableFrom(arg!!::class.java)) return (arg as T).also(::checkIfAllowed)

        return when (type) {
            Boolean::class -> arg.toBoolean() as T // NOTE: switches are automatically parsed as true
            Int::class -> arg.toInt() as T
            Long::class -> arg.toLong() as T
            Float::class -> arg.toFloat() as T
            Double::class -> arg.toDouble() as T
            String::class -> arg as T
            else -> throw IllegalArgumentException(
                "Argument $placeholder" +
                        "has unsupported type ${type.simpleName}"
            )
        }.also(::checkIfAllowed)
    }

    private fun checkIfAllowed(value: T?) {
        val allowedValues = allowedValues
        if (value != null && allowedValues != null && value !in allowedValues)
            throw IllegalArgumentException("Argument $placeholder has value $value which is not allowed")
    }
}