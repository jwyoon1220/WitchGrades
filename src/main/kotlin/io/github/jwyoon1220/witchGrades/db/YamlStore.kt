package io.github.jwyoon1220.witchGrades.db

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class YamlStore(private val file: File) {

    val config: YamlConfiguration

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    @Synchronized
    fun save() {
        config.save(file)
    }
}
