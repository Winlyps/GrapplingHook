//1.File: GrapplingHook.kt
package winlyps.grapplingHook

import org.bukkit.plugin.java.JavaPlugin

class GrapplingHook : JavaPlugin() {

    override fun onEnable() {
        // Register command
        this.getCommand("grhook")?.setExecutor(GrapplingHookCommandExecutor(this))
        // Register event listener
        server.pluginManager.registerEvents(GrapplingHookListener(this), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
