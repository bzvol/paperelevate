package me.bzvol.paperelevate

import me.bzvol.paperelevate.command.Command2
import org.bukkit.block.Block
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.BlockIterator
import kotlin.reflect.KClass

object Util {
    fun BlockIterator.closest(predicate: (block: Block) -> Boolean): Block? {
        var block: Block? = null
        while (hasNext()) {
            block = next()
            if (predicate(block)) {
                break
            }
        }
        return block
    }

    inline fun <reified T : Any> getType(): KClass<T> = T::class

    fun JavaPlugin.registerAllCommands(vararg commands: Command2) =
        commands.forEach { it.register(this) }

    fun JavaPlugin.registerAllListeners(vararg listeners: Listener) =
        listeners.forEach { server.pluginManager.registerEvents(it, this) }
}