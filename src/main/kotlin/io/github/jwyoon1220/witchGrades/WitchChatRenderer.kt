package io.github.jwyoon1220.witchGrades

import io.github.jwyoon1220.witchGrades.service.CustomNameService
import io.papermc.paper.chat.ChatRenderer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.bukkit.entity.Player

class WitchChatRenderer(private val service: CustomNameService): ChatRenderer {
    override fun render(player: Player, playerName: Component, message: Component, viewer: Audience): Component {
        val grade = WitchGrades.gradeManager.getGrade(player.uniqueId)


        var pn = service.getNickname(player)
        if (pn == "Unknown") {
            pn = player.name
        }
        return text(grade.title).append(text(" ")).append(text(pn ?: player.name, grade.color)).append(text(" : ")).append(message)
    }
}