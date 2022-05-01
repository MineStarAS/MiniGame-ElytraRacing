package kr.kro.minestar.elytra.racing

import kr.kro.minestar.elytra.racing.Main.Companion.prefix
import kr.kro.minestar.elytra.racing.data.locations.BoostLocation
import kr.kro.minestar.utility.command.Argument
import kr.kro.minestar.utility.command.FunctionalCommand
import kr.kro.minestar.utility.location.Axis
import kr.kro.minestar.utility.location.addAxis
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.string.toServer
import kr.kro.minestar.utility.unit.setFalse
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Command : FunctionalCommand {
    private enum class Arg(override val howToUse: String) : Argument {
        cmd1(""),
        cmd2(""),
        cmd3(""),
    }

    private enum class OpArg(override val howToUse: String) : Argument {
        test(""),
    }

    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false

        if (args.isEmpty()) return "$prefix $label".toPlayer(player).setFalse()

        val arg = argument(Arg.values(), args) ?: if (player.isOp) argument(OpArg.values(), args) else return false

        when (arg) {
            Arg.cmd1 -> {}
            Arg.cmd2 -> {
                val loc = player.location
                " ".toServer()
                "${loc.yaw}".toServer()
                "${loc.pitch}".toServer()
                loc.addAxis(Axis.YAW, 10)
                " ".toServer()
                "${loc.yaw}".toServer()
                "${loc.pitch}".toServer()
                player.teleport(loc)
            }
            Arg.cmd3 -> {}

            OpArg.test -> {
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

        fun playerAdd() {
            for (s in Bukkit.getOnlinePlayers()) if (s.name.contains(last)) list.add(s.name)
        }

        if (arg == null) {
            Arg.values().add()
            if (player.isOp) OpArg.values().add()
        } else when (arg) {
            Arg.cmd1 -> when (lastIndex) {
                1 -> arg.argList(lastIndex).add()
            }
            Arg.cmd2 -> when (lastIndex) {
                1 -> arg.argList(lastIndex).add()
                2 -> playerAdd()
            }
            Arg.cmd3 -> when (lastIndex) {
                1 -> arg.argList(lastIndex).add()
                2 -> playerAdd()
                3 -> if (last.isEmpty()) list.add(arg.argElement(args))
            }

            OpArg.test -> {}
        }
        return list
    }
}