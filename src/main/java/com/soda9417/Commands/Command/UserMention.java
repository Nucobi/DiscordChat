package com.soda9417.Commands.Command;


import com.soda9417.DiscordPlugin;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserMention implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args.length < 1) {return false;}

        ArrayList<String> idlist = new ArrayList<>();

        if (!DiscordPlugin.user_channel.containsKey(p)) {
            DiscordPlugin.user_channel.put(p,DiscordPlugin.DEFAULT_CHANNEL);
        }
        String channel_name = DiscordPlugin.user_channel.get(p);

        List<TextChannel> textChannels = DiscordPlugin.jda.getTextChannelsByName(channel_name,true);

        if (textChannels.isEmpty()){
            p.sendMessage("설정된 이름의 채널이 없습니다.. ["+channel_name+"] /channel 로 채널을 변경하세요.");
            return false;
        }


        StringBuilder rawMessage = new StringBuilder();
        StringBuilder message = new StringBuilder();

        for (String arg : args) {
            if (arg.startsWith("<@") && arg.endsWith(">")) {
                String username = arg;
                username = username.replaceAll("[@<>]","");

                for (TextChannel channel : textChannels) {
                    Guild guild = channel.getGuild();
                    Task<List<Member>> memberTask = guild.loadMembers();

                    for(Member memb : memberTask.get()) {
                        if(memb.getEffectiveName().equalsIgnoreCase(username.trim())){
                            idlist.add(memb.getId());
                        }
                    }
                }
            }
            if (!idlist.isEmpty()) {
                for (String id : idlist) {
                    String mention = "<@"+id+">";
                    message.append(mention);
                }
                idlist.clear();
            }
            else {
                message.append(arg);
            }
            rawMessage.append(arg+" ");
            message.append(" ");
        }

        String msg = message.toString();
        String rawmsg = rawMessage.toString();

        for (TextChannel channel : textChannels) {
            channel.sendMessage( p.getName() +  ":  " + msg).queue();
        }
        return true;
    }

}
