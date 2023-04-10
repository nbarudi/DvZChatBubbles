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
        	int count = 0;
        	int duration = Math.max(message.length() * 2, 60);

            public void run() {
            	if (count == 0) {
                	chatBubble.spawnChatBubble();
            	}

                if (count < duration) {
                	chatBubble.updatePosition();
                	count++;
                } else {
                	chatBubble.removeChatBubble();
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 1);
        bubbleList.remove(chatBubble);
    }

    public static List<ChatBubble> getBubbleList() {
		return bubbleList;
	}

	public class ChatBubble {
    	private Player player;
    	private String message;
    	private TextDisplay display = null;
    	private Location startLocation;

    	public ChatBubble(Player player, String message) {
    		this.player = player;
    		this.message = message;
    	}

    	public void spawnChatBubble() {
    		this.startLocation = this.player.getLocation().clone();
    		this.startLocation.setY(this.startLocation.getY() + 2);
    		this.startLocation.setPitch(0F);
    		this.startLocation.setYaw(0F);
    		this.display = this.player.getWorld().spawn(this.startLocation, TextDisplay.class);
    		this.display.setText(this.message);

    		this.display.setBillboard(Billboard.CENTER);
    		this.display.setLineWidth(150);
    		this.display.setSeeThrough(false);
    		this.display.setDefaultBackground(false);
    		this.display.setInterpolationDuration(1);
    		this.display.setShadowed(true);
    		this.display.setBrightness(new Brightness(15, 15));
    	}

    	public void removeChatBubble() {
    		this.display.remove();
    	}

    	public void updatePosition() {
    		if (this.display != null && this.player != null && this.player.isValid()) {
        		Location currentLocation = this.player.getLocation().clone();
        		currentLocation.setY(currentLocation.getY() + 2);
        		currentLocation.setPitch(0F);
        		currentLocation.setYaw(0F);

                Vector3f translation = new Vector3f(
            		(float) (currentLocation.getX() - this.startLocation.getX()),
            		(float) (currentLocation.getY() - this.startLocation.getY()),
            		(float) (currentLocation.getZ() - this.startLocation.getZ())
        		);
        		AxisAngle4f leftRotation = new AxisAngle4f(0, 0, 0, 0);
        		Vector3f scale = new Vector3f(1, 1, 1);
        		AxisAngle4f rightRotation = new AxisAngle4f(0, 0, 0, 0);

        		this.display.setInterpolationDelay(0);
    			this.display.setTransformation(new Transformation(translation, leftRotation, scale, rightRotation));
    		}
    	}
    }
}