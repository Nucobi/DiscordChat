package com.soda9417.Commands.TabCompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountTab  implements TabCompleter {
    private final String[] cases = {"register","move","my_account"};
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completion = new ArrayList<>();

        if (args.length <= 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(cases), completion);
        }
        else if (args.length == 2) {
            switch (args[0]) {
                case "register":
                    completion.add("<nickname>");
                    break;
                case "move":
                    completion.add("<text_channel_name>");
                    break;
            }
        }
        return completion;
    }
}
