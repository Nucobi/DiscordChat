package com.soda9417.Commands.Command;


import com.soda9417.DiscordPlugin;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Account implements CommandExecutor {

    public static long TIMELIMIT = 60000; // 단위는 밀리초입니다.

    public static HashMap<String,Player> REGISTER_ACCOUNT = new HashMap<>();
    public static HashMap<Player,String> REGISTER_KEY = new HashMap<>();
    public static HashMap<Player,Long> REGISTER_TIME = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        Player p = null;
        try {
            p = (Player) sender;
        }
        catch (Throwable t) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED +"You cannot use this command in console.");
        }

        if (args.length < 1) {
            return false;
        }

        switch (args[0]) {
            case "register":
                if (args.length <2) {
                    p.sendMessage(ChatColor.RED+"형식에 맞지 않습니다! \n사용법: /account register [계정 닉네임]");
                    break;
                }
                if (DiscordPlugin.user_account.containsKey(p)) {
                    // reset User Account
                    p.sendMessage("기존에 연동된 계정 '" + DiscordPlugin.jda.getUserById(DiscordPlugin.user_account.get(p)).getName() + "' 을(를) 연동 해제합니다.");
                    DiscordPlugin.user_account.remove(p);
                }
                // reset Keys
                REGISTER_ACCOUNT.remove(p);
                REGISTER_TIME.remove(p);
                REGISTER_KEY.remove(p);

                String accountName = String.join(" ",Arrays.copyOfRange(args,1,args.length));

                startRegister(p,accountName);
                break;
            case "my_account":
                // 아직 개발 중입니다
                if (DiscordPlugin.user_account.containsKey(p)) {
                    String accountMessage =
                        String.format(
                        """
                            =============================
                            연동된 계정 : '%s'
                            이 기능은 준비 중입니다..
                            =============================""",
                            DiscordPlugin.jda.getUserById(DiscordPlugin.user_account.get(p)).getName()
                    );
                    p.sendMessage(accountMessage);
                }
                else {
                    p.sendMessage(ChatColor.RED+"연동된 계정이 없습니다!\n/account register <계정 이름> 으로 연동하세요.");
                }
                break;
            case "move":
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED+"형식에 맞지 않습니다! \n사용법: /account move <음성 채널>");
                    break;
                }
                if (!DiscordPlugin.user_account.containsKey(p)) {
                    break;
                }
                String voiceChatName = String.join(" ",Arrays.copyOfRange(args,1,args.length));
                String channel_name = DiscordPlugin.user_channel.get(p);
                List<TextChannel> textChannels = DiscordPlugin.jda.getTextChannelsByName(channel_name, true);

                Boolean flag = true;
                for (TextChannel chan : textChannels) {
                    List<VoiceChannel> voiceChannels = chan.getGuild().getVoiceChannelsByName(voiceChatName,true);
                    if (!voiceChannels.isEmpty()){
                        Member moveMember = chan.getGuild().getMemberById(DiscordPlugin.user_account.get(p));
                        if (moveMember != null) {
                            try {
                                chan.getGuild().moveVoiceMember(moveMember, voiceChannels.get(0)).queue();
                            }
                            catch (Throwable t) {
                                p.sendMessage(ChatColor.DARK_RED+"에러. 현재 음성 채널에 연동된 계정이 없습니다. /account register <계정 이름> 으로 연동해주세요.");
                                return false;
                            }
                            finally {
                                p.sendMessage(ChatColor.AQUA + "해당 보이스 채널로 이동되었습니다!\n" + ChatColor.DARK_AQUA + "만약 이동되지 않는다면,연동된 계정이 보이스 채널에 들어 있는지 확인해주세요.");
                            }
                            flag = false;
                        }
                    }
                }

                if (flag) {p.sendMessage(ChatColor.RED+"해당되는 보이스 채널이 없습니다..");}
                break;
        }


        return true;
    }

    public void startRegister(Player player,String account_Name) {
        Random rand = new Random();
        int password = rand.nextInt(8999)+1000;

        REGISTER_KEY.put(player, Integer.toString(password));
        REGISTER_TIME.put(player,System.currentTimeMillis());

        REGISTER_ACCOUNT.put(account_Name,player);

        player.sendMessage(String.format(ChatColor.AQUA+"지금부터 %.1f초 내로, '%s'계정을 이용해 비밀번호 "
            +ChatColor.BOLD+""+ChatColor.GOLD+"[%d]"+ChatColor.RESET+""+ChatColor.AQUA+
            "을(를) 입력해 주세요.",(float) TIMELIMIT/1000,account_Name,password));
    }
}
