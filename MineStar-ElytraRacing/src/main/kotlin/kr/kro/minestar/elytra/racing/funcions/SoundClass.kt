package kr.kro.minestar.elytra.racing.funcions

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.utility.location.Axis
import kr.kro.minestar.utility.location.addAxis
import kr.kro.minestar.utility.scheduler.Scheduler
import kr.kro.minestar.utility.scheduler.objectes.later.RunLater
import kr.kro.minestar.utility.scheduler.objectes.now.RunNow
import kr.kro.minestar.utility.sound.PlaySound
import kr.kro.minestar.utility.sound.Scale
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

object SoundClass {
    /**
     * Design Sound
     */
    internal val addSound = PlaySound().apply {
        soundCategory = SoundCategory.RECORDS
        sound = Sound.BLOCK_LEVER_CLICK
    }

    internal val subtractSound = PlaySound().apply {
        soundCategory = SoundCategory.RECORDS
        sound = Sound.BLOCK_LEVER_CLICK
        pitch = 0.8F
    }

    internal val successSound = PlaySound().apply {
        soundCategory = SoundCategory.RECORDS
        sound = Sound.ENTITY_PLAYER_LEVELUP
        pitch = 2.0F
        volume = 0.5F
    }


    internal val failSound = PlaySound().apply {
        soundCategory = SoundCategory.RECORDS
        sound = Sound.ENTITY_PLAYER_ATTACK_CRIT
        volume = 0.5F
    }

    /**
     * Racing Sound
     */
    internal val giveBoosterSound = PlaySound().apply {
        soundCategory = SoundCategory.RECORDS
        sound = Sound.ENTITY_PLAYER_LEVELUP
        pitch = 1.5F
    }

    internal val playerHurt = PlaySound().apply {
        soundCategory = SoundCategory.PLAYERS
        sound = Sound.ENTITY_PLAYER_HURT
    }


    internal val playerHurtInWater = PlaySound().apply {
        soundCategory = SoundCategory.PLAYERS
        sound = Sound.ENTITY_PLAYER_HURT_DROWN
    }

    internal val playerHurtOnFire = PlaySound().apply {
        soundCategory = SoundCategory.PLAYERS
        sound = Sound.ENTITY_PLAYER_HURT_ON_FIRE
    }

    internal val countDown = PlaySound().apply {
        soundCategory = SoundCategory.RECORDS
        sound = Sound.BLOCK_NOTE_BLOCK_BIT
        setScale(Scale.DO)
    }

    internal fun start(players: Collection<Player>) {
        val scheduler = Scheduler(pl)
        val interval = 2L
        val sound = PlaySound().apply {
            soundCategory = SoundCategory.RECORDS
            sound = Sound.BLOCK_NOTE_BLOCK_BIT
        }
        scheduler.addRun(RunNow { sound.setScale(Scale.LA).play(players) })
        scheduler.addRun(RunLater({ sound.setScale(Scale.TI).play(players) }, interval))
        scheduler.addRun(RunLater({ sound.setScale(Scale.H_RE).play(players) }, interval))
        scheduler.play()
    }

    internal fun enterWorld(players: Collection<Player>) {
        val scheduler = Scheduler(pl)
        val interval = 4L
        val sound = PlaySound().apply {
            soundCategory = SoundCategory.RECORDS
            sound = Sound.BLOCK_NOTE_BLOCK_BIT
        }
        scheduler.addRun(RunNow { sound.setScale(Scale.DO).play(players) })
        scheduler.addRun(RunLater({ sound.setScale(Scale.MI).play(players) }, interval))
        scheduler.addRun(RunLater({ sound.setScale(Scale.DO).play(players) }, interval))
        scheduler.addRun(RunLater({ sound.setScale(Scale.MI).play(players) }, interval))
        scheduler.addRun(RunLater({ sound.setScale(Scale.SOL).play(players) }, interval))
        scheduler.addRun(RunLater({ sound.setScale(Scale.H_DO).play(players) }, interval))
        scheduler.play()
    }

    internal fun goalInSound(players: Collection<Player>) {
        val scheduler = Scheduler(pl)
        val interval = 4L
        val sound = PlaySound().apply {
            soundCategory = SoundCategory.RECORDS
            sound = Sound.BLOCK_NOTE_BLOCK_BIT
        }
        scheduler.addRun(RunNow { sound.setScale(Scale.DO).play(players) })
        scheduler.addRun(RunLater({ sound.setScale(Scale.MI).play(players) }, interval))
        scheduler.addRun(RunLater({ sound.setScale(Scale.DO).play(players) }, interval))
        scheduler.addRun(RunLater({ sound.setScale(Scale.SOL).play(players) }, interval))
        scheduler.play()
    }

    internal fun finishingMusic(players: Collection<Player>) {
        val sound = PlaySound().apply {
            soundCategory = SoundCategory.RECORDS
            sound = Sound.MUSIC_DISC_PIGSTEP
            volume = 100F
            pitch = 1.5F
        }
        var b = false
        for (player in players) if (!b) b = true else sound.play(player, player.location.addAxis(Axis.Y, 500))
    }
}