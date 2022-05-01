package kr.kro.minestar.elytra.racing.data.locations

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.funcions.colorParticle
import kr.kro.minestar.utility.location.Axis
import kr.kro.minestar.utility.location.addAxis
import kr.kro.minestar.utility.location.offset
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitTask

class StartLocation(location: Location): LocationData(location) {

    init {
        enable()
    }

    override fun enable() {
        disable()
        val amount1 = 36
        task = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            for (int in 0 until amount1) {

                val angle = 360.0 / amount1 * int
                val loc = location.clone()

                loc.addAxis(Axis.YAW, angle)
                loc.offset(4).colorParticle(Color.AQUA, 4F)
                loc.offset(1).colorParticle(Color.AQUA, 4F)
            }

            val amount2 = 18
            for (int in 0 until amount2) {

                val angle = 360.0 / amount1 * int
                val loc = location.clone().addAxis(Axis.Y, 0.1)

                loc.addAxis(Axis.YAW, angle)
                loc.offset(2).colorParticle(Color.YELLOW, 4F)
                loc.offset(1).colorParticle(Color.YELLOW, 4F)
            }
        }, 0, 10)
    }
}