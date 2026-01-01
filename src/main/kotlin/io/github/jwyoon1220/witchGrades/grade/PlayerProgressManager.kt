package io.github.jwyoon1220.witchGrades.grade

import io.github.jwyoon1220.witchGrades.WitchGrades
import io.github.jwyoon1220.witchGrades.db.YamlStore
import org.bukkit.Bukkit
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerProgressManager(private val plugin: WitchGrades) {

    private data class Cache(
        var playtime: Long,
        var crystals: Int
    )

    private var id: Int = 0
    private var id2: Int = 0

    private val store = YamlStore(File(plugin.dataFolder, "players.yml"))
    private val cache = ConcurrentHashMap<UUID, Cache>()

    fun ensurePlayer(uuid: UUID) {
        if (cache.containsKey(uuid)) return

        val base = "players.$uuid"

        val playtime = store.config.getLong("$base.playtime", 0L)
        val crystals = store.config.getInt("$base.crystals", 0)

        if (!store.config.contains(base)) {
            store.config.set("$base.playtime", 0L)
            store.config.set("$base.crystals", 0)
            store.save()
        }

        cache[uuid] = Cache(playtime, crystals)
    }

    fun tickPlaytime(uuid: UUID, seconds: Long = 1) {
        val data = cache[uuid] ?: return

        data.playtime += seconds

        val expectedCrystals = (data.playtime / 3600).toInt()
        if (data.crystals != expectedCrystals) {
            data.crystals = expectedCrystals
        }
    }


    fun addCrystals(uuid: UUID, amount: Int) {
        val data = cache[uuid] ?: return
        data.crystals += amount
        flush(uuid)
    }

    fun consumeCrystals(uuid: UUID, amount: Int): Boolean {
        val data = cache[uuid] ?: return false
        if (data.crystals < amount) return false

        data.crystals -= amount
        flush(uuid)
        return true
    }

    private fun flush(uuid: UUID) {
        val data = cache[uuid] ?: return
        val base = "players.$uuid"

        store.config.set("$base.playtime", data.playtime)
        store.config.set("$base.crystals", data.crystals)
        store.save()
    }

    fun startAutoFlushTask() {
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(
            plugin,
            Runnable {
                val snapshot = cache.keys.toList()

                Bukkit.getScheduler().runTask(plugin, Runnable {
                    snapshot.forEach { flush(it) }
                })
            },
            20L * 3600L,
            20L * 3600L
        ).taskId
        id2 = Bukkit.getScheduler().runTaskTimer(
            plugin,
            Runnable {
                for (player in Bukkit.getOnlinePlayers()) {
                    val uuid = player.uniqueId
                    if (!WitchGrades.afkList[player]!!) tickPlaytime(uuid, 1)
                    //player.sendMessage("${uuid}, 1")
                }
            },
            20L,   // 서버 시작 후 1초 뒤
            20L    // 1초 주기
        ).taskId
    }

    fun shutdown() {
        cache.keys.forEach { flush(it) }
        Bukkit.getScheduler().cancelTasks(plugin)
    }

    fun getPlaytime(uuid: UUID): Long = cache[uuid]?.playtime ?: 0L
    fun getCrystals(uuid: UUID): Int = cache[uuid]?.crystals ?: 0
}
