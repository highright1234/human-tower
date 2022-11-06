package io.github.highright1234.humantower.kommand

import io.github.highright1234.humantower.HumanTower
import io.github.highright1234.humantower.TowerMaker
import io.github.highright1234.shotokonoko.monun.suspendingExecutes
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue

object HumanTowerKommand : KommandClass {
    override fun register(pluginKommand: PluginKommand) {
        pluginKommand.register("tower") {
            requires { isPlayer }
            then("size" to int()) {
                suspendingExecutes {
                    val size: Int by it
                    repeat(size) {
                        TowerMaker.create(player)
                    }
                }
                then("twerking") {
                    requires {
                        HumanTower.plugin.server.pluginManager.isPluginEnabled("SeniorCenterTwerkMachine")
                    }
                    suspendingExecutes {
                        val size: Int by it
                        repeat(size) {
                            TowerMaker.create(player, true)
                        }
                    }
                }
            }
        }
    }
}