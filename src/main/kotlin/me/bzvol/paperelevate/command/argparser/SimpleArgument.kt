package me.bzvol.paperelevate.command.argparser

import kotlin.reflect.KClass

class SimpleArgument<T : Any>(
    type: KClass<T>,
    placeholder: String,
    default: T? = null,
    required: Boolean = false,
    allowNull: Boolean = true,
    allowedValues: (() -> List<T>)? = null,
    parser: ((String?) -> T?)? = null,
) : Argument<T>(type, placeholder, default, required, allowNull, allowedValues, parser) {
    companion object {
        inline operator fun <reified T : Any> invoke(
            placeholder: String, default: T? = null,
            required: Boolean = false, allowNull: Boolean = true,
            noinline allowedValues: (() -> List<T>)? = null,
            noinline parser: ((String?) -> T?)? = null,
        ): SimpleArgument<T> = SimpleArgument(
            T::class, placeholder, default, required, allowNull, allowedValues, parser
        )
    }
}