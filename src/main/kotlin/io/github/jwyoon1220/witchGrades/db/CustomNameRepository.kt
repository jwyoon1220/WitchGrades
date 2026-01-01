package io.github.jwyoon1220.witchGrades.db

import io.github.jwyoon1220.witchGrades.model.CustomName
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class CustomNameRepository(private val plugin: Plugin) {

    private val cache = ConcurrentHashMap<UUID, CustomName>()
    private val file: File = File(plugin.dataFolder, "custom-names.yml")
    private val config: YamlConfiguration = YamlConfiguration()

    init {
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdirs()
        if (!file.exists()) file.createNewFile()
        config.load(file)
        loadAll()
    }

    fun get(uuid: UUID): CustomName =
        cache.computeIfAbsent(uuid) {
            val section = config.getConfigurationSection("players.${uuid}") ?: return@computeIfAbsent CustomName(uuid)
            val title = section.getString("title")
            val nickname = section.getString("nickname")
            CustomName(uuid, title, nickname)
        }

    fun setTitle(uuid: UUID, title: String?) {
        val cn = get(uuid)
        cn.title = title
        config.set("players.${uuid}.title", title)
        save()
    }

    fun setNickname(uuid: UUID, nickname: String?) {
        val cn = get(uuid)
        cn.nickname = nickname
        config.set("players.${uuid}.nickname", nickname)
        save()
    }

    fun save() {
        config.save(file)
    }

    private fun loadAll() {
        val root = config.getConfigurationSection("players") ?: return
        for (key in root.getKeys(false)) {
            val uuid = UUID.fromString(key)
            val title = root.getString("$key.title")
            val nickname = root.getString("$key.nickname")
            cache[uuid] = CustomName(uuid, title, nickname)
        }
    }
}