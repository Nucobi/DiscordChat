package com.soda9417.Bot;


import com.soda9417.Commands.Command.Account;
import com.soda9417.DiscordPlugin;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.print.DocFlavor;
import java.util.Arrays;
import java.util.HashMap;

import static com.soda9417.DiscordPlugin.*;

public class Bot_Listener extends ListenerAdapter {
    public static String PREFIX = "";
    public static HashMap<String,String> Bot_Made_VoiceChannel = new HashMap<>();
    public static HashMap<String,String> Generated = new HashMap<>();

    public static MessageReceivedEvent recentEvent;
    public int VChat_Count = 0; // count of generated Voicechannel by voicechat generator.
    public Member Joined = null;

    static ChatColor color = ChatColor.WHITE;
    static ChatColor mention = ChatColor.GOLD;
    static ChatColor account_mention = ChatColor.AQUA;
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        TextChannel StateChannel = jda.getTextChannelById(SERVER_STATUS_CHANNEL);
        if (StateChannel != null) {
            StateChannel.getManager().setName(SERVER_ONLINE_NAME).queue();
            Role ServerStatRole = jda.getRoleById(SERVER_NOTICE_ROLE);
            if (ServerStatRole != null) {
                StateChannel.sendMessage(":green_circle: 서버가 열렸습니다! <@&"+ServerStatRole.getId()+">").queue();
            }
        }
        for (Guild guild : jda.getGuilds()) {
            if (!ACTIVATE_GENERATOR) {
                System.out.println("음성 채널 생성기가 비활성 상태입니다.");
                break;
            }
            System.out.println(guild.getName());
            if (!VOICECHAT_GENERATOR_CATEGORY.isEmpty()) {
                for (Category category : guild.getCategoriesByName(VOICECHAT_GENERATOR_CATEGORY, true)) {
                    System.out.println(category.getName() + " 카테고리에 음성 채널 생성기를 생성합니다.");
                    guild.createVoiceChannel(GeneratorName, category).queue(vc -> Bot_Made_VoiceChannel.put(GeneratorName, vc.getId()));
                }
            }
            else {
                System.out.println("카테고리 없이 음성 채널 생성기를 생성합니다.");
                guild.createVoiceChannel(GeneratorName).queue(vc -> Bot_Made_VoiceChannel.put(GeneratorName, vc.getId()));
            }
        }
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        // 음챗 생성기
        if (event.getChannelJoined().getName().equals(GeneratorName)) {
            if (!ACTIVATE_GENERATOR) {
                System.out.println("음성 채널 생성기가 비활성 상태입니다.");
            }
            else {
                VChat_Count += 1;
                String name = PREFIX + VChat_Count;
                if (!VOICECHAT_GENERATOR_CATEGORY.isEmpty()) {
                    for (Category category : event.getGuild().getCategoriesByName(VOICECHAT_GENERATOR_CATEGORY, true)) {
                        event.getGuild().createVoiceChannel(name, category).queue(vc -> {
                                    Bot_Made_VoiceChannel.put(name, vc.getId());
                                    Generated.put(name, vc.getId());
                                    try {
                                        event.getGuild().moveVoiceMember(Joined, event.getGuild().getVoiceChannelById(vc.getId())).queue();
                                    } catch (Throwable t) {
                                        System.out.println("멤버 이동 과정 중 오류가 발생하였습니다.");
                                    }
                                }
                        );
                    }
                }
                else {
                    event.getGuild().createVoiceChannel(name).queue(vc -> {
                                Bot_Made_VoiceChannel.put(name, vc.getId());
                                Generated.put(name, vc.getId());
                                try {
                                    event.getGuild().moveVoiceMember(Joined, event.getGuild().getVoiceChannelById(vc.getId())).queue();
                                } catch (Throwable t) {
                                    System.out.println("멤버 이동 과정 중 오류가 발생하였습니다.");
                                }
                            }
                    );

                }
                Joined = event.getMember();
            }
        }
    }


    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        return;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message msg = event.getMessage();
        recentEvent = event;

        // 입력받을 채널 제한
        boolean isunlimitedchan = false;
        if (DiscordPlugin.isChannelLimited) {
            for (String limchan : DiscordPlugin.limitedChannel) {
                if (event.getMessage().getChannel().getName().equalsIgnoreCase(limchan)) {
                    isunlimitedchan = true;
                }
            }
            if (!isunlimitedchan) {
                return;
            }
        }

        // 느낌표 커맨드
        if (msg.getContentRaw().startsWith("!")) {
            boolean isCommand = expCommandHandler(msg.getContentRaw(), event);
            if (isCommand) {
                return;
            }
        }

        // 색 지정


        // check if chatter is This bot
        if (event.getAuthor().isBot()) {
            if (event.getJDA().getSelfUser().equals(jda.getSelfUser())) {
                return;
            }
        }

        // Check Mention
        String rawMsg = msg.getContentRaw();
        StringBuilder message = mentionEffectText(rawMsg,event,true);



        // Generate Format
        String messageFormat = String.format(ChatColor.AQUA +"%s"+ ChatColor.GRAY + ":" + color + " %s",
                event.getMember().getEffectiveName(),
                message);


        // send Messages to User & Console
        Bukkit.getConsoleSender().sendMessage(messageFormat);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(messageFormat);
        }

        // check Account Register
        if (Account.REGISTER_ACCOUNT.containsKey(event.getMember().getEffectiveName())) {
            Player registerPlayer = Account.REGISTER_ACCOUNT.get(event.getMember().getEffectiveName());

            if (event.getMessage().getContentRaw().trim().equalsIgnoreCase(Account.REGISTER_KEY.get(registerPlayer).trim())) {
                if ( (System.currentTimeMillis()-Account.REGISTER_TIME.get(registerPlayer)) <= Account.TIMELIMIT) {
                    DiscordPlugin.user_account.put(registerPlayer,event.getMember().getUser().getId());
                    registerPlayer.sendMessage(String.format(ChatColor.GREEN+"성공적으로 계정 '%s'와(과) 연동되었습니다! \n이제 계정 기능을 사용할 수 있습니다.",event.getMember().getEffectiveName()));
                }
                else {
                    // 나중에 수정
                    System.out.println("Timeout.");
                }
            }
        }
    }

    public static StringBuilder mentionEffectText(String rawMsg, MessageReceivedEvent event,boolean isListener) {
        StringBuilder message = new StringBuilder();
        for (String rawMsgSplit : rawMsg.split(" ")) {
            String messageSplit = rawMsgSplit;
            Boolean accMention = false;
            if (rawMsgSplit.startsWith("@") || rawMsgSplit.startsWith("<@")) {
                String userName = rawMsgSplit.replace("@", "");
                if (rawMsgSplit.startsWith("<") && rawMsgSplit.endsWith(">")) {
                    userName = userName.replace("<", "");
                    userName = userName.replace(">", "");
                }
                if (jda.getSelfUser().getId().equals(userName)) {
                    accMention = true;
                    userName = event.getMessage().getGuild().getMemberById(userName).getEffectiveName();
                }
                messageSplit = "@" + userName;
                for (Player p : DiscordPlugin.mentionEffect_Enabled) {
                    if (p.getName().equalsIgnoreCase(userName)) {
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                        p.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                new TextComponent(String.format(ChatColor.GOLD + " '%s'님이 당신을 언급했습니다. ", event.getMember().getEffectiveName())));
                    }
                    if (userName.equals(DiscordPlugin.user_account.get(p))) {
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f);
                        p.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                new TextComponent(String.format(ChatColor.AQUA + " '%s'님이 당신을 멘션했습니다. ", event.getMember().getEffectiveName())));
                        accMention = true;

                    }
                    try {
                        Member mentionedMember = event.getGuild().getMemberById(userName);
                        if (mentionedMember != null) {
                            messageSplit = "@" + mentionedMember.getEffectiveName();
                        }
                    } catch (Throwable t) {
                        messageSplit = "@" + userName;
                    }
                }

                if (accMention) {
                    messageSplit = account_mention + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + messageSplit;
                } else {
                    messageSplit = mention + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + messageSplit;
                }
                message.append(messageSplit);
            } else {
                if (isListener) messageSplit = color + messageSplit;
                message.append(messageSplit);
            }
            if ((rawMsgSplit.startsWith("@") || rawMsgSplit.startsWith("<@"))) message.append(ChatColor.RESET);
            message.append(" ");
        }
        return message;
    }

    public boolean expCommandHandler(String command,MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        if (command.contains("<@") && command.contains(">")) {
            event.getMessage().reply("> 명령어에는 멘션을 사용할 수 없어요.").queue();
            return false;
        }
        String[] coms = command.replace("!","").split(" ");
        boolean rt = false;

        // Commands

        if (coms[0].equalsIgnoreCase("VoiceChat")) {
            if (coms.length < 3) {
                return false;
            }
            switch (coms[1]){
                case "create": // voice channel creation.
                    String value = String.join(" ",Arrays.copyOfRange(coms,2,coms.length));
                    if (coms.length >= 4 && value.startsWith("\"") && value.endsWith("\"") && value.split("\" \"").length == 2) {
                        String chanName = value.split("\" \"")[0].replace("\"","");
                        String parent = value.split("\" \"")[1].replace("\"","");

                        if (!guild.getVoiceChannelsByName(chanName,true).isEmpty() || Bot_Made_VoiceChannel.containsKey(chanName)) {
                            event.getMessage().reply("> 같은 이름의 채널은 생성할 수 없습니다!").queue();
                            break;
                        }
                        boolean hasCategory = false;
                        for (Category category : guild.getCategories()) {
                            if (category.getName().equalsIgnoreCase(parent))
                               try {
                                    RestAction<VoiceChannel> gvc = guild.createVoiceChannel(chanName, category);
                                    gvc.complete();
                                    for(VoiceChannel vc : category.getVoiceChannels()) {
                                        if (vc.getName().equals(chanName)) {
                                            Bot_Made_VoiceChannel.put(chanName,vc.getId());
                                        }
                                    }
                                    event.getMessage().reply("> 카테고리 '"+category.getName()+"'에 음성 채널 '"+chanName+"'을(를) 생성하였습니다!").queue();
                                    hasCategory = true;
                                }
                                catch (Throwable t) {
                                    t.printStackTrace();
                                    System.out.println("해당 이름의 카테고리가 존재하지 않습니다.");
                                    return false;
                                }
                        }
                        if (!hasCategory) {
                            event.getMessage().reply("> 카테고리 '"+parent+"' 는 존재하지 않습니다.").queue();
                        }

                    }
                    else if (coms.length >= 3 && value.startsWith("\"") && value.endsWith("\"")) {
                        String chanName = value.replace("\"","");
                        ChannelAction<VoiceChannel> gvc = guild.createVoiceChannel(chanName);
                        gvc.complete();
                        for (VoiceChannel vc : guild.getVoiceChannels()) {
                            if (vc.getName().equalsIgnoreCase(chanName)) {
                                Bot_Made_VoiceChannel.put(chanName, vc.getId());
                            }
                        }
                        event.getMessage().reply("> 카테고리 없는 음성 채널 '"+chanName+"'을(를) 생성하였습니다!").queue();
                    }
                    rt = true;
                    break;

                case "delete":
                    String dvalue = String.join(" ",Arrays.copyOfRange(coms,2,coms.length));
                    if (dvalue.startsWith("\"") && dvalue.endsWith("\"")) {
                        String chanName = dvalue.replace("\"","");
                        if (!Bot_Made_VoiceChannel.containsKey(chanName)){
                            event.getMessage().reply("> 해당 채널은 봇이 생성한 채널이 아닙니다.").queue();
                            return false;
                        }
                        try {
                            for (String key :Bot_Made_VoiceChannel.keySet()) {
                                if (key.equalsIgnoreCase(chanName)) {
                                    VoiceChannel targetChan = guild.getVoiceChannelById(Bot_Made_VoiceChannel.get(key));
                                    AuditableRestAction<Void> delAction = targetChan.delete();
                                    delAction.complete();
                                    Bot_Made_VoiceChannel.remove(chanName);
                                }
                            }
                        }
                        catch (Throwable t) {
                            t.printStackTrace();
                            event.getMessage().reply("> 삭제 중 문제가 발생했스빈다.").queue();
                            return false;
                        }

                    }
                    rt = true;
                    break;
            }
        }


        else if (coms[0].equalsIgnoreCase("ServerStatus")) {
            // 유저 목록, 서버 오픈 시간으로부터 얼마나 지났는지 등의 정보를 말해줍니다.
            long timeSpan = (System.currentTimeMillis() - DiscordPlugin.SERVER_OPEN_TIME)/1000;
            int hour = (int) (timeSpan/3600);
            timeSpan = timeSpan%3600;
            int minute = (int) timeSpan/60;
            int second = (int) timeSpan%60;
            StringBuilder players = new StringBuilder();
            for (Player onlinePlayer :Bukkit.getServer().getOnlinePlayers()) {
                players.append(onlinePlayer.getName()).append("\n");
            }
            event.getMessage().reply(String.format(">>> <서버 상태>\n이전 reload로부터 %d:%2d:%2d 만큼 지남\n[서버 플레이어 목록]\n%s",hour,minute,second,players)).queue();
            rt = true;
        }


        else if (coms[0].equalsIgnoreCase("")) {
            rt = true;
        }
        return rt;
    }
}
