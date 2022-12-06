package com.soda9417.Commands.TabCompleter;

import com.soda9417.DiscordPlugin;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ChangeChannelTab  implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (DiscordPlugin.isChannelLimited) {
            StringUtil.copyPartialMatches(args[0], DiscordPlugin.limitedChannel,completions);
        }
        else {
            List<String> AllChannels = new ArrayList<>();
            for (TextChannel chan : DiscordPlugin.jda.getTextChannels()) AllChannels.add(chan.getName());
            StringUtil.copyPartialMatches(args[0],AllChannels,completions);
        }
        return completions;
    }
}
