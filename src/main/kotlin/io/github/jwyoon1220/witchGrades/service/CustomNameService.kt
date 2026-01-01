package io.github.jwyoon1220.witchGrades.service

import io.github.jwyoon1220.witchGrades.NameTagUtil
import io.github.jwyoon1220.witchGrades.WitchGrades
import io.github.jwyoon1220.witchGrades.db.CustomNameRepository
import io.github.jwyoon1220.witchGrades.model.CustomName
import io.github.jwyoon1220.witchGrades.model.TitleEntry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay

class CustomNameService(
    private val repo: CustomNameRepository
) {
    private val displays: MutableMap<Player, TextDisplay> = mutableMapOf()
    private val mm = MiniMessage.miniMessage()

    // 다중 칭호 관리
    @Deprecated("안씀")
    fun getTitles(player: Player): List<TitleEntry> =
        repo.get(player.uniqueId).titles.ifEmpty { emptyList() }
    @Deprecated("안씀")
    fun addTitle(player: Player, entry: TitleEntry) {
        val current = getTitles(player).toMutableList()
        if (current.none { it.name == entry.name }) {
            current.add(entry)
            repo.setTitles(player.uniqueId, current)
            if (getTitle(player) == null) setTitle(player, entry.name)
        }
    }
    @Deprecated("안씀")
    fun removeTitle(player: Player, name: String) {
        val updated = getTitles(player).filterNot { it.name == name }
        repo.setTitles(player.uniqueId, updated)
        if (getTitle(player) == name) setTitle(player, null)
    }

    // 활성 칭호 및 닉네임
    @Deprecated("안씀")
    fun getTitle(player: Player): String? = repo.get(player.uniqueId).title
    @Deprecated("안씀")
    fun setTitle(player: Player, name: String?) {
        repo.setTitle(player.uniqueId, name)
        applyDisplay(player)
    }

    fun getNickname(player: Player): String? = repo.get(player.uniqueId).nickname

    fun setNickname(player: Player, nickname: String?) {
        repo.setNickname(player.uniqueId, nickname)
        applyDisplay(player)
    }

    // 표시 엔티티 생성/갱신
    fun applyDisplay(player: Player) {
        NameTagUtil.applyNoNametagTeam(player)

        val gm = WitchGrades.gradeManager
        val grade = gm.getGrade(player.uniqueId)


        val cn: CustomName = repo.get(player.uniqueId)
        val loc: Location = player.location.clone().add(0.0, 0.3, 0.0)
        val current = displays[player]

        player.playerListName(text("${grade.title} ").append(text(cn.nickname ?: player.name, grade.color)))
        val textComponent: Component = mm.deserialize(cn.nickname ?: player.name)

        if (current == null || current.isDead || !current.isValid || current.world != player.world) {
            val spawned = NameTagUtil.spawnTextDisplay(loc, cn.nickname ?: player.name)
            spawned.text(text("${grade.title} ").append(textComponent))
            spawned.customName(textComponent)
            displays[player] = spawned
        } else {
            current.teleport(loc)
            current.text(textComponent)
            current.customName(textComponent)
        }
    }

    fun removeDisplay(player: Player) {
        val display = displays.remove(player)
        NameTagUtil.destroyTextDisplay(display)
    }
}