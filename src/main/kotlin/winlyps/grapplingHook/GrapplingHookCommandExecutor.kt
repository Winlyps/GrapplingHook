//2.File: GrapplingHookCommandExecutor.kt
package winlyps.grapplingHook

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class GrapplingHookCommandExecutor(private val plugin: GrapplingHook) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }

        if (!sender.hasPermission("grapplinghook.use")) {
            sender.sendMessage("You do not have permission to use this command.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("Usage: /grhook <NumberOfUses>")
            return true
        }

        val numberOfUses = args[0].toIntOrNull() ?: return false

        val hook = ItemStack(Material.FISHING_ROD)
        val meta = hook.itemMeta
        meta?.setDisplayName("${ChatColor.AQUA}Grappling Hook")
        meta?.setLore(listOf("Uses left: $numberOfUses"))
        hook.itemMeta = meta

        sender.inventory.addItem(hook)
        sender.sendMessage("You have received a grappling hook with $numberOfUses uses.")
        return true
    }
}