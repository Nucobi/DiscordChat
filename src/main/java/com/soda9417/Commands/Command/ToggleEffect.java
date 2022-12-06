package com.soda9417.Commands.Command;

import com.soda9417.DiscordPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleEffect implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player;
        try {
            player = (Player) sender;
        }
        catch (Throwable t) {
            return false;
        }
        if (DiscordPlugin.mentionEffect_Enabled.contains(player)) {
            player.sendMessage(ChatColor.DARK_AQUA+"알림을 껐습니다.");
            DiscordPlugin.mentionEffect_Enabled.remove(player);
        }
        else {
            player.sendMessage(ChatColor.GOLD+"알림을 켰습니다!");
            DiscordPlugin.mentionEffect_Enabled.add(player);
        }
        return true;
    }
}
