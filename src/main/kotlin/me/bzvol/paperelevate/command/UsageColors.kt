package me.bzvol.paperelevate.command

import me.bzvol.paperelevate.Colors

object UsageColors {
    // Example for defaults:

    // For usage:
    // "§eUsage: §r/fsp loot §7<§badd§7|§bdelete§7|§bsetcooldown§7|§blist§7|§binfo§7> §3[options]"

    // For arguments:
    // override val usage: String
    //        get() = if (required) "§b$shortFlag/$longFlag §7<§b$placeholder§7>§r"
    //        else "§3[§b$shortFlag/$longFlag §7<§b$placeholder§7>§3]§r"

    var titleColor = Colors.YELLOW

    var commandColor = Colors.WHITE
    var subCommandColor = Colors.AQUA

    var argumentColor = Colors.GOLD
    var optionalArgumentColor = Colors.DARK_AQUA

    var otherColor = Colors.GRAY
    var resetColor = Colors.RESET
}