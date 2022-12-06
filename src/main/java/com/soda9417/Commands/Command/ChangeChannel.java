package com.soda9417.Commands.Command;

import com.soda9417.DiscordPlugin;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChangeChannel implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,String label,String[] args) {
        Player p;
        try {
            p = (Player) sender;
        }
        catch (Throwable t){
            return false;
        }
        if (args.length<1) {
            return false;
        }
        String channelName = String.join(" ",args);
        if (p.isOp()) {
            // 디폴트 채널 설정
            DiscordPlugin.DEFAULT_CHANNEL = channelName;
            p.sendMessage(ChatColor.GREEN +"기본 채널을 '"+channelName+"' 으로 변경하였습니다.");
        }
        if (DiscordPlugin.isChannelLimited) {
            boolean flag = true;
            for (String txChan : DiscordPlugin.limitedChannel) {
                if (channelName.equals(txChan)) {
                    flag = false;
                }
            }

            if (flag) {
                p.sendMessage(ChatColor.DARK_RED+"[채널 제한됨] 지정된 채널로만 이동할 수 있습니다.");
                return false;
            }
        }
        if (DiscordPlugin.user_channel.containsKey(p)) {
            DiscordPlugin.user_channel.replace(p, channelName);
        }
        else{
            DiscordPlugin.user_channel.put(p, channelName);
        }
        p.sendMessage(ChatColor.AQUA +"채널을 '"+channelName+"' 으로 변경하였습니다.");


        return true;
    }
}
