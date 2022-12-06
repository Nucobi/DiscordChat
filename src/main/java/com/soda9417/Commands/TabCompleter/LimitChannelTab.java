package com.soda9417.Commands.TabCompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LimitChannelTab implements TabCompleter {

    private final String[] commands = {"add","set","clear","list"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length <=1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(commands), completions);
            Collections.sort(completions);
        }
        else {
            completions.add("List<channels>");
        }

        return completions;
    }
}
