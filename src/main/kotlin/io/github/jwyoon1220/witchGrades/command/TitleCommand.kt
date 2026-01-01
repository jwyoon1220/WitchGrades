package io.github.jwyoon1220.witchGrades.command

import io.github.jwyoon1220.witchGrades.model.TitleEntry
import io.github.jwyoon1220.witchGrades.service.CustomNameService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class TitleCommand(
    private val service: CustomNameService
) : CommandExecutor, TabCompleter, Listener {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("이 명령어는 OP만 사용할 수 있습니다.")
            return true
        }

        // 만들기/create
        if (args.isNotEmpty() && (args[0].equals("create", true) || args[0] == "만들기")) {
            return handleCreate(sender, args.drop(1))
        }

        // 추가/add <player> <titleName>
        if (args.isNotEmpty() && (args[0].equals("add", true) || args[0] == "추가")) {
            return handleAdd(sender, args.drop(1))
        }

        // 제거/remove <player> <titleName>
        if (args.isNotEmpty() && (args[0].equals("remove", true) || args[0] == "제거")) {
            return handleRemove(sender, args.drop(1))
        }

        val target: Player? = if (args.isNotEmpty()) Bukkit.getPlayerExact(args[0]) else (sender as? Player)
        if (target == null) {
            sender.sendMessage("플레이어를 찾을 수 없습니다.")
            return true
        }
        openTitleGui(target)
        return true
    }

    private fun handleCreate(sender: CommandSender, args: List<String>): Boolean {
        val player = sender as? Player
        if (player == null) {
            sender.sendMessage("콘솔에서는 사용할 수 없습니다. 플레이어로 실행하세요.")
            return true
        }
        if (args.size < 3) {
            sender.sendMessage("사용법: /칭호 만들기 <이름> <설명> <MiniMessage 본문>")
            return true
        }
        val name = args[0]
        val description = args[1]
        val bodyMini = args.drop(2).joinToString(" ")

        val entry = TitleEntry(name, description, bodyMini)
        service.addTitle(player, entry)
        service.setTitle(player, name) // 바로 활성화
        sender.sendMessage("새 칭호를 만들고 적용했습니다: [$name]")
        return true
    }

    private fun handleAdd(sender: CommandSender, args: List<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("사용법: /칭호 추가 <플레이어> <칭호이름>")
            return true
        }
        val target = Bukkit.getPlayerExact(args[0])
        if (target == null) {
            sender.sendMessage("플레이어를 찾을 수 없습니다.")
            return true
        }
        val titleName = args[1]

        // 실행자가 가진 칭호 템플릿을 기준으로 부여
        val template = (sender as? Player)
            ?.let { service.getTitles(it).firstOrNull { t -> t.name == titleName } }

        if (template == null) {
            sender.sendMessage("해당 칭호 템플릿을 찾을 수 없습니다. /칭호 만들기 로 먼저 생성하세요.")
            return true
        }

        val before = service.getTitles(target).size
        service.addTitle(target, template)
        val after = service.getTitles(target).size
        if (after == before) {
            sender.sendMessage("${target.name} 은(는) 이미 [$titleName] 칭호를 보유하고 있습니다.")
        } else {
            sender.sendMessage("${target.name} 에게 [$titleName] 칭호를 부여했습니다.")
            target.sendMessage("새 칭호를 획득했습니다: [$titleName]")
        }
        return true
    }

    private fun handleRemove(sender: CommandSender, args: List<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("사용법: /칭호 제거 <플레이어> <칭호이름>")
            return true
        }
        val target = Bukkit.getPlayerExact(args[0])
        if (target == null) {
            sender.sendMessage("플레이어를 찾을 수 없습니다.")
            return true
        }
        val titleName = args[1]

        val owned = service.getTitles(target)
        if (owned.none { it.name == titleName }) {
            sender.sendMessage("${target.name} 은(는) [$titleName] 칭호를 가지고 있지 않습니다.")
            return true
        }

        service.removeTitle(target, titleName) // 활성 칭호였으면 내부에서 해제됨
        sender.sendMessage("${target.name} 의 [$titleName] 칭호를 제거했습니다.")
        target.sendMessage("[$titleName] 칭호가 제거되었습니다.")
        return true
    }

    private fun openTitleGui(player: Player) {
        val titles = service.getTitles(player)
        val inv: Inventory = Bukkit.createInventory(player, 54, Component.text("칭호 선택"))
        var slot = 0
        titles.forEach { entry ->
            val item = ItemStack(Material.NAME_TAG)
            val meta: ItemMeta = item.itemMeta
            meta.displayName(Component.text("[${entry.name}]"))
            if (entry.description.isNotBlank()) {
                meta.lore(listOf(Component.text(entry.description)))
            }
            item.itemMeta = meta
            inv.setItem(slot++, item)
        }
        // 현재 칭호 해제 버튼
        val clear = ItemStack(Material.BARRIER)
        val m = clear.itemMeta
        m.displayName(Component.text("칭호 해제"))
        clear.itemMeta = m
        inv.setItem(53, clear)

        player.openInventory(inv)
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val player = e.whoClicked as? Player ?: return
        val inv = e.clickedInventory ?: return
        val title = e.currentItem ?: return
        if (player.openInventory.title() != Component.text("칭호 선택")) return
        e.isCancelled = true

        val name = LegacyComponentSerializer.legacySection().serialize(title.itemMeta.displayName() ?: return)
        if (title.type == Material.BARRIER) {
            service.setTitle(player, null)
            player.closeInventory()
            player.sendMessage("칭호를 해제했습니다.")
            return
        }
        val selected = name.removePrefix("[").removeSuffix("]")
        service.setTitle(player, selected)
        player.closeInventory()
        player.sendMessage("선택한 칭호: [$selected]")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if (!sender.isOp) return mutableListOf()
        return when (args.size) {
            1 -> listOf("create", "만들기", "add", "추가", "remove", "제거") +
                    Bukkit.getOnlinePlayers().map { it.name }
                        .filter { it.startsWith(args[0], ignoreCase = true) }

            2 -> when {
                args[0].equals("add", true) || args[0] == "추가" ||
                        args[0].equals("remove", true) || args[0] == "제거" ->
                    Bukkit.getOnlinePlayers().map { it.name }
                        .filter { it.startsWith(args[1], ignoreCase = true) }
                        .toMutableList()

                else -> mutableListOf()
            }

            3 -> when {
                args[0].equals("add", true) || args[0] == "추가" ->
                    (sender as? Player)?.let { service.getTitles(it).map { t -> t.name } }
                        ?.filter { it.startsWith(args[2], ignoreCase = true) }
                        ?.toMutableList() ?: mutableListOf()

                args[0].equals("remove", true) || args[0] == "제거" -> {
                    val target = Bukkit.getPlayerExact(args[1])
                    target?.let { service.getTitles(it).map { t -> t.name } }
                        ?.filter { it.startsWith(args[2], ignoreCase = true) }
                        ?.toMutableList() ?: mutableListOf()
                }

                else -> mutableListOf()
            }

            else -> mutableListOf()
        }.toMutableList()
    }
}