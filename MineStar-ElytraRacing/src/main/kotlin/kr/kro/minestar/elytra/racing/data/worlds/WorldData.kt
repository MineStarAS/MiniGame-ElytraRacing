package kr.kro.minestar.elytra.racing.data.worlds

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.funcions.ItemClass
import kr.kro.minestar.elytra.racing.funcions.SoundClass
import kr.kro.minestar.elytra.racing.funcions.WorldClass
import kr.kro.minestar.utility.event.enable
import kr.kro.minestar.utility.inventory.hasSameItem
import kr.kro.minestar.utility.location.*
import kr.kro.minestar.utility.location.Axis
import org.bukkit.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.scheduler.BukkitTask
import java.io.File

@Suppress("DEPRECATION")
abstract class WorldData(internal val world: World) : Listener {
    protected var folder = File("${WorldClass.worldFolder}/${world.name}")

    protected fun startLocation() = world.spawnLocation.clone()

    private val boostDelaySet = hashSetOf<Player>()
    private val previousBoostMap = hashMapOf<Player, ArmorStand>()

    var particleTask: BukkitTask? = null

    protected fun init() {
        enable(pl)
        locationParticle()
    }


    /**
     * Event function
     */
    protected open fun playerMove(e: PlayerMoveEvent) {
        if (e.player.world != world) return
        if (e.player.gameMode != GameMode.ADVENTURE) return

        val player = e.player
        val location = player.location

        fun giveBooster(mark: ArmorStand) {
            if (this is RacingWorld) if (!isStarted()) return
            if (player.inventory.hasSameItem(ItemClass.fireworkRocket)) return
            if (boostDelaySet.contains(player)) return
            if (previousBoostMap[player] == mark) return

            boostDelaySet.add(player)
            Bukkit.getScheduler().runTaskLater(pl, Runnable { boostDelaySet.remove(player) }, 10)

            previousBoostMap[player] = mark
            player.inventory.setItemInOffHand(ItemClass.fireworkRocket)
            SoundClass.giveBoosterSound.play(player)
        }

        fun goalIn() {
            when (this) {
                is DesignWorld -> {
                }
                is RacingWorld -> {
                    goalInPlayer(player)
                }
                else -> return
            }
        }

        when (true) {
            player.isGliding -> {
                val nearMarks = nearMarks(location, 3.0)
                if (nearMarks.isEmpty()) return
                for (mark in nearMarks) {
                    if (isBoosterMark(mark)) return giveBooster(mark)
                    else continue
                }
            }
            player.isOnGround -> {
                for (mark in nearMarks(location, 5.0)) if (isGoalMark(mark)) return goalIn()
                else continue

                if (location.distance(startLocation()) < 5.0) return

                teleportToStartLocation(player)
                inventorySet(player)
                if (player.isInWater) SoundClass.playerHurtInWater.play(player)
                else SoundClass.playerHurt.play(player)
            }
            player.isVisualFire -> {
                teleportToStartLocation(player)
                inventorySet(player)
                SoundClass.playerHurtOnFire.play(player)
            }
            else -> return
        }
    }

    protected open fun damageFixed(e: EntityDamageEvent) {
        if (e.entity !is Player) return
        val player = e.entity as Player
        if (player.world != world) return
        e.damage = 1.0

        if (e.cause == EntityDamageEvent.DamageCause.VOID) {
            teleportToStartLocation(player)
            inventorySet(player)
        }
    }

    /**
     * Particle function
     */
    private fun locationParticle() {
        particleTask?.cancel()
        particleTask = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            if (world.playerCount == 0) return@Runnable

            startLocationParticle()

            for (mark in marks()) {
                val name = mark.customName ?: continue
                val location = mark.location
                when (name) {
                    "goal" -> goalLocationParticle(location)
                    "booster" -> boosterLocationParticle(location)
                    else -> continue
                }
            }
        }, 0, 10)
    }

    private fun startLocationParticle() {
        val location = startLocation().toFloorCenter()
        val amount1 = 36
        val amount2 = 18
        val radius = 5

        for (int in 0 until amount1) {
            val angle = 360.0 / amount1 * int
            val loc = location.clone()

            loc.addAxis(Axis.YAW, angle)
            loc.clone().offset(radius).colorParticle(Color.AQUA, 4F)
            loc.clone().offset(radius - 1).colorParticle(Color.AQUA, 4F)
        }
        for (int in 0 until amount2) {
            val angle = 360.0 / amount2 * int
            val loc = location.clone().addAxis(Axis.Y, 0.1)

            loc.addAxis(Axis.YAW, angle)
            loc.clone().offset(radius - 2).colorParticle(Color.YELLOW, 4F)
            loc.clone().offset(radius - 3).colorParticle(Color.YELLOW, 4F)
        }
    }

    private fun goalLocationParticle(location: Location) {
        val amount1 = 36
        val amount2 = 18
        val radius = 5

        for (int in 0 until amount1) {
            val angle = 360.0 / amount1 * int
            val loc = location.clone()

            loc.addAxis(Axis.YAW, angle)
            loc.clone().offset(radius).colorParticle(Color.AQUA, 4F)
            loc.clone().offset(radius - 1).colorParticle(Color.AQUA, 4F)
        }
        for (int in 0 until amount2) {
            val angle = 360.0 / amount2 * int
            val loc = location.clone().addAxis(Axis.Y, 0.1)

            loc.addAxis(Axis.YAW, angle)
            loc.clone().offset(radius - 2).colorParticle(Color.LIME, 4F)
            loc.clone().offset(radius - 3).colorParticle(Color.LIME, 4F)
        }
    }

    private fun boosterLocationParticle(location: Location) {
        val maxCount = 36
        val radius = 3

        for (int in 0 until maxCount) {
            val loc = location.clone()
            val angle = 360.0 / maxCount * int

            if (location.pitch < -45) loc.setAxis(Axis.PITCH, 0).addAxis(Axis.YAW, angle).offset(radius)
            else loc.addAxis(Axis.YAW, 90).addAxis(Axis.PITCH, angle).offset(radius)

            val color = if (int % 2 == 1) Color.YELLOW
            else Color.ORANGE

            loc.colorParticle(color, 3F)
        }
    }

    private fun Location.colorParticle(color: Color, size: Float) = world.spawnParticle(Particle.REDSTONE, this, 1, 0.0, 0.0, 0.0, 0.0, Particle.DustOptions(color, size), true)

    /**
     * Marks function
     */
    protected fun marks(): MutableCollection<ArmorStand> = world.getEntitiesByClass(ArmorStand::class.java)

    protected fun nearMarks(location: Location, radius: Double): MutableCollection<ArmorStand> = location.getNearbyEntitiesByType(ArmorStand::class.java, radius)

    /**
     * Is Function
     */
    protected fun isGoalMark(mark: ArmorStand): Boolean {
        val name = mark.customName ?: return false
        if (name != "goal") return false
        return true
    }

    protected fun isBoosterMark(mark: ArmorStand): Boolean {
        val name = mark.customName ?: return false
        if (name != "booster") return false
        return true
    }

    /**
     * Other Function
     */
    fun teleportToStartLocation(player: Player) {
        player.teleport(startLocation())
        player.health = player.maxHealth
        player.gameMode = GameMode.ADVENTURE
        previousBoostMap.remove(player)
    }

    protected fun inventoryClear(player: Player) {
        val inventory = player.inventory
        inventory.clear()
    }

    protected fun inventorySet(player: Player) {
        if (player.gameMode == GameMode.SPECTATOR) return
        val inventory = player.inventory
        inventory.clear()
        if (this is RacingWorld) if (!isStarted()) return
        inventory.chestplate = ItemClass.elytra
        inventory.setItemInOffHand(ItemClass.fireworkRocket)
    }
}