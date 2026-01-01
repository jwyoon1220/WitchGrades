package io.github.jwyoon1220.witchGrades.db

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class DatabaseQueue {

    private val executor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "WitchGrades-DB").apply { isDaemon = true }
    }

    fun submit(task: () -> Unit) {
        executor.submit {
            try {
                task()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun shutdown() {
        executor.shutdown()
    }
}

