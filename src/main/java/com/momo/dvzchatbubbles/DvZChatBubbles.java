package com.momo.dvzchatbubbles;

import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private static Map<Player, ChatBubble> playerChatBubbles = new HashMap<Player, ChatBubble>();

    public DvZChatBubbles() {

    }

    @Override
    public void onEnable() {
    	this.getCommand("cleanuptextdisplays").setExecutor(new CommandCleanupTextDisplays());

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    	for (ChatBubble chatBubble: playerChatBubbles.values()) {
    		chatBubble.remove();
    	}
    	playerChatBubbles.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Cancel the default chat message
        event.setCancelled(true);

        Player player = event.getPlayer();
        final String message = event.getMessage();

        if (!playerChatBubbles.containsKey(player)) {
            ChatBubble chatBubble = new ChatBubble(player, message);
            playerChatBubbles.put(player, chatBubble);

            // Create the display entity and then track it to the players location
            new BukkitRunnable() {
                public void run() {
                	chatBubble.spawn();
                }
            }.runTask(this);

            new BukkitRunnable() {
                public void run() {
                	if (chatBubble.removeMessage(0)) removePlayer(player, chatBubble);
                }
            }.runTaskLater(this, Math.max(message.length(), 60));
        } else {
        	ChatBubble chatBubble = playerChatBubbles.get(player);

        	int messageID = chatBubble.addMessage(message);

            new BukkitRunnable() {
                public void run() {
                	if (chatBubble.removeMessage(messageID)) removePlayer(player, chatBubble);
                }
            }.runTaskLater(this, Math.max(message.length(), 60));
        }
    }

    private static void removePlayer(Player player, ChatBubble chatBubble) {
    	chatBubble.remove();
    	playerChatBubbles.remove(player);
    }

    public static Collection<ChatBubble> getChatBubbles() {
		return playerChatBubbles.values();
	}

	public class ChatBubble {
    	private Player player;
    	private List<String> messages;
    	private TextDisplay display = null;

    	public ChatBubble(Player player, String message) {
    		this.player = player;
    		messages = new ArrayList<String>(Arrays.asList(message));
    	}

    	public void spawn() {
    		Location location = this.player.getLocation().clone();
    		location.setY(location.getY() + this.player.getEyeHeight() * 0.85);

    		this.display = this.player.getWorld().spawn(location, TextDisplay.class);
    		this.display.setText(this.makeMessage());

    		this.display.setBillboard(Billboard.CENTER);
    		this.display.setLineWidth(150);
    		this.display.setSeeThrough(false);
    		this.display.setDefaultBackground(false);
    		this.display.setShadowed(true);
    		this.display.setBrightness(new Brightness(15, 15));

            this.display.setInterpolationDuration(0);
    		this.display.setInterpolationDelay(-1);
			this.display.setTransformation(new Transformation(new Vector3f(0F,0.8F,0F), new AxisAngle4f(), new Vector3f(1), new AxisAngle4f()));

			this.player.addPassenger(this.display);
    	}

    	public void remove() {
    		if (this.display != null && this.display.isValid()) {
        		this.display.remove();
    		}
    	}

    	public int addMessage(String message) {
    		int messageId = this.messages.size();

    		this.messages.add(message);
    		this.display.setText(this.makeMessage());

    		return messageId;
    	}

    	public boolean removeMessage(int id) {
    		this.messages.set(id, null);

    		String message = this.makeMessage();

    		if (message != null) {
        		this.display.setText(this.makeMessage());
        		return false;
    		} else {
    			return true;
    		}
    	}

    	private String makeMessage() {
    		String message = null;

    		for (int i = 0; i < this.messages.size(); i ++) {
    			if (this.messages.get(i) != null) {
        			if (message == null) {
            			message = this.messages.get(i);
        			} else {
            			message += "\n" + this.messages.get(i);
        			}
    			}
    		}

    		return message;
    	}
    }
}