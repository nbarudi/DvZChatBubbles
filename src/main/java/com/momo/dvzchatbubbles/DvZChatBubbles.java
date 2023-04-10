package com.momo.dvzchatbubbles;

import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
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

        // Create the display entity
        new BukkitRunnable() {
            public void run() {
                ChatBubble chatBubble = new ChatBubble(player, message);
            }
        }.runTask(this);

        // Update the entity location every tick
        new BukkitRunnable() {
            public void run() {
            	int count = 0;
            	int duration = 60;
                if (count < duration) {
                	count++;
                } else {
                	chatBubble.display.remove();
                    cancel();
                }
            }
        }.runTaskTimer(this, 1L, 1L);
    }

    private class ChatBubble {
    	Player player;
    	TextDisplay display;
    	Location location;

    	private ChatBubble(Player player, String message) {
    		this.player = player;
    		location = player.getLocation();
    		location.setY(location.getY() + 2);
    		this.display = player.getWorld().spawn(location, TextDisplay.class);
    		this.display.setBillboard(Billboard.CENTER);
    		this.display.setLineWidth(150);
    		this.display.setText(message);
    		this.display.setSeeThrough(true);
    	}
    }
}