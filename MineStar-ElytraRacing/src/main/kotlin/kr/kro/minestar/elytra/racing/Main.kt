package kr.kro.minestar.elytra.racing

import kr.kro.minestar.elytra.racing.data.player.DesignData
import kr.kro.minestar.elytra.racing.funcions.AlwaysEvent
import kr.kro.minestar.elytra.racing.funcions.TestClass
import kr.kro.minestar.elytra.racing.funcions.WorldClass
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    companion object {
        lateinit var pl: Main
        const val prefix = "§f[§9ElytraRacing§f]"
    }

    override fun onEnable() {
        pl = this
        logger.info("$prefix §aEnable")
        getCommand("er")?.setExecutor(Command)

        WorldClass.reloadEnable()

        if (!isReload()) {
            createReloadCheckFile()
            WorldClass.deleteRacingWorlds()
        }

        DesignData.enableAllPlayer()

        AlwaysEvent
        TestClass
    }

    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) {
            try {
                player.closeInventory()
            } catch (_: Exception) {
            }
        }
        WorldClass.disableGaugeTask()
        WorldClass.saveDesignWorlds()
    }

    /**
     * Check reload function
     */
    private fun reloadCheckFile() = File(dataFolder, "on")

    private fun createReloadCheckFile() {
        val file = reloadCheckFile()
        if (file.exists()) return
        file.createNewFile()
        file.deleteOnExit()
    }

    private fun isReload() = reloadCheckFile().exists()
}