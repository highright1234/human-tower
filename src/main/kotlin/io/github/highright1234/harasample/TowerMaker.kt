package io.github.highright1234.harasample

import io.github.highright1234.seniorcentertwerkmachine.util.NpcUtil
import io.github.monun.tap.fake.FakeEntity
import org.bukkit.entity.Player

object TowerMaker {

//    fun Player.isFake() : Boolean = kotlin.runCatching { fake() }.getOrNull()?.let { true } ?: false
//
//    fun Player.fake(): FakeEntity<Player> {
//        @Suppress("UNCHECKED_CAST")
//        return HumanTower.fakeServer.entities.first { it.bukkitEntity == this } as FakeEntity<Player>
//    }

    suspend fun create(player: Player, isTwerking: Boolean = false) {
        val npc: FakeEntity<Player> = if (isTwerking) {
            NpcUtil.createNpc(player.name, player.location).getOrNull()?.fakePlayer ?: return
        } else {
            HumanTower.fakeServer.spawnPlayer(player.location, "", player.playerProfile.properties)
        }
        tailOf(player).addPassenger(npc.bukkitEntity)
    }

//    private tailrec fun tailOf(parent: Player): FakeEntity<Player>? {
//        if (parent.passengers.filterIsInstance<Player>().firstOrNull() == null) {
//            return if (parent.isFake()) parent.fake() else null
//        }
//        return tailOf(parent.passengers.filterIsInstance<Player>().first())
//    }

    private tailrec fun tailOf(parent: Player): Player {
        if (parent.passengers.filterIsInstance<Player>().firstOrNull() == null) return parent
        return tailOf(parent.passengers.filterIsInstance<Player>().first())
    }
}