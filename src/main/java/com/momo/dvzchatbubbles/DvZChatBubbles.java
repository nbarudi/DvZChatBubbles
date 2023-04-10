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
/*    	private Location prevLocation;
    	private Location predLocation;*/

    	public ChatBubble(Player player, String message) {
    		this.player = player;
    		this.message = message;
    	}

    	public void spawnChatBubble() {
/*    		this.prevLocation = this.player.getLocation().clone();
    		this.prevLocation.setY(this.prevLocation.getY() + 2);
    		this.prevLocation.setPitch(0);
    		this.prevLocation.setYaw(0);
    		this.predLocation = this.prevLocation.clone();

    		this.display = this.player.getWorld().spawn(this.prevLocation, TextDisplay.class);*/

    		Location location = this.player.getLocation().clone();
    		location.setY(location.getY() + 2);

    		this.display = this.player.getWorld().spawn(location, TextDisplay.class);
    		this.display.setText(this.message);

    		this.display.setBillboard(Billboard.CENTER);
    		this.display.setLineWidth(150);
    		this.display.setSeeThrough(false);
    		this.display.setDefaultBackground(false);
    		//this.display.setInterpolationDuration(1);
    		this.display.setShadowed(true);
    		this.display.setBrightness(new Brightness(15, 15));
    	}

    	public void removeChatBubble() {
    		this.display.remove();
    	}

    	public void updatePosition() {
    		if (this.display != null && this.player != null && this.player.isValid()) {
    			Location location = this.player.getLocation().clone();
        		location.setY(location.getY() + 2);
    			this.display.teleport(location);
    		}
    		//The commented out code was intended for use with Display interpolation. I commented it out because it turns out that as of right now, it is useless for my purpose.
/*    		if (this.display != null && this.player != null && this.player.isValid()) {
        		Location currLocation = this.player.getLocation().clone();
        		currLocation.setY(currLocation.getY() + 2);
        		currLocation.setPitch(0);
        		currLocation.setYaw(0);

                Vector3f change = new Vector3f(
            		(float) (currLocation.getX() - this.prevLocation.getX()),
            		0F,
            		(float) (currLocation.getZ() - this.prevLocation.getZ())
        		);

    			this.display.teleport(this.predLocation);
                this.predLocation = currLocation.clone().add(change.x, change.y, change.z);

        		this.display.setInterpolationDelay(-1);
    			this.display.setTransformation(new Transformation(change, new AxisAngle4f(), new Vector3f(1), new AxisAngle4f()));

    			this.prevLocation = currLocation;
    		}*/
    	}
    }
}