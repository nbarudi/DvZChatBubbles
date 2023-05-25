package com.momo.dvzchatbubbles.events;

import com.momo.dvzchatbubbles.DvZChatBubbles;
import com.momo.dvzchatbubbles.types.ChatBubble;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Cancel the default chat message
        event.setCancelled(true);

        Player player = event.getPlayer();
        final String message = event.getMessage();

        if (!DvZChatBubbles.getInstance().playerChatBubbles.containsKey(player)) {
            // Create the display entity and attach it to a player
            ChatBubble chatBubble = new ChatBubble(player, message);
            DvZChatBubbles.getInstance().playerChatBubbles.put(player, chatBubble);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DvZChatBubbles.getInstance(), chatBubble::spawn);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DvZChatBubbles.getInstance(), () -> {
                if (chatBubble.removeMessage(0)) DvZChatBubbles.getInstance().removePlayer(player, chatBubble);
            }, Math.max(message.length(), 60));


        } else {
            // Get the display entity and add new lines to it
            ChatBubble chatBubble = DvZChatBubbles.getInstance().playerChatBubbles.get(player);

            int messageID = chatBubble.addMessage(message);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DvZChatBubbles.getInstance(), ()->{
                if (chatBubble.removeMessage(messageID)) DvZChatBubbles.getInstance().removePlayer(player, chatBubble);
            }, Math.max(message.length(), 60));


        }
    }

}
