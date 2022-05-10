package kr.kro.minestar.elytra.racing.gui

import kr.kro.minestar.elytra.racing.Main
import kr.kro.minestar.elytra.racing.Main.Companion.prefix
import kr.kro.minestar.elytra.racing.funcions.WorldClass
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.SlotKey
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.material.item
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.string.unColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class WorldListGUI(player: Player) : GUI(player) {
    override val pl = Main.pl
    override val gui = InventoryUtil.gui(6, "World List")

    private enum class Key : SlotKey {
        PREV_PAGE, NEXT_PAGE, PAGE_NUMBER, ADD_WORLD
    }

    private var page = 0

    override fun slots(): Map<out SlotKey, Slot> = mapOf(
        Pair(Key.PREV_PAGE, Slot(5, 0, Material.BLUE_CONCRETE.item().display("§9[Prev Page]"))),
        Pair(Key.PAGE_NUMBER, Slot(5, 4, Material.LIGHT_GRAY_CONCRETE.item().display("§8[Page : ${page + 1}]"))),
        Pair(Key.NEXT_PAGE, Slot(5, 8, Material.BLUE_CONCRETE.item().display("§9[Next Page]"))),

        Pair(Key.ADD_WORLD, Slot(5, 2, Material.LIME_CONCRETE.item().display("§a[Add World]"))),
    )

    override fun displaying() {
        val list = WorldClass.worldList()
        for ((slot, index) in ((page * 45) until ((page + 1) * 45)).withIndex()) {
            if (list.lastIndex < index) break
            val file = list[index]
            val item = Material.GRASS_BLOCK.item().display(file.name)
                .addLore(" ")
                .addLore("§8[Left Click] Open Design World")
            gui.setItem(slot, item)
        }
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (!isSameGUI(e)) return
        e.isCancelled = true
        if (e.clickedInventory != e.view.topInventory) return

        val clickItem = e.currentItem ?: return
        val key = getSlotKey(clickItem, false)

        if (key == null) {
            when (e.click) {
                ClickType.LEFT -> {
                    player.closeInventory()
                    val designWorld = WorldClass.enableDesignWorld(clickItem.display().unColor())
                        ?: return "$prefix §c월드 활성화에 실패하였습니다.".toPlayer(player)
                    player.teleport(designWorld.world.spawnLocation)
                }
                else -> return
            }
            return
        }

        when (key as Key) {
            Key.PREV_PAGE -> {
                if (e.click != ClickType.LEFT) return
                if (page <= 0) return
                --page
                displaying()
            }
            Key.NEXT_PAGE -> {
                if (e.click != ClickType.LEFT) return
                if (WorldClass.worldList().lastIndex < (page + 1) * 45) return
                ++page
                displaying()
            }
            Key.ADD_WORLD -> {
                if (e.click != ClickType.LEFT) return

            }

            else -> return
        }
    }

    private val init = openGUI()
}