package me.bzvol.paperelevate.command

import me.bzvol.paperelevate.PaperElevate
import me.bzvol.paperelevate.command.argparser.ArgParser
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object CommandUtil {
    fun CommandSender.sendPrefixedMessage(message: String) {
        val prefix = PaperElevate.CONFIG?.getString("plugin-prefix")
        if (!prefix.isNullOrEmpty())
            sendMessage(prefix + message)
        else
            sendMessage(message)
    }

    val CommandSender.isPlayer: Boolean
        get() {
            if (this !is Player) {
                sendMessage("This command can only be executed by players.")
                return false
            }
            return true
        }

    fun buildUsage(name: String, subCommands: List<Command2>, argParser: ArgParser?): String {
        val usage = StringBuilder("${UsageColors.titleColor}Usage: ")

        if (subCommands.isEmpty() && argParser == null) {
            usage.append("${UsageColors.commandColor}/$name")
            return usage.toString()
        }

        val multiLine = subCommands.isNotEmpty() && argParser != null

        if (multiLine) usage.append("\n- ")
        if (argParser != null)
            usage.append("${UsageColors.commandColor}/$name ${argParser.usage}")

        if (multiLine) usage.append("\n- ")
        if (subCommands.isNotEmpty()) {
            usage.append("${UsageColors.commandColor}/$name ")
            usage.append(
                subCommands.joinToString(
                    "${UsageColors.otherColor}|${UsageColors.subCommandColor}",
                    "${UsageColors.otherColor}<${UsageColors.subCommandColor}",
                    "${UsageColors.otherColor}>"
                ) { it.name }
            )
        }

        return usage.toString()
    }
}