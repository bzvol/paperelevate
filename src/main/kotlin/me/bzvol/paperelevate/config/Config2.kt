package me.bzvol.paperelevate.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

abstract class Config2(private val plugin: JavaPlugin, private val name: String) {
    private val file: File = File(plugin.dataFolder, "$name.yml")
    protected var config: FileConfiguration

    init {
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
                plugin.logger.severe("Failed to create $name.yml file!")
            }
        }

        config = YamlConfiguration.loadConfiguration(file)
    }

    protected fun setDefaults(vararg defaults: Pair<String, Any>) {
        defaults.forEach { (key, value) ->
            config.addDefault(key, value)
        }

        config.options().copyDefaults(true)
        save()
    }

    fun save() {
        try {
            config.save(file)
        } catch (e: Exception) {
            e.printStackTrace()
            plugin.logger.severe("Failed to save $name.yml file!")
        }
    }

    fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
    }

    protected fun setAndReload(key: String, value: Any) {
        config.set(key, value)
        save()
        reload()
    }
}