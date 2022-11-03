package io.github.highright1234.harasample

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import io.github.highright1234.harasample.kommand.HumanTowerKommand
import io.github.highright1234.harasample.listener.HumanTowerListener
import io.github.monun.kommand.kommand
import io.github.monun.tap.fake.FakeEntityServer

class HumanTower : SuspendingJavaPlugin() {

    private val listeners = listOf(
        HumanTowerListener
    )

    private val kommands = listOf(
        HumanTowerKommand
    )

//    private fun file(name: String) = File(dataFolder, name)

    companion object {
        lateinit var plugin: HumanTower
        lateinit var fakeServer: FakeEntityServer
    }
    override suspend fun onEnableAsync() {
        plugin = this

        kommand {
            kommands.forEach { it.register(this) }
        }
        listeners.forEach {
            server.pluginManager.registerSuspendingEvents(it, this)
        }
    }
}
