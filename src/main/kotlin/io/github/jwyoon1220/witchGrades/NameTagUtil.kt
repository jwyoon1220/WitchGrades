// Kotlin
package io.github.jwyoon1220.witchGrades

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.scoreboard.Team
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f

object NameTagUtil {

    // 플레이어를 이름표 숨김 팀에 추가 (전역 설정)
    fun applyNoNametagTeam(player: Player, teamName: String = "wg_hidden_nametag") {
        val sb = Bukkit.getScoreboardManager().mainScoreboard
        val team = sb.getTeam(teamName) ?: sb.registerNewTeam(teamName)
        team.setCanSeeFriendlyInvisibles(true)
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
        if (!team.hasEntry(player.name)) {
            team.addEntry(player.name)
        }
    }

    // TextDisplay 엔티티 스폰 (모든 플레이어에게 보임)
    fun spawnTextDisplay(loc: Location, text: String): TextDisplay {
        val world = loc.world ?: throw IllegalStateException("월드가 null입니다.")
        val entity = world.spawnEntity(loc, EntityType.TEXT_DISPLAY) as TextDisplay
        entity.text(Component.text(text))
        // 필요 시 표시 옵션 조정
        entity.billboard = Display.Billboard.CENTER
        entity.isSeeThrough = true
        entity.isShadowed = false
        entity.alignment = TextDisplay.TextAlignment.CENTER
        entity.transformation = Transformation(
            Vector3f(0f, 0.3f, 0f),          // translation (위쪽 오프셋)
            Quaternionf(),                          // left rotation
            Vector3f(1f, 1f, 1f),                    // scale
            Quaternionf()                           // right rotation
        )

        return entity
    }

    // TextDisplay 텍스트 업데이트
    fun updateTextDisplay(display: TextDisplay, newText: String) {
        display.text(Component.text(newText))
    }

    // TextDisplay 제거
    fun destroyTextDisplay(entity: Entity?) {
        entity?.remove()
    }
}
