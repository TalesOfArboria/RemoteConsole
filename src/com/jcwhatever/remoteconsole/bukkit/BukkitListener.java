package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.remoteconsole.bukkit.connect.ConnectionManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit event listener
 */
public class BukkitListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onCommand(PlayerCommandPreprocessEvent event) {

        ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        String text = time() + "[COMMAND] " + event.getPlayer().getName() +
                " Issued command: " + event.getMessage();

        if (event.isCancelled())
            text += "(CANCELLED)";

        manager.sendLine(text);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {

        ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        Player player = event.getPlayer();

        String text = time() + "[CONNECT] " + event.getPlayer().getName() +
                " Logged in. ID: " + player.getUniqueId() + ", IP: " + player.getAddress();

        manager.sendLine(text);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {

        ConnectionManager manager = RemoteConsolePlugin.getConnectionManager();

        Player player = event.getPlayer();

        String text = time() + "[DISCONNECT] " + player.getName() + " left the server.";

        manager.sendLine(text);
    }

    private String time() {
        return '[' + RemoteConsolePlugin.formatTime(System.currentTimeMillis()) + "] ";
    }
}
