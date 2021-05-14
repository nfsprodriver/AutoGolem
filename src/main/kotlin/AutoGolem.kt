import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.Dispenser
import org.bukkit.block.data.Directional
import org.bukkit.entity.EntityType
import org.bukkit.entity.IronGolem
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import commands.AutoGolem as AutoGolemCommand

class AutoGolem : JavaPlugin(){
    override fun onEnable() {
        getCommand("autogolem")?.setExecutor(AutoGolemCommand(this))
        server.scheduler.scheduleSyncRepeatingTask(this, {
            dispenserTask()
        }, 0L, 60L)
    }

    private fun dispenserTask() {
        server.worlds.forEach { world ->
            world.loadedChunks.forEach { chunk ->
                val dispensers: List<Dispenser> = chunk.tileEntities.filterIsInstance<Dispenser>()
                dispensers.forEach { dispenser ->
                    val id: String = dispenser.toString().split("@").last()
                    val countKey = NamespacedKey(this, "agCount$id")
                    val radiusKey = NamespacedKey(this, "agRadius$id")
                    if (chunk.persistentDataContainer.has(countKey, PersistentDataType.INTEGER) && chunk.persistentDataContainer.has(radiusKey, PersistentDataType.INTEGER)) {
                        val count = chunk.persistentDataContainer.get(countKey, PersistentDataType.INTEGER)
                        val radius = chunk.persistentDataContainer.get(radiusKey, PersistentDataType.INTEGER)
                        if (radius != null) {
                            if (dispenser.location.getNearbyLivingEntities(radius.toDouble()) { entity -> entity is IronGolem }.size < count!!) {
                                val disInv: Inventory = dispenser.inventory
                                if (disInv.contains(Material.CARVED_PUMPKIN, 1) && disInv.contains(Material.IRON_BLOCK, 4)) {
                                    val facing: Vector = (dispenser.blockData as Directional).facing.direction
                                    dispenser.location.world.spawnEntity(dispenser.location.add(facing), EntityType.IRON_GOLEM)
                                    disInv.removeItem(ItemStack(Material.CARVED_PUMPKIN, 1))
                                    disInv.removeItem(ItemStack(Material.IRON_BLOCK, 4))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}