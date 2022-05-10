package kr.kro.minestar.elytra.racing.data.worlds

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.data.bossbar.SpeedGauge
import kr.kro.minestar.elytra.racing.data.timer.GameTimer
import kr.kro.minestar.elytra.racing.data.timer.Timestamp
import kr.kro.minestar.elytra.racing.funcions.SoundClass
import kr.kro.minestar.elytra.racing.funcions.WorldClass
import kr.kro.minestar.utility.event.disable
import kr.kro.minestar.utility.scheduler.Scheduler
import kr.kro.minestar.utility.scheduler.objectes.later.RunLater
import kr.kro.minestar.utility.scheduler.objectes.later.RunTitle
import kr.kro.minestar.utility.scheduler.objectes.now.RunNow
import kr.kro.minestar.utility.sound.Scale
import kr.kro.minestar.utility.string.removeUnderBar
import kr.kro.minestar.utility.string.toPlayers
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.scheduler.BukkitTask

class RacingWorld(world: World, private val worldName: String) : WorldData(world) {
    init {
        init()
        WorldClass.startRacing(this)

        racingReady()
    }

    private fun allPlayers() = Bukkit.getOnlinePlayers()
    private fun worldPlayers(): List<Player> = world.players

    private var started = false
    internal fun isStarted() = started

    /**
     * Ranking function
     */
    private val rankingMap = hashMapOf<Player, Timestamp>()

    internal fun goalInPlayer(player: Player) {
        player.gameMode = GameMode.SPECTATOR
        inventoryClear(player)
        if (rankingMap.isEmpty()) firstGoalIn(player)
        if (rankingMap.contains(player)) return
        val stamp = gameTimer.createTimestamp()
        rankingMap[player] = stamp
        SoundClass.goalInSound(worldPlayers())
        player.stopSound(Sound.MUSIC_DISC_PIGSTEP, SoundCategory.RECORDS)
        " §d[ ${rankingMap.size}위 ] §e${player.name} §b[ $stamp ]".toPlayers(worldPlayers())

        if (worldPlayers().size == rankingMap.size) return finishing()
        for (p in worldPlayers()) if (p.gameMode != GameMode.SPECTATOR) return

        return finishing()
    }

    /**
     * Event function
     */
    @EventHandler
    override fun playerMove(e: PlayerMoveEvent) {
        super.playerMove(e)
    }

    @EventHandler
    fun dropItem(e: PlayerDropItemEvent) {
        if (e.player.world != world) return
        e.isCancelled = true
    }

    @EventHandler
    fun attackEntity(e: EntityDamageByEntityEvent) {
        if (e.damager.world != world) return
        e.isCancelled = true
    }

    @EventHandler
    override fun damageFixed(e: EntityDamageEvent) {
        super.damageFixed(e)
    }

    /**
    Game function
     */
    private fun racingReady() {
        for (player in allPlayers()) {
            teleportToStartLocation(player)
            inventoryClear(player)
        }

        val scheduler = Scheduler(pl)
        scheduler.addRun(RunLater({ SoundClass.enterWorld(worldPlayers()) }, 20 * 3))
        scheduler.addRun(RunTitle(worldPlayers(), "§9Racing World", "§e${WorldClass.readUnicode(worldName).removeUnderBar()}", 5, 40, 5, 20))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§e잠시 후 레이싱이 시작합니다", 5, 40, 5, 20))
        scheduler.addRun(RunNow { SoundClass.countDown.clone().setScale(Scale.DO).play(worldPlayers()) })
        scheduler.addRun(RunTitle(worldPlayers(), "§a5", " ", 0, 20, 0, 0))
        scheduler.addRun(RunNow { SoundClass.countDown.clone().setScale(Scale.RE).play(worldPlayers()) })
        scheduler.addRun(RunTitle(worldPlayers(), "§a4", " ", 0, 20, 0, 0))
        scheduler.addRun(RunNow { SoundClass.countDown.clone().setScale(Scale.MI).play(worldPlayers()) })
        scheduler.addRun(RunTitle(worldPlayers(), "§e3", " ", 0, 20, 0, 0))
        scheduler.addRun(RunNow { SoundClass.countDown.clone().setScale(Scale.FA).play(worldPlayers()) })
        scheduler.addRun(RunTitle(worldPlayers(), "§e2", " ", 0, 20, 0, 0))
        scheduler.addRun(RunNow { SoundClass.countDown.clone().setScale(Scale.SOL).play(worldPlayers()) })
        scheduler.addRun(RunTitle(worldPlayers(), "§c1", " ", 0, 20, 0, 0))
        scheduler.addRun(RunNow {
            gameTimer.start()
            started = true
            for (player in worldPlayers()) inventorySet(player)
            gaugeDisplay()
            SoundClass.start(worldPlayers())
        })
        scheduler.addRun(RunTitle(worldPlayers(), "§6START", " ", 0, 20, 0, 0))
        scheduler.play()
    }

    private var firstGoalIn: Scheduler? = null

    private fun firstGoalIn(player: Player) {
        val scheduler = Scheduler(pl)
        scheduler.addRun(RunTitle(worldPlayers(), "§o§n§a1ST", "§e${player.name}", 5, 30, 5, 0))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§a30 초 후 레이싱이 끝납니다", 5, 30, 5, 0))
        scheduler.addRun(RunNow { SoundClass.finishingMusic(worldPlayers()) })
        scheduler.addRun(RunLater({}, 20 * 8))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§e남은시간 20 초", 5, 10, 5, 0))
        scheduler.addRun(RunLater({}, 20 * 9))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§c남은시간 10 초", 5, 10, 5, 0))
        scheduler.addRun(RunLater({}, 20 * 4))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§c5", 0, 20, 0, 0))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§c4", 0, 20, 0, 0))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§c3", 0, 20, 0, 0))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§c2", 0, 20, 0, 0))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§c1", 0, 20, 0, 0))
        scheduler.addRun(RunNow { finishing() })

        scheduler.play()
        firstGoalIn = scheduler
    }

    private fun finishing() {
        firstGoalIn?.stop()
        val scheduler = Scheduler(pl)
        scheduler.addRun(RunNow {
            gameTimer.finish()
            gaugeDisplayCancel()
            for (p in worldPlayers()) {
                p.gameMode = GameMode.SPECTATOR
                p.stopSound(Sound.MUSIC_DISC_PIGSTEP, SoundCategory.RECORDS)
                SoundClass.goalInSound(worldPlayers())
            }
        })
        scheduler.addRun(RunTitle(allPlayers(), "§o§n§dFINISH!", " ", 0, 60, 5, 0))
        scheduler.addRun(RunTitle(worldPlayers(), " ", "§a잠시 후 오버월드로 이동합니다", 5, 50, 5, 20 * 3))
        scheduler.addRun(RunNow {
            val world = Bukkit.getWorlds().first()
            for (player in worldPlayers()) {
                player.gameMode = GameMode.ADVENTURE
                player.teleport(world.spawnLocation)
            }
            disable()
            WorldClass.finishRacing(this)
        })
        scheduler.play()
    }

    /**
     * Timer function
     */
    private var gameTimer = GameTimer()
    private var gaugeTask: BukkitTask? = null

    private val gaugeMap = hashMapOf<Player, SpeedGauge>()

    private fun gaugeDisplay() {
        gaugeDisplayCancel()
        gaugeTask = Bukkit.getScheduler().runTaskTimer(pl, Runnable {
            for (player in worldPlayers()) {
                if (!gaugeMap.contains(player)) gaugeMap[player] = SpeedGauge(player)
                val gauge = gaugeMap[player]!!
                gauge.display(gameTimer)
            }
        }, 0, 1)
    }

    internal fun gaugeDisplayCancel() {
        gaugeTask?.cancel()
        gaugeTask = null
        for (gauge in gaugeMap.values) gauge.disable()
        gaugeMap.clear()
    }
}