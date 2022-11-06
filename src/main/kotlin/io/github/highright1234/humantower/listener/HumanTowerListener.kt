package io.github.highright1234.humantower.listener

import io.github.highright1234.humantower.HumanTower
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object HumanTowerListener : Listener {
    @EventHandler
    fun PlayerJoinEvent.on() {
        HumanTower.fakeServer.addPlayer(player)
    }
    @EventHandler
    fun PlayerQuitEvent.on() {
        HumanTower.fakeServer.removePlayer(player)
    }
}