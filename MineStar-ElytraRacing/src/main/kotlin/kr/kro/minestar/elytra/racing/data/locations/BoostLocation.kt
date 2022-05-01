package kr.kro.minestar.elytra.racing.data.locations

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.funcions.colorParticle
import kr.kro.minestar.utility.location.Axis
import kr.kro.minestar.utility.location.addAxis
import kr.kro.minestar.utility.location.offset
import kr.kro.minestar.utility.location.setAxis
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location

class BoostLocation(location: Location) : LocationData(location) {

    init {
        enable()
    }

    override fun enable() {
        disable()
        val maxCount = 36
        val radius = 3
        task = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            for (int in 0 until maxCount) {
                val loc = location.clone()
                val angle = 360.0 / maxCount * int

                if (location.pitch < -45) loc.setAxis(Axis.PITCH, 0).addAxis(Axis.YAW, angle).offset(radius)
                else loc.addAxis(Axis.YAW, 90).addAxis(Axis.PITCH, angle).offset(radius)

                val color = if (int % 2 == 1) Color.YELLOW
                else Color.ORANGE

                loc.colorParticle(color, 3F)
            }
        }, 0, 10)
    }
}