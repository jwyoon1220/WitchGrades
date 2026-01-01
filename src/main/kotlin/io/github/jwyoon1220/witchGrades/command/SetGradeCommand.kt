package io.github.jwyoon1220.witchGrades.command

import io.github.jwyoon1220.witchGrades.WitchGrades
import io.github.jwyoon1220.witchGrades.grade.WitchGrade
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetGradeCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (args.size != 2) {
            sender.sendMessage("§c사용법: /setgrade <player> <level>")
            return true
        }

        val target: Player = Bukkit.getPlayer(args[0])
            ?: run {
                sender.sendMessage("§c플레이어를 찾을 수 없습니다.")
                return true
            }

        val level = args[1].toIntOrNull()
            ?: run {
                sender.sendMessage("§c레벨은 숫자여야 합니다.")
                return true
            }

        val grade = WitchGrade.byLevel(level)

        WitchGrades.gradeManager.setGrade(target.uniqueId, grade)

        sender.sendMessage(
            "§a${target.name}의 등급을 §f${grade.displayName} §a(레벨 ${grade.level})로 설정했습니다."
        )

        target.sendMessage(
            "§e당신의 등급이 §f${grade.displayName} §e(으)로 변경되었습니다."
        )

        return true
    }
}
