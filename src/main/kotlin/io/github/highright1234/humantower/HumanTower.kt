package io.github.highright1234.humantower

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import io.github.highright1234.humantower.kommand.HumanTowerKommand
import io.github.highright1234.humantower.listener.HumanTowerListener
import io.github.monun.kommand.kommand
import io.github.monun.tap.fake.FakeEntityServer
import kotlinx.coroutines.delay

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
        fakeServer = FakeEntityServer.create(this)
        server.onlinePlayers.forEach(fakeServer::addPlayer)
        launch {
            while (true) {
                fakeServer.update()
                delay(1)
            }
        }
        kommand {
            kommands.forEach { it.register(this) }
        }
        listeners.forEach {
            server.pluginManager.registerSuspendingEvents(it, this)
        }
    }

    override suspend fun onDisableAsync() {
        server.onlinePlayers.forEach(fakeServer::removePlayer)
    }
}
