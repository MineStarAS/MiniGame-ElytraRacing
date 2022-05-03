@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package kr.kro.minestar.elytra.racing.funcions

import kr.kro.minestar.elytra.racing.Main.Companion.pl
import kr.kro.minestar.elytra.racing.data.worlds.DesignWorld
import kr.kro.minestar.elytra.racing.data.worlds.RaceWorld
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object WorldClass {
    internal val folder = File("${pl.dataFolder}/worldFolder").apply { if (!exists()) mkdir() }
    private fun serverFolder() = File(pl.dataFolder.absolutePath).parentFile!!.parentFile!!

    fun worldFolder(world: World) = File(serverFolder(), world.name)

    fun enableRaceWorld(worldName: String): RaceWorld? {
        val worldFolder = File("$folder/$worldName")
        if (!isWorldFolder(worldFolder)) return null

        File(worldFolder, "uid.dat").delete()

        val date = date()
        val cloneFolder = File(serverFolder(), date).apply { mkdir() }

        fileCopy(worldFolder, cloneFolder)

        val world = WorldCreator(date).createWorld() ?: return null
        return RaceWorld(world)
    }

    fun enableDesignWorld(worldName: String): DesignWorld? {
        val worldFolder = File("$folder/$worldName")
        val cloneFolder = File(serverFolder(), worldName)
        if (!cloneFolder.exists()) {
            if (!isWorldFolder(worldFolder)) return null
            File(worldFolder, "uid.dat").delete()
            fileCopy(worldFolder, cloneFolder)
        }

        val world = WorldCreator(worldName).createWorld() ?: return null
        return DesignWorld(world)
    }

    private fun date(): String = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().time)

    private fun isWorldFolder(worldFolder: File): Boolean {
        if (!worldFolder.exists()) return false
        val checkFileList = listOf(
            "advancements", "data", "datapacks",
            "entities", "playerdata", "poi",
            "region", "stats", "level.dat",
            "level.dat_old", "worldData.yml",
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
}