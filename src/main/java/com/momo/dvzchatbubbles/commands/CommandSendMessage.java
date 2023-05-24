package com.momo.dvzchatbubbles.commands;

import com.momo.dvzchatbubbles.DvZChatBubbles;
import com.momo.dvzchatbubbles.types.ChatBubble;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandSendMessage extends Command {


    public CommandSendMessage(@NotNull String name) {
        super(name);
        this.usageMessage = "/" + this.getName() + " <Message>";
        this.description = "Send a Bubble Chat message!";
        this.setPermission("dvzchatbubbles.command.sendmessage");

        this.setAliases(new ArrayList<>() {{
            add("me");
            add("sm");
            add("message");
            add("sendm");
        }});
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if(!(sender instanceof Player player)) return false;

        if(args.length == 0) {
            player.sendMessage(Component.text(ChatColor.RED + "Invalid Usage: " + this.usageMessage));
            return false;
        }

        StringBuilder builder = new StringBuilder();
        for(String word : args){
            builder.append(word).append(" ");
        }

        String message = builder.substring(0, builder.length()-1);

        if(!DvZChatBubbles.getInstance().playerChatBubbles.containsKey(player)){
            ChatBubble chatBubble = new ChatBubble(player, message);
            DvZChatBubbles.getInstance().playerChatBubbles.put(player, chatBubble);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DvZChatBubbles.getInstance(), chatBubble::spawn);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DvZChatBubbles.getInstance(), () -> {
                if (chatBubble.removeMessage(0)) DvZChatBubbles.getInstance().removePlayer(player, chatBubble);
            }, Math.max(message.length(), 60));
        } else{
            ChatBubble chatBubble = DvZChatBubbles.getInstance().playerChatBubbles.get(player);

            int messageID = chatBubble.addMessage(message);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DvZChatBubbles.getInstance(), ()->{
                if (chatBubble.removeMessage(messageID)) DvZChatBubbles.getInstance().removePlayer(player, chatBubble);
            }, Math.max(message.length(), 60));
        }
        return false;
    }
}
