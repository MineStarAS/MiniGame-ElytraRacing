@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package kr.kro.minestar.elytra.racing.funcions

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.data.timer.worlds.DesignWorld
import kr.kro.minestar.elytra.racing.data.timer.worlds.RacingWorld
import org.apache.commons.io.FileUtils
import org.bukkit.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object WorldClass {
    init {
        if (!pl.dataFolder.exists()) pl.dataFolder.mkdir()
    }

    internal val worldFolder = File("${pl.dataFolder}/worldFolder").apply { if (!exists()) mkdir() }
    private fun serverFolder() = File(pl.dataFolder.absolutePath).parentFile!!.parentFile!!

    /**
     * Map function
     */
    private val designWorldMap = hashMapOf<World, DesignWorld>()
    internal fun getDesignWorld(world: World) = designWorldMap[world]
    internal fun addDesignWorld(designWorld: DesignWorld) {
        designWorldMap[designWorld.world] = designWorld
    }

    /**
     * World File function
     */
    internal fun worldList(): List<File> {
        val list = mutableListOf<File>()
        val folderList = worldFolder.listFiles() ?: return list.toList()
        for (folder in folderList) {
            if (!folder.isDirectory) continue
            if (!isWorldFolder(folder)) continue
            list.add(folder)
        }
        return list.toList()
    }

    internal fun worldFolder(world: World) = File(serverFolder(), world.name)

    /**
     * Enable function
     */
    internal fun enableDesignWorld(worldName: String): DesignWorld? {
        val worldFolder = File("$worldFolder/$worldName")

        if (Bukkit.getWorld(worldName) != null && designWorldMap.contains(Bukkit.getWorld(worldName)!!)) {
            return getDesignWorld(Bukkit.getWorld(worldName)!!)
        }

        if (!worldFolder.exists()) {
            val world = createWorld(worldName)
            world.save()
            worldFolder.mkdir()
            fileCopy(world.worldFolder, worldFolder)
            return DesignWorld(world)
        }

        val cloneFolder = File(serverFolder(), worldName)
        if (!cloneFolder.exists()) {
            cloneFolder.mkdir()
            if (!isWorldFolder(worldFolder)) return null
            File(worldFolder, "uid.dat").delete()
            fileCopy(worldFolder, cloneFolder)
        }

        val world = WorldCreator(worldName).createWorld() ?: return null
        return DesignWorld(world)
    }

    internal fun enableRacingWorld(worldName: String): RacingWorld? {
        if (isRacingNow()) return null
        val worldFolder = File("$worldFolder/$worldName")
        if (!isWorldFolder(worldFolder)) return null

        File(worldFolder, "uid.dat").delete()

        val racingWorldName = "racingWorld-${date()}"
        val cloneFolder = File(serverFolder(), racingWorldName).apply { mkdir() }

        fileCopy(worldFolder, cloneFolder)

        val world = WorldCreator(racingWorldName).createWorld() ?: return null
        return RacingWorld(world, worldName)
    }

    internal fun reloadEnable() {
        val serverFileNameList = serverFolder().list()!!
        for (world in worldList()) {
            val worldName = world.name
            if (!serverFileNameList.contains(worldName)) continue
            enableDesignWorld(worldName)
        }
    }

    internal fun deleteRacingWorlds() {
        val serverFileList = serverFolder().listFiles()
        for (file in serverFileList) {
            if (!file.isDirectory) continue
            if (!file.name.contains("racingWorld")) continue
            FileUtils.forceDelete(file)
        }
    }

    internal fun saveDesignWorlds() {
        val designWorlds = designWorldMap.values
        for (designWorld in designWorlds) designWorld.save()
    }

    /**
     * Racing function
     */
    private var racingWorld: RacingWorld? = null

    private fun isRacingNow() = racingWorld != null

    internal fun startRacing(racingWorld: RacingWorld) {
        if (!isRacingNow()) this.racingWorld = racingWorld
    }

    internal fun finishRacing(racingWorld: RacingWorld) {
        if (this.racingWorld == racingWorld) this.racingWorld = null
    }

    internal fun disableGaugeTask() {
        racingWorld?.gaugeDisplayCancel()
    }

    /**
     * Other function
     */
    private fun date(): String = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().time)

    private fun isWorldFolder(worldFolder: File): Boolean {
        if (!worldFolder.exists()) return false
        val checkFileList = listOf(
            "data", "entities", "poi",
            "region", "level.dat"
        )
        for (fileName in checkFileList) if (!File("$worldFolder/$fileName").exists()) return false
        return true
    }

    internal fun fileCopy(sourceFolder: File, targetFolder: File) {
        val fileList = sourceFolder.listFiles()
        for (file in fileList) {
            val temp = File(targetFolder.absolutePath + File.separator + file.name)
            if (file.isDirectory) {
                temp.mkdir()
                fileCopy(file, temp)
            } else {
                if (file.name == "session.lock") return
                val input = FileInputStream(file)
                val output = FileOutputStream(temp)
                try {
                    val b = ByteArray(4096)
                    var cnt: Int
                    while (input.read(b).also { cnt = it } != -1) {
                        output.write(b, 0, cnt)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        input.close()
                        output.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun createWorld(worldName: String): World {
        if (Bukkit.getWorld(worldName) != null) return Bukkit.getWorld(worldName)!!
        val wc = WorldCreator(worldName)
        wc.environment(World.Environment.NORMAL)
        wc.type(WorldType.FLAT)
        wc.generatorSettings(
            "{\"structures\": {\"structures\": {}}, \"layers\": [{\"block\": \"air\", \"height\": 1}], \"biome\":\"plains\"}"
        )
        wc.generateStructures(false)
        val world = wc.createWorld()!!
        worldSetting(world)
        return world
    }

    private fun worldSetting(world: World) {
        world.spawnLocation = Location(world, 0.0, 60.0, 0.0)
        world.difficulty = Difficulty.PEACEFUL
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false)
        world.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, false)
        world.setGameRule(GameRule.DISABLE_RAIDS, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false)
        world.setGameRule(GameRule.DO_FIRE_TICK, false)
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
        world.setGameRule(GameRule.DO_INSOMNIA, false)
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, false)
        world.setGameRule(GameRule.DO_MOB_LOOT, true)
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false)
        world.setGameRule(GameRule.DO_TILE_DROPS, true)
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.DROWNING_DAMAGE, false)
        world.setGameRule(GameRule.FALL_DAMAGE, false)
        world.setGameRule(GameRule.FIRE_DAMAGE, false)
        world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, false)
        world.setGameRule(GameRule.FREEZE_DAMAGE, false)
        world.setGameRule(GameRule.KEEP_INVENTORY, true)
        world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false)
        world.setGameRule(GameRule.MAX_COMMAND_CHAIN_LENGTH, 65536)
        world.setGameRule(GameRule.MAX_ENTITY_CRAMMING, 24)
        world.setGameRule(GameRule.MOB_GRIEFING, false)
        world.setGameRule(GameRule.NATURAL_REGENERATION, false)
        world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 100)
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3)
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, false)
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true)
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
        world.setGameRule(GameRule.SPAWN_RADIUS, 0)
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, true)
        world.setGameRule(GameRule.UNIVERSAL_ANGER, false)
        world.time = 6000
    }

    internal fun convertUnicode(kor: String): String {
        val result = StringBuffer()
        for (i in kor.indices) {
            val cd = kor.codePointAt(i)
            if (cd < 128) {
                result.append(String.format("%c", cd))
            } else {
                result.append(String.format("-u%04x", cd))
            }
        }
        return result.toString()
    }

    internal fun readUnicode(unicode: String): String {
        val result = StringBuffer()
        var i = 0
        while (i < unicode.length) {
            if (unicode[i] == '-' && unicode[i + 1] == 'u') {
                val c = unicode.substring(i + 2, i + 6).toInt(16).toChar()
                result.append(c)
                i += 5
            } else {
                result.append(unicode[i])
            }
            i++
        }
        return result.toString()
    }
}
