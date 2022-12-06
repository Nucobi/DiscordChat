package com.soda9417.Commands.Command;

import com.soda9417.DiscordPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LimitChannel implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = null;
        try {
            p = (Player) sender;
        }
        catch (ClassCastException ct) {
            Bukkit.getConsoleSender().sendMessage("콘솔에서는 사용할 수 없습니다..");
        }

        // add, set ,clear
        if (args.length < 1) {return false;}

        switch (args[0]) {
            case "add":
                if (!p.isOp()) {
                    p.sendMessage(ChatColor.RED+"권한이 없어 채널을 제한할 수 없습니다.");
                    return false;
                }
                if (args.length<2) {break;}
                String[] chans = String.join(" ",Arrays.copyOfRange(args,1,args.length)).split(",");
                for (String chanName : chans) {
                    if (!DiscordPlugin.limitedChannel.contains(chanName)) {
                        DiscordPlugin.limitedChannel.add(chanName);
                    }
                }
                StringBuilder builder = new StringBuilder();
                for (String arg : chans)
                    builder.append(arg).append(",");
                p.sendMessage(ChatColor.YELLOW+"채널 한정 목록에 ["+builder+"] 를 추가하였습니다.");
                DiscordPlugin.isChannelLimited = true;
                break;
            case "set":
                if (!p.isOp()) {
                    p.sendMessage(ChatColor.RED+"권한이 없어 채널을 제한할 수 없습니다.");
                    return false;
                }
                if (args.length<2) {break;}
                String[] setchans = String.join(" ",Arrays.copyOfRange(args,1,args.length)).split(",");
                DiscordPlugin.limitedChannel.clear();
                DiscordPlugin.limitedChannel.addAll(Arrays.asList(setchans));
                DiscordPlugin.isChannelLimited = true;
                StringBuilder chanlist = new StringBuilder();
                for (String arg : setchans)
                    chanlist.append(arg).append(",");
                p.sendMessage(ChatColor.AQUA+"채널 한정 목록을 ["+chanlist+"] 으로 설정하였습니다.");
                break;

            case "clear":
                if (!p.isOp()) {
                    p.sendMessage(ChatColor.RED+"권한이 없어 채널을 제한할 수 없습니다.");
                    return false;
                }
                DiscordPlugin.isChannelLimited = false;
                DiscordPlugin.limitedChannel.clear();
                p.sendMessage(ChatColor.GREEN+"채널 제한이 초기화 되었습니다.");
                break;
            case "list":
                StringBuilder list = new StringBuilder();
                for (String chanName : DiscordPlugin.limitedChannel) {
                    list.append(chanName).append(",");
                }
                p.sendMessage(ChatColor.GOLD+"제한된 채널 목록: "+ChatColor.WHITE+"["+ list +"]");
                break;
        }

        return true;
    }
}
