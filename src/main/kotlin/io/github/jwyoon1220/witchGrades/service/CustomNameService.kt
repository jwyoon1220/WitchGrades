package io.github.jwyoon1220.witchGrades.service

import io.github.jwyoon1220.witchGrades.NameTagUtil
import io.github.jwyoon1220.witchGrades.db.CustomNameRepository
import io.github.jwyoon1220.witchGrades.model.CustomName
import org.bukkit.Location
import org.bukkit.entity.Player

class CustomNameService(
    private val repo: CustomNameRepository
) {
    // player별 TEXT_DISPLAY 엔티티 ID 캐시
    private val entityIds = mutableMapOf<Player, Int>()

    fun getTitle(player: Player): String? = repo.get(player.uniqueId).title
    fun setTitle(player: Player, title: String?) {
        repo.setTitle(player.uniqueId, title)
        applyDisplay(player)
    }

    fun getNickname(player: Player): String? = repo.get(player.uniqueId).nickname
    fun setNickname(player: Player, nickname: String?) {
        repo.setNickname(player.uniqueId, nickname)
        applyDisplay(player)
    }

    // TEXT\_DISPLAY 적용/갱신
    fun applyDisplay(player: Player) {
        NameTagUtil.applyNoNametagTeam(player)

        val cn: CustomName = repo.get(player.uniqueId)
        val text = cn.formatted()

        val loc: Location = player.location.clone().add(0.0, 0.3, 0.0)
        val id = entityIds[player]
        if (id == null) {
            val newId = NameTagUtil.spawnTextDisplay(player, loc, text)
            entityIds[player] = newId
        } else {
            NameTagUtil.updateTextDisplay(player, id, text)
        }
    }

    // 플레이어가 떠날 때 정리
    fun removeDisplay(player: Player) {
        entityIds.remove(player)?.let { NameTagUtil.destroyTextDisplay(player, it) }
    }
}