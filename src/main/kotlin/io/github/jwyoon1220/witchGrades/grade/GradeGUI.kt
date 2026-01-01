package io.github.jwyoon1220.witchGrades.grade

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MenuType

class GradeGUI(private val gradeManager: GradeManager) {

    fun open(player: Player) {
        val currentGrade = gradeManager.getGrade(player.uniqueId)

        val inv: Inventory = Bukkit.createInventory(
            null,
            9 * 3,
            Component.text("성장 단계", NamedTextColor.DARK_GREEN)
        )

        WitchGrade.entries.forEachIndexed { index, grade ->
            val item =
                if (grade.level <= currentGrade.level) {
                    unlockedItem(grade)
                } else {
                    lockedItem()
                }

            inv.setItem(index, item)
        }

        player.openInventory(inv)
    }


    /** 이미 달성한 단계 */
    private fun unlockedItem(grade: WitchGrade): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta

        meta.displayName(
            Component.text(grade.displayName)
                .color(grade.color)
        )

        meta.lore(
            listOf(
                Component.text("✔ 달성 완료", NamedTextColor.GREEN),
                Component.text(grade.englishName, NamedTextColor.GRAY),
                Component.text(grade.description, NamedTextColor.DARK_GRAY)
            )
        )

        item.itemMeta = meta
        return item
    }

    /** 다음 단계 (1단계만 공개) */
    private fun nextItem(grade: WitchGrade): ItemStack {
        val item = ItemStack(Material.BOOK)
        val meta = item.itemMeta

        meta.displayName(
            Component.text("다음 단계: ${grade.displayName}")
                .color(grade.color)
        )

        meta.lore(
            listOf(
                Component.text("▶ 아직 도달하지 못했습니다", NamedTextColor.YELLOW),
                Component.text("달성 시 새로운 힘이 깨어납니다", NamedTextColor.GRAY)
            )
        )

        item.itemMeta = meta
        return item
    }

    /** 잠긴 단계 */
    private fun lockedItem(): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta

        meta.displayName(
            Component.text("???", NamedTextColor.DARK_GRAY)
        )

        meta.lore(
            listOf(
                Component.text("아직 알 수 없습니다.", NamedTextColor.GRAY)
            )
        )

        item.itemMeta = meta
        return item
    }
}
