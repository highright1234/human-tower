package io.github.highright1234.humantower

import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.seniorcentertwerkmachine.util.NpcUtil
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.monun.tap.fake.FakeEntity
import io.github.monun.tap.protocol.PacketSupport
import io.github.monun.tap.protocol.sendPacket
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.WeakHashMap

object TowerMaker {

//    fun Player.isFake() : Boolean = kotlin.runCatching { fake() }.getOrNull()?.let { true } ?: false
//
//    fun Player.fake(): FakeEntity<Player> {
//        @Suppress("UNCHECKED_CAST")
//        return HumanTower.fakeServer.entities.first { it.bukkitEntity == this } as FakeEntity<Player>
//    }

    private val passengers = WeakHashMap<Player, Player>()

    suspend fun create(player: Player, isTwerking: Boolean = false) {
        val tail = tailOf(player)
        val location = tail.location.clone().apply {
            pitch = 0.0f
            @Suppress("MagicNumber")
            yaw += 30
        }
        val npc: FakeEntity<Player> = if (isTwerking) {
//        if (isTwerking) {
            NpcUtil.createNpc(player.name, location).getOrNull()?.fakePlayer ?: return
        } else {
            HumanTower.fakeServer.spawnPlayer(location, "", player.playerProfile.properties)
        }
        passengers[player] = npc.bukkitEntity
        plugin.launch {
            delay(2) // fake server 이놈 update 될때까지 기다려야함
            player.sendMessage(passengers[player]!!.name)
            val packet = PacketSupport.mount(tail.entityId, intArrayOf(npc.bukkitEntity.entityId))
            Bukkit.getOnlinePlayers().forEach {
                it.sendPacket(packet)
            }
            launch {
                while(npc.valid) {
                    @Suppress("MagicNumber")
                    npc.moveTo(npc.location.clone().apply { yaw += 30 })
                    delay(1)
                }
            }
        }
    }

//    private tailrec fun tailOf(parent: Player): FakeEntity<Player>? {
//        if (parent.passengers.filterIsInstance<Player>().firstOrNull() == null) {
//            return if (parent.isFake()) parent.fake() else null
//        }
//        return tailOf(parent.passengers.filterIsInstance<Player>().first())
//    }

    private tailrec fun tailOf(parent: Player): Player {
        if (passengers[parent] == null) return parent
        return tailOf(passengers[parent]!!)
    }
}