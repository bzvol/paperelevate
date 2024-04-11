package me.bzvol.paperelevate.command

import me.bzvol.paperelevate.PaperElevate
import me.bzvol.paperelevate.command.CommandUtil.isPlayer
import me.bzvol.paperelevate.command.argparser.ArgParser
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class Command2 private constructor(
    val name: String, private val playerOnly: Boolean = false,
    private var parentCommandString: String?,
    private val commandUsage: String?,
    open val subCommands: List<Command2>,
    open val argParser: ArgParser?,
    open val permission: String?
) : TabExecutor {
    open val usage: String
        get() = commandUsage ?: CommandUtil.buildUsage(pcsExtRight(parentCommandString, name), subCommands, argParser)

    protected constructor(name: String, playerOnly: Boolean = false, parentCommandString: String? = null)
            : this(name, playerOnly, parentCommandString, null, emptyList(), null, null)

    protected val subCommandNames: List<String> by lazy {
        subCommands.map { it.name }
    }

    override fun onCommand(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<String>
    ): Boolean {
        if (!permission.isNullOrBlank() && !sender.hasPermission(permission!!))
            sender.sendMessage(
                (if (sender.isPlayer) "§c" else "") + "You do not have permission to execute this command."
            )
        else execute(sender, args)

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        val completions = tabCompletions(sender, args, args.size - 1)
        return completions.filter { it.startsWith(args.last()) }.sorted()
    }

    open fun execute(sender: CommandSender, args: Array<String>) {
        if (playerOnly)
            if (!sender.isPlayer) return
            else execute(sender as Player, args)
        else if (args.isEmpty()) sender.sendUsage()
        else if (subCommands.isNotEmpty()) executeSubCommands(sender, args)
        else sender.sendUsage()
    }

    open fun execute(sender: Player, args: Array<String>) {
        if (args.isEmpty()) sender.sendUsage()
        else if (subCommands.isNotEmpty()) executeSubCommands(sender, args)
        else sender.sendUsage()
    }

    open fun tabCompletions(sender: CommandSender, args: Array<String>, argIndex: Int): List<String> =
        if (subCommands.isEmpty())
            argParser?.tabCompletions(args, argIndex) ?: emptyList()
        else
            if (argIndex == 0) subCommandNames
            else subCommandTabCompletions(sender, args, argIndex)

    fun CommandSender.sendUsage() {
        sendMessage(usage)
    }

    fun executeSubCommands(sender: CommandSender, args: Array<String>) {
        val slicedArgs = args.drop(1).toTypedArray()
        subCommands.find { it.name == args[0] }?.execute(sender, slicedArgs)
            ?: sender.sendUsage()
    }

    fun subCommandTabCompletions(sender: CommandSender, args: Array<String>, argIndex: Int): List<String> {
        val slicedArgs = args.drop(1).toTypedArray()
        return if (argIndex == 0) subCommandNames
        else subCommands.find { it.name == args[0] }?.tabCompletions(sender, slicedArgs, argIndex - 1)
            ?: emptyList()
    }

    fun register(plugin: JavaPlugin) =
        if (parentCommandString?.isNotBlank() == true) throw IllegalStateException("Cannot register subcommands")
        else plugin.getCommand(name)?.setExecutor(this)

    private fun refreshAllPcs(addition: String) {
        parentCommandString = pcsExtLeft(parentCommandString, addition)
        subCommands.forEach { it.refreshAllPcs(addition) }
    }

    class Builder private constructor(
        private val name: String,
        private val playerOnly: Boolean,
        private val parentCommandString: String?,
    ) {
        private var usage: String? = null
        private var subCommands: MutableList<Command2> = mutableListOf()
        private var argParser: ArgParser? = null
        private var permission: String? = null

        private var action: (Command2.(CommandSender, Array<String>) -> Unit)? = null
        private var playerAction: (Command2.(Player, Array<String>) -> Unit)? = null

        fun usage(usage: String) = apply { this.usage = usage }

        fun subCommand(name: String, playerOnly: Boolean = false, init: Builder.() -> Unit) = apply {
            val builder = Builder(name, playerOnly, pcsExtRight(parentCommandString, this.name)).apply(init)
            subCommands.add(builder.build())
        }

        fun subCommand(command: Command2) = apply {
            val newPcs = pcsExtRight(parentCommandString, name)
            subCommands.add(command.apply { refreshAllPcs(newPcs) })
        }

        fun argParser(argParser: ArgParser) = apply { this.argParser = argParser }

        fun permission(permission: String) = apply { this.permission = permission }

        fun action(action: Command2.(CommandSender, Array<String>) -> Unit) = apply { this.action = action }

        @JvmName("playerAction")
        fun action(action: Command2.(Player, Array<String>) -> Unit) = apply { this.playerAction = action }

        fun build() =
            object : Command2(name, playerOnly, parentCommandString, usage, subCommands, argParser, permission) {
                override fun execute(sender: CommandSender, args: Array<String>) =
                    action?.invoke(this, sender, args) ?: super.execute(sender, args)

                override fun execute(sender: Player, args: Array<String>) =
                     playerAction?.invoke(this, sender, args)
                         ?: action?.invoke(this, sender, args)
                         ?: super.execute(sender, args)
            }

        fun buildAndRegister(plugin: JavaPlugin) = build().apply { register(plugin) }

        companion object {
            fun builder(name: String, playerOnly: Boolean = false, parentCommandString: String? = null) =
                Builder(name, playerOnly, parentCommandString)
        }
    }

    companion object {
        fun builder(name: String, playerOnly: Boolean = false, parentCommandString: String? = null) =
            Builder.builder(name, playerOnly, parentCommandString)

        private fun pcsExtRight(pcs: String?, addition: String) = if (pcs == null) addition else "$pcs $addition"
        private fun pcsExtLeft(pcs: String?, addition: String) = if (pcs == null) addition else "$addition $pcs"
    }
}