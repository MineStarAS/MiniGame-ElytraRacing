package kr.kro.minestar.elytra.racing.funcions

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle

object UtilClass {
}

fun Location.colorParticle(color: Color, size: Float) = world.spawnParticle(Particle.REDSTONE, this, 1, 0.0, 0.0, 0.0, 0.0, Particle.DustOptions(color, size), true)