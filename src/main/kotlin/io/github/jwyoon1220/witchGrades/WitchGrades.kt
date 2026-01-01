package io.github.jwyoon1220.witchGrades

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import io.github.jwyoon1220.witchGrades.command.GradeGUICommand
import io.github.jwyoon1220.witchGrades.command.SetGradeCommand
import io.github.jwyoon1220.witchGrades.db.CustomNameRepository
import io.github.jwyoon1220.witchGrades.grade.GradeManager
import org.bukkit.Bukkit
import io.github.jwyoon1220.witchGrades.grade.PlayerProgressManager
import io.github.jwyoon1220.witchGrades.service.CustomNameService
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class WitchGrades : JavaPlugin() {

    companion object {
        lateinit var gradeManager: GradeManager
        lateinit var playerProgressManager: PlayerProgressManager
        lateinit var plugin: WitchGrades

        val afkList = mutableMapOf<Player, Boolean>()

        const val isDebug = true //프로덕션에서는 제거할것!
    }

    lateinit var customNameService: CustomNameService
        private set


    var protocolManager: ProtocolManager? = null

    override fun onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onEnable() {
        plugin = this
        gradeManager = GradeManager(this)
        playerProgressManager = PlayerProgressManager(this)
        saveDefaultConfig()
        getCommand("witch_grade")?.setExecutor(SetGradeCommand())
        getCommand("등급")?.setExecutor(GradeGUICommand())
        playerProgressManager.startAutoFlushTask()

        val repo = CustomNameRepository(this)
        customNameService = CustomNameService(repo)


        server.pluginManager.registerEvents(GlobalEventListener(), this)

        server.scheduler.runTaskTimer(this, Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                afkList[player] = true
            }
        }, 20L, 20L * 60L)

    }

    override fun onDisable() {
        // Plugin shutdown logic
        playerProgressManager.shutdown()

    }
}
