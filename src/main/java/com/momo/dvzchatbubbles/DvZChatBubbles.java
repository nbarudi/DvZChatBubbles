package com.momo.dvzchatbubbles;

import com.momo.dvzchatbubbles.commands.CommandCleanTextDisplays;
import com.momo.dvzchatbubbles.commands.CommandSendMessage;
import com.momo.dvzchatbubbles.events.ChatEvents;
import com.momo.dvzchatbubbles.types.ChatBubble;
import org.bukkit.Bukkit;
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

public class DvZChatBubbles extends JavaPlugin {

	public Map<Player, ChatBubble> playerChatBubbles = new HashMap<>();

	private static DvZChatBubbles instance;

    @Override
    public void onEnable() {
		instance = this;

		registerEvents();
		registerCommands();
    }

    @Override
    public void onDisable() {
    	for (ChatBubble chatBubble: playerChatBubbles.values()) {
    		chatBubble.remove();
    	}
    	playerChatBubbles.clear();
    }

	private void registerEvents(){
		//Commented out. Just un-comment if wanting all chat messages -> bubble chat
		//Bukkit.getServer().getPluginManager().registerEvents(new ChatEvents(), this);
	}

	private void registerCommands(){
		getServer().getCommandMap().register("dvzchatbubbles", new CommandCleanTextDisplays("cleantextdisplays"));
		getServer().getCommandMap().register("dvzchatbubbles", new CommandSendMessage("sendmessage"));
	}

    public void removePlayer(Player player, ChatBubble chatBubble) {
    	chatBubble.remove();
    	playerChatBubbles.remove(player);
    }

    public Collection<ChatBubble> getChatBubbles() {
		return playerChatBubbles.values();
	}


	public static DvZChatBubbles getInstance(){
		return instance;
	}
}