package com.momo.dvzchatbubbles;

import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class DvZChatBubbles extends JavaPlugin implements Listener {

	private static List<ChatBubble> bubbleList;

    public DvZChatBubbles() {
    	bubbleList = new ArrayList<ChatBubble>();
    }

    @Override
    public void onEnable() {
    	this.getCommand("cleanuptextdisplays").setExecutor(new CommandCleanupTextDisplays());

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    	for (ChatBubble chatBubble: bubbleList) {
    		chatBubble.removeChatBubble();
    	}
    	bubbleList.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Cancel the default chat message
        event.setCancelled(true);

        final String message = event.getMessage();

        ChatBubble chatBubble = new ChatBubble(event.getPlayer(), message);
        bubbleList.add(chatBubble);

        // Create the display entity and then track it to the players location
        new BukkitRunnable() {
            public void run() {
            	chatBubble.spawnChatBubble();
            }
        }.runTask(this);

        new BukkitRunnable() {
            public void run() {
            	chatBubble.removeChatBubble();
                bubbleList.remove(chatBubble);
            }
        }.runTaskLater(this, Math.max(message.length(), 60));
    }

    public static List<ChatBubble> getBubbleList() {
		return bubbleList;
	}

	public class ChatBubble {
    	private Player player;
    	private String message;
    	private TextDisplay display = null;

    	public ChatBubble(Player player, String message) {
    		this.player = player;
    		this.message = message;
    	}

    	public void spawnChatBubble() {
    		Location location = this.player.getLocation().clone();
    		location.setY(location.getY() + this.player.getEyeHeight() * 0.85);

    		this.display = this.player.getWorld().spawn(location, TextDisplay.class);
    		this.display.setText(this.message);

    		this.display.setBillboard(Billboard.CENTER);
    		this.display.setLineWidth(150);
    		this.display.setSeeThrough(false);
    		this.display.setDefaultBackground(false);
    		this.display.setShadowed(true);
    		this.display.setBrightness(new Brightness(15, 15));

            this.display.setInterpolationDuration(0);
    		this.display.setInterpolationDelay(-1);
			this.display.setTransformation(new Transformation(new Vector3f(0F,0.8F,0F), new AxisAngle4f(), new Vector3f(1), new AxisAngle4f()));

			this.player.setPassenger(this.display);
    	}

    	public void removeChatBubble() {
    		this.display.remove();
    	}
    }
}