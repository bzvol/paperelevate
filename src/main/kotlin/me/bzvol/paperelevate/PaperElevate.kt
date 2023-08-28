package me.bzvol.paperelevate

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

object PaperElevate {
    var PLUGIN: JavaPlugin? = null
    val CONFIG: FileConfiguration?
        get() = PLUGIN?.config
}