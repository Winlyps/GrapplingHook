//3.File: GrapplingHookListener.kt
package winlyps.grapplingHook

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.util.UUID

class GrapplingHookListener(private val plugin: JavaPlugin) : Listener {

    private val lastUseTime = mutableMapOf<UUID, Long>()

    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.REEL_IN) return

        val player = event.player
        val itemInMainHand = player.inventory.itemInMainHand
        val itemInOffHand = player.inventory.itemInOffHand

        val itemMetaMainHand = itemInMainHand.itemMeta
        val itemMetaOffHand = itemInOffHand.itemMeta

        val itemMeta = if (itemInMainHand.type == Material.FISHING_ROD && itemMetaMainHand?.displayName == "${ChatColor.AQUA}Grappling Hook") {
            itemMetaMainHand
        } else if (itemInOffHand.type == Material.FISHING_ROD && itemMetaOffHand?.displayName == "${ChatColor.AQUA}Grappling Hook") {
            itemMetaOffHand
        } else {
            return
        }

        val lore = itemMeta?.lore
        if (lore.isNullOrEmpty()) return

        val usesLeft = lore[0].split(" ")[2].toIntOrNull() ?: return

        // Check if the player is not recently used the grappling hook
        if (System.currentTimeMillis() - (lastUseTime[player.uniqueId] ?: 0) < 1000) {
            return
        }

        // Update the last use time
        lastUseTime[player.uniqueId] = System.currentTimeMillis()

        // Apply additional velocity to the player based on their yaw
        val hookEntity = event.hook
        val playerLocation = player.location
        val hookLocation = hookEntity.location
        val direction = hookLocation.toVector().subtract(playerLocation.toVector()).normalize()

        // Adjust the direction based on the player's yaw
        val yaw = Math.toRadians(player.location.yaw.toDouble())
        val yawVector = Vector(-Math.sin(yaw), 0.0, Math.cos(yaw))
        val adjustedDirection = direction.add(yawVector).normalize()

        player.velocity = player.velocity.add(adjustedDirection.multiply(2.0)) // Adjust the multiplier for desired speed

        if (usesLeft <= 1) {
            if (itemInMainHand.type == Material.FISHING_ROD && itemMetaMainHand?.displayName == "${ChatColor.AQUA}Grappling Hook") {
                player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            } else if (itemInOffHand.type == Material.FISHING_ROD && itemMetaOffHand?.displayName == "${ChatColor.AQUA}Grappling Hook") {
                player.inventory.setItemInOffHand(ItemStack(Material.AIR))
            }
            // Removed the notification message
        } else {
            val newMeta = itemMeta.clone()
            newMeta.setLore(listOf("Uses left: ${usesLeft - 1}"))
            if (itemInMainHand.type == Material.FISHING_ROD && itemMetaMainHand?.displayName == "${ChatColor.AQUA}Grappling Hook") {
                itemInMainHand.itemMeta = newMeta
            } else if (itemInOffHand.type == Material.FISHING_ROD && itemMetaOffHand?.displayName == "${ChatColor.AQUA}Grappling Hook") {
                itemInOffHand.itemMeta = newMeta
            }
        }
    }
}
