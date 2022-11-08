package io.github.highright1234.humantower

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import io.github.highright1234.seniorcentertwerkmachine.util.NpcUtil
import io.github.highright1234.shotokonoko.Shotokonoko.plugin
import io.github.highright1234.shotokonoko.coroutine.MutableDelayData
import io.github.highright1234.shotokonoko.coroutine.mutableDelay
import io.github.monun.tap.fake.FakeEntity
import io.github.monun.tap.mojangapi.MojangAPI
import io.github.monun.tap.protocol.PacketSupport
import io.github.monun.tap.protocol.sendPacket
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

object TowerMaker {

//    fun Player.isFake() : Boolean = kotlin.runCatching { fake() }.getOrNull()?.let { true } ?: false
//
//    fun Player.fake(): FakeEntity<Player> {
//        @Suppress("UNCHECKED_CAST")
//        return HumanTower.fakeServer.entities.first { it.bukkitEntity == this } as FakeEntity<Player>
//    }

    private val passengers = WeakHashMap<Player, Player>()

    suspend fun create(player: Player, profiles: MojangAPI.SkinProfile? = null, isTwerking: Boolean = false) {
        val tail = tailOf(player)
        val location = tail.location.clone().apply {
            pitch = 0.0f
            @Suppress("MagicNumber")
            yaw += 30
        }
        val profile = profiles ?: skinOf(player.name)
        val npc: FakeEntity<Player> = if (isTwerking) {
//        if (isTwerking) {
            NpcUtil.createNpc(player.name, location, null, profile).getOrNull()?.fakePlayer ?: return
        } else {
            HumanTower.fakeServer.spawnPlayer(
                location,
                "",
                profile?.profileProperties()?.toSet() ?: emptySet()
            )
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

    private val profileImporter : MutableMap<String, Job> = ConcurrentHashMap<String, Job>()
    private val skinCache = ConcurrentHashMap<String, MojangAPI.SkinProfile?>()
    private val skinCacheRemover = ConcurrentHashMap<String, MutableDelayData>()


    // 솔직히 이거 스킨 말고 다른것도 포함되는거 아는데
    // 귀찮아서 스킨이라함
    suspend fun skinOf(name: String): MojangAPI.SkinProfile? {

        profileImporter[name]?.join()
        skinCache[name]?.let {
            @Suppress("MagicNumber")
            skinCacheRemover[name]?.timeToRun = System.currentTimeMillis() + 60000L
            return it
        }

        println("응애애")
        var profile: MojangAPI.SkinProfile? = null
        plugin.launch(plugin.asyncDispatcher) {
            profile = MojangAPI.fetchProfile(name)?.uuid()?.let { MojangAPI.fetchSkinProfile(it) }
            println("씨ㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣ바")
            skinCache[name] = profile
            @Suppress("MagicNumber")
            val mutableDelayData = mutableDelay(60000L)
            skinCacheRemover[name] = mutableDelayData
            plugin.launch {
                mutableDelayData.block()
                skinCache -= name
                skinCacheRemover -= name
            }
        }.also {
            profileImporter[name] = it
            it.join()
        }
        println("씨바")
        return profile
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