package io.github.highright1234.humantower.kommand

import io.github.highright1234.humantower.HumanTower
import io.github.highright1234.humantower.TowerMaker
import io.github.highright1234.humantower.TowerMaker.skinOf
import io.github.highright1234.shotokonoko.monun.suspendingExecutes
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue
import org.bukkit.entity.Player

object HumanTowerKommand : KommandClass {
    override fun register(pluginKommand: PluginKommand) {
        pluginKommand.register("tower") {
            requires { isPlayer }
            then("players" to players()) {
                then("skinOwner" to string()) {
                    then("size" to int()) {
                        suspendingExecutes { context ->
                            val skinOwner: String by context
                            val skin = skinOf(skinOwner)
                            val size: Int by context
                            val players: Collection<Player> by context
                            repeat(size) {
                                players.forEach { TowerMaker.create(it, skin) }
                            }
                            player.sendMessage("End")
                        }
                        then("twerking") {
                            requires {
                                HumanTower.plugin.server.pluginManager.isPluginEnabled("SeniorCenterTwerkMachine")
                            }
                            suspendingExecutes {context ->
                                val skinOwner: String by context
                                val skin = skinOf(skinOwner)
                                val size: Int by context
                                val players: Collection<Player> by context
                                repeat(size) {
                                    players.forEach { TowerMaker.create(it, skin, true) }
                                }
                                player.sendMessage("End")
                            }
                        }
                    }
                }
            }
        }
    }
}