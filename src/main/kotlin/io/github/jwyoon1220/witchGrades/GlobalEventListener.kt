package io.github.jwyoon1220.witchGrades

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.*
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import com.sun.jna.IntegerType
import io.github.jwyoon1220.witchGrades.service.CustomNameService
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class GlobalEventListener(private val service: CustomNameService): Listener {


    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val joined = event.player
        WitchGrades.gradeManager.ensurePlayer(joined.uniqueId)
        WitchGrades.playerProgressManager.ensurePlayer(joined.uniqueId)
        WitchGrades.afkList[joined] = false
        service.applyDisplay(joined)

    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        WitchGrades.afkList.remove(event.player)
        service.removeDisplay(event.player)
    }
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        WitchGrades.afkList[event.player] = false
    }
    @EventHandler
    fun onTp(event: PlayerTeleportEvent) {
        service.removeDisplay(event.player)
        Bukkit.getScheduler().runTaskLater(WitchGrades.plugin, Runnable {
            service.applyDisplay(event.player)
        }, 20L)
    }
    @EventHandler
    fun onDimensionChange(event: PlayerPortalEvent) {
        service.removeDisplay(event.player)
        Bukkit.getScheduler().runTaskLater(WitchGrades.plugin, Runnable {
            service.applyDisplay(event.player)
        }, 20L)
    }
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val title = "성장 단계"
        if (event.view.title.contains(title)) {
            event.isCancelled = true
        }
    }
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        event.renderer(WitchChatRenderer(service))
    }
}