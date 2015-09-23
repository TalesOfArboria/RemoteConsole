package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.managed.messaging.IMessenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Static convenience methods for chat and console related messaging.
 */
public class Msg {

    private Msg() {}

	public static void tell(CommandSender sender, String message, Object...params) {
        msg().tell(sender, message, params);
    }
    
    public static void tell(Player p, String message, Object...params) {
        msg().tell(p, message, params);
    }
    
    public static void tellImportant(UUID playerId, String context, String message, Object...params) {
        msg().tellImportant(playerId, context, message, params);
    }
    
    public static void info(String message, Object...params) {
        msg().info(message, params);
	}
    
    public static void debug(String message, Object...params) {
    	if (!RemoteConsolePlugin.getPlugin().isDebugging())
    		return;

        msg().debug(message, params);
    }
    
    public static void warning(String message, Object...params) {
        msg().warning(message, params);
    }
    
    public static void severe(String message, Object...params) {
    	msg().severe(message, params);
    }

    public static void broadcast(String message, Object...params) {
		msg().broadcast(message, params);
	}
    
    public static void broadcast(Collection<? extends Player> exclude, String message, Object...params) {
		msg().broadcast(exclude, message, params);
	}

    private static IMessenger msg() {
        return RemoteConsolePlugin.getPlugin().getMessenger();
    }
}
