package kr.kro.minestar.elytra.racing.data.locations

import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask

abstract class LocationData(location: Location) {

    val location = location.toCenterLocation()
        get() = field.clone()

    var task: BukkitTask? = null

    protected open fun enable() {}

    fun disable() {
        task?.cancel()
        task = null
    }
}