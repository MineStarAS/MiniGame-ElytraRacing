package kr.kro.minestar.elytra.racing.funcions

import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.item.unbreakable
import kr.kro.minestar.utility.material.item
import org.bukkit.Material

object ItemClass {
    /**
     * Racing Item
     */
    internal val elytra = Material.ELYTRA.item().unbreakable()

    internal val fireworkRocket = Material.FIREWORK_ROCKET.item()

    /**
     * Tool Item
     */
    internal val worldEditWand = Material.WOODEN_AXE.item()
        .display("§cWorld Edit Wand")

    internal val worldEditCompass = Material.COMPASS.item()
        .display("§cWorld Edit Compass")

    internal val redStoneBlock = Material.REDSTONE_BLOCK.item()

    internal val setStartLocation = Material.GOLD_INGOT.item()
        .display("§eSet Start Location")
        .addLore(" ")
        .addLore("§8[Right Click] Set Location")

    internal val setGoalLocation = Material.EMERALD.item()
        .display("§aSet Goal Location")
        .addLore(" ")
        .addLore("§8[Right Click] Set Location")

    internal val setBoosterLocation = Material.FIREWORK_ROCKET.item()
        .display("§bSet Booster Location")
        .addLore(" ")
        .addLore("§8[Right Click] Set Location")
        .addLore("§8[Drop] Add Angle")
        .addLore("§8[Swap] Remove Angle")

    internal val removeBoosterLocation = Material.FIREWORK_STAR.item()
        .display("§7Set Booster Location")
        .addLore(" ")
        .addLore("§8[Right Click] Remove Location")

    internal val adjustOffset = Material.TRIPWIRE_HOOK.item()
        .display("§6Adjust Offset")
        .addLore(" ")
        .addLore("§8[Left Click] Subtract Offset")
        .addLore("§8[Right Click] Add Offset")

    internal val toggleCenterAndUpward = Material.STRUCTURE_VOID.item()
        .display("§3Toggle Center/Upward")
        .addLore(" ")
        .addLore("§8[Left Click] Toggle Center")
        .addLore("§8[Right Click] Toggle Upward")
}