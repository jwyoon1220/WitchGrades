package io.github.jwyoon1220.witchGrades.command

import io.github.jwyoon1220.witchGrades.service.CustomNameService
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CustomNameCommand(
    private val service: CustomNameService
) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("이 명령어는 OP만 사용할 수 있습니다.")
            return true
        }

        if (args.isEmpty()) {
            sendUsage(sender, label)
            return true
        }

        val sub = args[0].lowercase()
        when (sub) {
            "set", "설정" -> {
                if (args.size < 3) {
                    sender.sendMessage("사용법: /$label set|설정 <플레이어> <닉네임>")
                    return true
                }
                val targetName = args[1]
                val target: Player? = Bukkit.getPlayerExact(targetName)
                if (target == null) {
                    sender.sendMessage("플레이어를 찾을 수 없습니다: $targetName")
                    return true
                }
                val nickname = args.drop(2).joinToString(" ")
                service.setNickname(target, nickname)
                sender.sendMessage("닉네임 설정 완료: ${target.name} -> $nickname")
                return true
            }
            "reset", "재설정" -> {
                if (args.size < 2) {
                    sender.sendMessage("사용법: /$label reset|재설정 <플레이어>")
                    return true
                }
                val targetName = args[1]
                val target: Player? = Bukkit.getPlayerExact(targetName)
                if (target == null) {
                    sender.sendMessage("플레이어를 찾을 수 없습니다: $targetName")
                    return true
                }
                service.setNickname(target, null)
                sender.sendMessage("닉네임이 기본값으로 복구되었습니다: ${target.name}")
                return true
            }
            else -> {
                sendUsage(sender, label)
                return true
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (!sender.isOp) return mutableListOf()

        return when (args.size) {
            1 -> listOf("set", "설정", "reset", "재설정")
                .filter { it.startsWith(args[0], ignoreCase = true) }
                .toMutableList()
            2 -> {
                val subs = setOf("set", "설정", "reset", "재설정")
                if (subs.contains(args[0].lowercase())) {
                    Bukkit.getOnlinePlayers()
                        .map { it.name }
                        .filter { it.startsWith(args[1], ignoreCase = true) }
                        .toMutableList()
                } else mutableListOf()
            }
            3 -> {
                val sub = args[0].lowercase()
                if (sub == "set" || sub == "설정") {
                    // 닉네임 자리 힌트
                    mutableListOf("<닉네임>").filter { it.startsWith(args[2], ignoreCase = true) }.toMutableList()
                } else mutableListOf()
            }
            else -> mutableListOf()
        }
    }

    private fun sendUsage(sender: CommandSender, label: String) {
        sender.sendMessage("/$label set|설정 <플레이어> <닉네임>")
        sender.sendMessage("/$label reset|재설정 <플레이어>")
    }
}