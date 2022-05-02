package kr.kro.minestar.elytra.racing.gui

import kr.kro.minestar.elytra.racing.Main
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.SlotKey
import kr.kro.minestar.utility.material.item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent

class MenuGUI(player: Player) : GUI(player) {
    override val gui = InventoryUtil.gui(3, "Elytra Racing Menu")
    override val pl = Main.pl

    private enum class Key : SlotKey {
        TEST
    }

    override fun slots(): Map<out SlotKey, Slot> = mapOf(
        Pair(Key.TEST, Slot(0, 0, Material.GRASS_BLOCK.item())),
    )

    init {
        openGUI()
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (!isSameGUI(e)) return
        e.isCancelled = true
        if (e.clickedInventory != e.view.topInventory) return

        val clickItem = e.currentItem ?: return
        val slotKey = getSlotKey(clickItem, false) ?: return

        when (slotKey as Key) {



            else -> return
        }
    }
}