package io.github.jwyoon1220.witchGrades.grade

import io.github.jwyoon1220.witchGrades.WitchGrades
import io.github.jwyoon1220.witchGrades.db.YamlStore
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.io.File
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GradeManager(private val plugin: WitchGrades) {

    private val store = YamlStore(File(plugin.dataFolder, "grades.yml"))
    private val mm = MiniMessage.miniMessage()

    private val cache = ConcurrentHashMap<UUID, WitchGrade>()

    fun ensurePlayer(uuid: UUID) {
        if (cache.containsKey(uuid)) return

        val path = "players.$uuid.level"
        val level = store.config.getInt(path, 1)

        if (!store.config.contains(path)) {
            store.config.set(path, 1)
            store.save()
        }

        cache[uuid] = WitchGrade.byLevel(level)
    }

    fun getGrade(uuid: UUID): WitchGrade {
        return cache.computeIfAbsent(uuid) {
            ensurePlayer(uuid)
            WitchGrade.byLevel(store.config.getInt("players.$uuid.level", 1))
        }
    }

    fun setGrade(uuid: UUID, grade: WitchGrade) {
        cache[uuid] = grade

        store.config.set("players.$uuid.level", grade.level)
        store.save()

        Bukkit.getPlayer(uuid)?.let { player ->
            player.showTitle(
                Title.title(
                    mm.deserialize(grade.message),
                    mm.deserialize(grade.description),
                    Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofSeconds(3),
                        Duration.ofMillis(500)
                    )
                )
            )

            player.playSound(
                player.location,
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                1.0f,
                1.0f
            )
        }
    }

    fun remove(uuid: UUID) {
        cache.remove(uuid)
        store.config.set("players.$uuid", null)
        store.save()
    }
}
