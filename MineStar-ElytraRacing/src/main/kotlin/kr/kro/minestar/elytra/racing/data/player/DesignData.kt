package kr.kro.minestar.elytra.racing.data.player

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.funcions.ItemClass
import kr.kro.minestar.elytra.racing.funcions.SoundClass
import kr.kro.minestar.elytra.racing.funcions.WorldClass
import kr.kro.minestar.utility.item.amount
import kr.kro.minestar.utility.item.isSameItem
import kr.kro.minestar.utility.location.*
import kr.kro.minestar.utility.location.Axis
import kr.kro.minestar.utility.string.toServer
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class DesignData(val player: Player) : Listener {

    companion object {
        private val map = hashMapOf<Player, DesignData>()
        internal fun getDesignData(player: Player) = map[player]
        internal fun addDesignData(player: Player, designData: DesignData) {
            map[player] = designData
        }

        internal val editToolHotBar = arrayOf(
            ItemClass.setStartLocation,
            ItemClass.setGoalLocation,
            ItemClass.setBoosterLocation,
            ItemClass.removeBoosterLocation,
            ItemClass.adjustOffset,
            ItemClass.toggleCenterAndUpward,
        )

        internal fun enableAllPlayer() {
            for (player in Bukkit.getOnlinePlayers()) if (player.isOp) DesignData(player)
        }
    }

    init {
        enable(pl)
        addDesignData(player, this)
    }

    /**
     * Angle
     */
    private var angle = 0
    private fun angle(): Int {
        if (angle in 0..18) return angle * 10
        return (angle - 36) * 10
    }

    private fun addAngle() {
        angle++
        if (angle >= 36) angle = 0
    }

    private fun subtractAngle() {
        angle--
        if (angle < 0) angle = 35
    }

    /**
     * Offset
     */
    private var offset = 8

    private fun addOffset(): Boolean {
        if (offset >= 15) return false
        offset++
        return true
    }

    private fun subtractOffset(): Boolean {
        if (offset <= 3) return false
        offset--
        return true
    }

    /**
     * Upward
     */
    private var upward = false

    private fun toggleUpward() {
        upward = !upward
    }

    /**
     * Center
     */
    private var center = true

    private fun toggleCenter() {
        center = !center
    }

    private fun Location.sort(): Location {
        if (upward) setAxis(Axis.PITCH, -90)
        else setAxis(Axis.PITCH, 0)
        setAxis(Axis.YAW, angle())
        return if (center) this.toCenterLocation()
        else toBlockLocation()
    }

    private fun Location.floorSort(): Location {
        setAxis(Axis.PITCH, 0)
        return if (center) this.toFloorCenter()
        else toBlockLocation()
    }

    /**
     * Event function
     */
    @EventHandler
    private fun addAngleEvent(e: PlayerDropItemEvent) {
        if (player != e.player) return
        if (!e.itemDrop.itemStack.isSameItem(ItemClass.setBoosterLocation)) return
        e.isCancelled = true
        addAngle()
        SoundClass.addSound.play(player)
        "§aAngle : ${angle()}˚".title()
    }

    @EventHandler
    private fun removeAngleEvent(e: PlayerSwapHandItemsEvent) {
        if (player != e.player) return
        if (!player.inventory.itemInMainHand.isSameItem(ItemClass.setBoosterLocation)) return
        e.isCancelled = true
        subtractAngle()
        SoundClass.subtractSound.play(player)
        "§cAngle : ${angle()}˚".title()
    }

    @EventHandler
    private fun useTool(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND) return
        if (player != e.player) return
        if (!player.isOp) return
        if (e.player.gameMode != GameMode.CREATIVE) return

        val designWorld = WorldClass.getDesignWorld(player.world)
        val item = e.item?.amount(1) ?: return
        val location = player.eyeLocation.offset(offset)

        when (item) {
            ItemClass.setStartLocation -> {
                if (e.action != Action.RIGHT_CLICK_AIR)
                    if (e.action != Action.RIGHT_CLICK_BLOCK) return
                if (designWorld == null) {
                    SoundClass.failSound.play(player)
                    return "§cthis world is not Design World".title()
                }
                designWorld.world.spawnLocation = location.floorSort()
                "§aSet Start Location".title()
                SoundClass.successSound.play(player)
            }
            ItemClass.setGoalLocation -> {
                if (e.action != Action.RIGHT_CLICK_AIR)
                    if (e.action != Action.RIGHT_CLICK_BLOCK) return
                if (designWorld == null) {
                    SoundClass.failSound.play(player)
                    return "§cthis world is not Design World".title()
                }
                designWorld.setGoalMark(location.floorSort())
                "§aSet Goal Mark".title()
                SoundClass.successSound.play(player)
            }
            ItemClass.setBoosterLocation -> {
                if (e.action != Action.RIGHT_CLICK_AIR)
                    if (e.action != Action.RIGHT_CLICK_BLOCK) return
                if (designWorld == null) {
                    SoundClass.failSound.play(player)
                    return "§cthis world is not Design World".title()
                }
                if (designWorld.addBoosterMark(location.sort())) {
                    "§aAdded Booster Mark".title()
                    SoundClass.successSound.play(player)
                } else {
                    "§cAnother booster is too close".title()
                    SoundClass.failSound.play(player)
                }
            }
            ItemClass.removeBoosterLocation -> {
                if (e.action != Action.RIGHT_CLICK_AIR)
                    if (e.action != Action.RIGHT_CLICK_BLOCK) return
                if (designWorld == null) {
                    SoundClass.failSound.play(player)
                    return "§cthis world is not Design World".title()
                }
                if (designWorld.removeBoosterMark(location)) {
                    "§aRemoved Booster Mark".title()
                    SoundClass.successSound.play(player)
                } else {
                    "§cThere are no booster nearby".title()
                    SoundClass.failSound.play(player)
                }
            }
            ItemClass.adjustOffset -> when (e.action) {
                Action.LEFT_CLICK_BLOCK,
                Action.LEFT_CLICK_AIR -> {
                    if (subtractOffset()) SoundClass.subtractSound.play(player)
                    else SoundClass.failSound.play(player)
                    "§c$offset".title()
                }
                Action.RIGHT_CLICK_BLOCK,
                Action.RIGHT_CLICK_AIR -> {
                    if (addOffset()) SoundClass.addSound.play(player)
                    else SoundClass.failSound.play(player)
                    "§a$offset".title()
                }
                Action.PHYSICAL -> {}
            }
            ItemClass.toggleCenterAndUpward -> when (e.action) {
                Action.LEFT_CLICK_BLOCK,
                Action.LEFT_CLICK_AIR -> {
                    toggleCenter()
                    if (center) {
                        "§eCenter Location".title()
                        SoundClass.addSound.play(player)
                    } else {
                        "§8Zero Location".title()
                        SoundClass.subtractSound.play(player)
                    }
                }
                Action.RIGHT_CLICK_BLOCK,
                Action.RIGHT_CLICK_AIR -> {
                    toggleUpward()
                    if (upward) {
                        "§aUpward ON".title()
                        SoundClass.addSound.play(player)
                    } else {
                        "§cUpward OFF".title()
                        SoundClass.subtractSound.play(player)
                    }
                }
                Action.PHYSICAL -> {}
            }

            else -> return
        }
        e.isCancelled = true
    }

    /**
     * Display function
     */
    private var task: BukkitTask? = null

    private fun taskRun() {
        taskCancel()
        var count = 0
        var b = true
        task = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            count++

            val item = player.inventory.itemInMainHand.clone().amount(1)

            val location = player.eyeLocation.offset(offset).sort()

            if (count == 10) {
                b = when (item) {
                    ItemClass.setBoosterLocation,
                    ItemClass.adjustOffset -> {
                        checkBoosterParticle(location)
                        true
                    }
                    ItemClass.setStartLocation,
                    ItemClass.setGoalLocation,
                    ItemClass.toggleCenterAndUpward,
                    ItemClass.removeBoosterLocation -> true
                    else -> false
                }
                count = 0
            }
            if (b) summonFallingBlock(location)
        }, 0, 1)
    }

    private fun taskCancel() {
        task?.cancel()
        task = null
    }


    private fun checkBoosterParticle(location: Location) {
        val maxCount = 36
        val radius = 3

        for (int in 0 until maxCount) {
            val loc = location.clone()
            val angle = 360.0 / maxCount * int

            if (location.pitch < -45) loc.setAxis(Axis.PITCH, 0).addAxis(Axis.YAW, angle).offset(radius)
            else loc.addAxis(Axis.YAW, 90).addAxis(Axis.PITCH, angle).offset(radius)

            val color = if (int % 2 == 1) Color.AQUA
            else Color.BLUE

            loc.colorParticle(color, 1F)
        }
    }

    private fun summonFallingBlock(location: Location) {
        val loc = location.clone().toFloorCenter()
        val block = loc.world.spawnFallingBlock(loc, Bukkit.createBlockData(Material.YELLOW_STAINED_GLASS))
        block.setGravity(false)
        block.dropItem = false
        block.ticksLived = 600 - 1

    }

    /**
     * Enable/Disable function
     */
    internal fun enable(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        taskRun()
    }

    internal fun disable() {
        HandlerList.unregisterAll(this)
        taskCancel()
    }

    /**
     * Utility function
     */
    private fun String.title() = player.sendTitle(" ", this, 5, 10, 5)
    private fun Location.colorParticle(color: Color, size: Float) =
        player.spawnParticle(
            Particle.REDSTONE, this,
            1, 0.0, 0.0, 0.0, 0.0,
            Particle.DustOptions(color, size)
        )

}