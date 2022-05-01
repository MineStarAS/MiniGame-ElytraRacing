package kr.kro.minestar.elytra.racing

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var pl: Main
        const val prefix = "§f[§9PLUGIN§f]"
    }

    override fun onEnable() {
        pl = this
        logger.info("$prefix §aEnable")
        getCommand("er")?.setExecutor(Command)
    }

    override fun onDisable() {
    }
}