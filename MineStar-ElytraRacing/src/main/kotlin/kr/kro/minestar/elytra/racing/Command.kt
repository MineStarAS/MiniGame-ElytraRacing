package kr.kro.minestar.elytra.racing

import kr.kro.minestar.elytra.racing.Main.Companion.prefix
import kr.kro.minestar.elytra.racing.data.player.DesignData
import kr.kro.minestar.elytra.racing.data.worlds.DesignWorld
import kr.kro.minestar.elytra.racing.funcions.ItemClass
import kr.kro.minestar.elytra.racing.funcions.WorldClass
import kr.kro.minestar.utility.command.Argument
import kr.kro.minestar.utility.command.FunctionalCommand
import kr.kro.minestar.utility.string.removeUnderBar
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.unit.setFalse
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

object Command : FunctionalCommand {
    private enum class Arg(override val howToUse: String) : Argument {
    }

    private enum class OpArg(override val howToUse: String) : Argument {
        help(""),
        racing("<WorldName>"),
        world("[create/open/save] <WorldName>"),
        tool(""),
        unicode("<Korean>")
    }

    var test: DesignWorld? = null

    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false

        if (args.isEmpty()) {
//            MenuGUI(player)
            printHowToUse(OpArg.values(), player, label, prefix)
            return false
        }

        val arg = argument(Arg.values(), args) ?: if (player.isOp) argument(OpArg.values(), args) else return false

        if (arg?.isValid(args) == false)
            return "$prefix §c${arg.howToUse(label)}".toPlayer(player).setFalse()

        when (arg) {
            OpArg.help -> printHowToUse(OpArg.values(), player, label, prefix)

            OpArg.racing -> {
                val worldName = WorldClass.convertUnicode(args.last())
                WorldClass.enableRacingWorld(worldName) ?: "$prefix §c존재하지 않는 월드이거나 레이싱이 진행 중입니다.".toPlayer(player)
            }
            OpArg.world -> {
                when (args[1]) {
                    "open", "create" -> {
                        val worldName = WorldClass.convertUnicode(args.last())
                        val designWorld = WorldClass.enableDesignWorld(worldName)
                            ?: return "$prefix §c월드 생성에 실패 하였습니다.".toPlayer(player).setFalse()
                        test = designWorld
                        player.gameMode = GameMode.CREATIVE
                        player.isFlying = true
                        player.teleport(designWorld.world.spawnLocation)
                        if (args[1] == "create") "$prefix §e${args.last().removeUnderBar()} §a월드를 생성 하였습니다.".toPlayer(player)
                        else "$prefix §e${args.last().removeUnderBar()} §a월드를 열었습니다.".toPlayer(player)
                        "[§a월드 코드§f] §e$worldName".toPlayer(player)
                    }
                    "save" -> {
                        val worldName = WorldClass.convertUnicode(args.last())
                        val world = Bukkit.getWorld(worldName)
                            ?: return "$prefix §c활성화 되어 있지 않거나 존재하지 않는 월드입니다.".toPlayer(player).setFalse()
                        val designWorld = WorldClass.getDesignWorld(world)
                            ?: return "$prefix §c해당 월드는 디자인월드가 아닙니다.".toPlayer(player).setFalse()
                        designWorld.save()
                        "$prefix §e${args.last().removeUnderBar()} §a을/를 저장하였습니다.".toPlayer(player).setFalse()
                        "[§a월드 코드§f] §e$worldName".toPlayer(player)
                    }
                    else -> return "$prefix §c${arg.howToUse(label)}".toPlayer(player).setFalse()
                }
            }
            OpArg.tool -> {
                val inventory = player.inventory
                inventory.setItem(0, ItemClass.worldEditWand)
                inventory.setItem(1, ItemClass.worldEditCompass)
                inventory.setItem(2, ItemClass.redStoneBlock)
                for ((int, item) in DesignData.editToolHotBar.withIndex()) {
                    val offset = if (int <= 2) 3
                    else 4
                    inventory.setItem(int + offset, item)
                }
            }
            OpArg.unicode -> {
                val korean = args.last()
                val unicode = WorldClass.convertUnicode(korean)

                val copyEvent = TextComponent("[§eClick to copy§f]")
                copyEvent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(unicode))
                copyEvent.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, unicode)

                prefix.toPlayer(player)
                "§aKoren §f: §e$korean".toPlayer(player)
                "§bUnicode §f: §e$unicode".toPlayer(player)
                player.spigot().sendMessage(copyEvent)
            }
        }
        return false
    }

    override fun onTabComplete(player: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String> {
        if (player !is Player) return listOf()

        val list = mutableListOf<String>()

        val arg = argument(Arg.values(), args) ?: if (player.isOp) argument(OpArg.values(), args) else null
        val lastIndex = args.lastIndex
        val last = args.lastOrNull() ?: ""

        fun List<String>.add() {
            for (s in this) if (s.contains(last)) list.add(s)
        }

        fun Array<out Argument>.add() {
            for (s in this) if (s.name.contains(last)) list.add(s.name)
        }

        fun List<File>.add() {
            for (s in this) if (WorldClass.readUnicode(s.name).contains(last)) list.add(WorldClass.readUnicode(s.name))
        }

        fun playerAdd() {
            for (s in Bukkit.getOnlinePlayers()) if (s.name.contains(last)) list.add(s.name)
        }

        if (arg == null) {
            Arg.values().add()
            if (player.isOp) OpArg.values().add()
        } else when (arg) {
            OpArg.racing -> when (lastIndex) {
                1 -> WorldClass.worldList().add()
            }
            OpArg.world -> when (lastIndex) {
                1 -> arg.argList(lastIndex).add()
                2 -> {
                    if (args[1] == "save") WorldClass.worldList().add()
                    if (args[1] == "open") WorldClass.worldList().add()
                }
            }
        }
        return list
    }
}