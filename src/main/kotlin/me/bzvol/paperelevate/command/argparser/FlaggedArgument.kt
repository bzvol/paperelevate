package me.bzvol.paperelevate.command.argparser

import me.bzvol.paperelevate.command.UsageColors
import kotlin.reflect.KClass

class FlaggedArgument<T : Any>(
    type: KClass<T>,
    placeholder: String,
    val shortFlag: String,
    val longFlag: String? = null,
    default: T? = null,
    required: Boolean = false,
    allowNull: Boolean = false,
    allowedValues: (() -> List<T>)? = null,
    parser: ((String?) -> T?)? = null,
) : Argument<T>(type, placeholder, default, required, allowNull, allowedValues, parser) {
    val isBoolean = type == Boolean::class

    override val usage: String
        get() = if (required) "${UsageColors.argumentColor}$shortFlag/$longFlag " +
                "${UsageColors.otherColor}<${UsageColors.argumentColor}$placeholder${UsageColors.otherColor}>" +
                UsageColors.resetColor
        else "${UsageColors.otherColor}[${UsageColors.optionalArgumentColor}$shortFlag/$longFlag " +
                "${UsageColors.otherColor}<${UsageColors.optionalArgumentColor}$placeholder${UsageColors.otherColor}>]" +
                UsageColors.resetColor

    companion object {
        inline operator fun <reified T : Any> invoke(
            placeholder: String, shortFlag: String, longFlag: String? = null,
            default: T? = null, required: Boolean = false, allowNull: Boolean = false,
            noinline allowedValues: (() -> List<T>)? = null,
            noinline parser: ((String?) -> T)? = null,
        ): FlaggedArgument<T> = FlaggedArgument(
            T::class, placeholder, shortFlag, longFlag,
            default, required, allowNull, allowedValues, parser
        )
    }
}