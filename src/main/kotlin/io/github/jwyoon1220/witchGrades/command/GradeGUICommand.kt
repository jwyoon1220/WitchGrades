package io.github.jwyoon1220.witchGrades.command

import io.github.jwyoon1220.witchGrades.WitchGrades
import io.github.jwyoon1220.witchGrades.grade.GradeGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GradeGUICommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            return false
        }

        val player = sender

        val gui = GradeGUI(WitchGrades.gradeManager)
        gui.open(player)
        return true
    }
}