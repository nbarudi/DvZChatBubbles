package com.momo.dvzchatbubbles;

import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DvZChatBubbles extends JavaPlugin implements Listener {

    public DvZChatBubbles() {

    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Cancel the default chat message
        event.setCancelled(true);

        final Player player = event.getPlayer();
        final String message = event.getMessage();

        ChatBubble chatBubble = new ChatBubble(player, message);

        // Create the display entity and then track it to the players location
        new BukkitRunnable() {
        	int count = 0;
        	int duration = Math.max(message.length() * 2, 60);

            public void run() {
            	if (count == 0) {
                	chatBubble.spawnChatBubble();
            	}

                if (count < duration) {
                	count++;
                } else {
                	chatBubble.removeChatBubble();
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    public class ChatBubble {
    	Player player;
    	String message;
    	TextDisplay display;
    	Location location;

    	public ChatBubble(Player player, String message) {
    		this.player = player;
    		this.message = message;
    		this.location = player.getLocation();
    		this.location.setY(location.getY() + 2);
    	}

    	public void spawnChatBubble() {
    		this.display = this.player.getWorld().spawn(this.location, TextDisplay.class);
    		this.display.setBillboard(Billboard.CENTER);
    		this.display.setLineWidth(150);
    		this.display.setText(this.message);
    		this.display.setSeeThrough(true);
    	}

    	public void removeChatBubble() {
    		this.display.remove();
    	}
    }
}