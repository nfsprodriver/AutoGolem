package commands

import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.Dispenser
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.jetbrains.annotations.Nullable
import java.lang.NumberFormatException
import kotlin.math.absoluteValue

class AutoGolem(private val plugin: Plugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val player: Player = sender
            val block: @Nullable Block? = player.getTargetBlock(10)
            if (block != null && block.state is Dispenser) {
                val dispenser = block.state as Dispenser
                if (args.size == 2) {
                    val countStr: String = args[0]
                    val radiusStr: String = args[1]
                    try {
                        val id: String = dispenser.toString().split("@").last()
                        val countKey = NamespacedKey(plugin, "agCount$id")
                        val radiusKey = NamespacedKey(plugin, "agRadius$id")
                        dispenser.chunk.persistentDataContainer.set(countKey, PersistentDataType.INTEGER, countStr.toInt().absoluteValue)
                        dispenser.chunk.persistentDataContainer.set(radiusKey, PersistentDataType.INTEGER, radiusStr.toInt().absoluteValue)
                        return true
                    } catch (e: NumberFormatException) {
                        player.sendMessage("count and radius must be natural numbers!")
                    }
                } else {
                    player.sendMessage("Not facing a dispenser!")
                }
            }
            player.sendMessage("Face a dispenser and type: /autogolem [count] [radius]")
            return true
        } else {
            return false
        }
    }
}