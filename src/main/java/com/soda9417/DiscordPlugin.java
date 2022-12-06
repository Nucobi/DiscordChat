package com.soda9417;

import com.soda9417.Bot.Bot_Listener;
import com.soda9417.Commands.Command.*;
import com.soda9417.Commands.TabCompleter.AccountTab;
import com.soda9417.Commands.TabCompleter.ChangeChannelTab;
import com.soda9417.Commands.TabCompleter.LimitChannelTab;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.JDA;


import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;


import javax.security.auth.login.LoginException;
import java.awt.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class DiscordPlugin extends JavaPlugin implements Listener {

    public final String PLUGIN_NAME = "DiscordChat";

    // config variables
    public static String DISCORD_TOKEN = "";
    public static String VOICECHAT_GENERATOR_CATEGORY = "";
    public static String GeneratorName = "";
    public static String DEFAULT_CHANNEL = "";
    public static String JOIN_LEAVE_CHANNEL = "";
    public static Boolean ACTIVATE_GENERATOR = false;
    public static Boolean ADVANCEMENT_NOTICE = false;

    public ConsoleCommandSender sender = Bukkit.getConsoleSender();
    public boolean isErrorOnBot = false;

    // these array came from 'https://dev.bukkit.org/projects/custom-advancements', thanks,Ca1ebS!
    public String[][] normal = new String[][]{{"story/mine_stone", "석기 시대"}, {"story/upgrade_tools", "더욱더 좋게"}, {"story/smelt_iron", "철이 철철 넘쳐"}, {"story/obtain_armor", "차려입기"}, {"story/lava_bucket", "화끈한 화제"}, {"story/iron_tools", "이젠 철 좀 들어라"}, {"story/deflect_arrow", "저희는 그런 것 받지 않습니다"}, {"story/form_obsidian", "아이스 버킷 챌린지"}, {"story/mine_diamond", "다이아몬드다!"}, {"story/enter_the_nether", "더 깊은 곳으로"}, {"story/shiny_gear", "다이아몬드로 날 감싸줘"}, {"story/enchant_item", "마법 부여자"}, {"story/follow_ender_eye", "스무고개"}, {"story/enter_the_end", "이걸로 끝이야?"}, {"nether/find_fortress", "끔찍한 요새"}, {"nether/get_wither_skull", "으스스한 스켈레톤"}, {"nether/obtain_blaze_rod", "포화 속으로"}, {"nether/summon_wither", "시들어 버린 언덕"}, {"nether/brew_potion", "물약 양조장"}, {"nether/create_beacon", "신호기 꾸리기"}, {"nether/find_bastion", "그때가 좋았지"}, {"nether/obtain_ancient_debris", "깊이 파묻힌 잔해"}, {"nether/obtain_crying_obsidian", "누가 양파를 써나?"}, {"nether/distract_piglin", "반짝반짝 눈이 부셔"}, {"nether/ride_strider", "두 발 달린 보트"}, {"nether/loot_bastion", "돼지와 전쟁"}, {"nether/use_lodestone", "집으로 이끌려가네"}, {"nether/charge_respawn_anchor", "목숨 충전"}, {"end/kill_dragon", "엔드 해방"}, {"end/enter_end_gateway", "머나먼 휴양지"}, {"end/find_end_city", "게임의 끝에서 만난 도시"}, {"adventure/voluntary_exile", "자진 유배"}, {"adventure/kill_a_mob", "몬스터 사냥꾼"}, {"adventure/trade", "훌륭한 거래군요!"}, {"adventure/honey_block_slide", "달콤함에 몸을 맡기다"}, {"adventure/ol_betsy","부러진 화살"}, {"adventure/sleep_in_bed", "달콤한 꿈"}, {"adventure/throw_trident", "준비하시고... 쏘세요!"}, {"adventure/shoot_arrow", "정조준"}, {"adventure/whos_the_pillager_now", "이제 누가 약탈자지?"}, {"adventure/very_very_frightening", "동에 번쩍 서에 번쩍"}, {"husbandry/safely_harvest_honey", "벌집을 내 집처럼"}, {"husbandry/breed_an_animal", "아기는 어떻게 태어나?"}, {"husbandry/tame_an_animal", "인생의 동반자"}, {"husbandry/fishy_business", "강태공이 세월을 낚듯"}, {"husbandry/silk_touch_nest", "한 벌 한 벌 정성껏 모시겠습니다"}, {"husbandry/plant_seed", "씨앗이 자라나는 곳"}, {"husbandry/tactical_fishing", "이 대신 잇몸으로"}};
    public String[][] goal = new String[][]{{"story/cure_zombie_villager", "좀비 의사"}, {"nether/create_full_beacon", "신호자"}, {"end/dragon_egg", "그다음 세대"}, {"end/respawn_dragon", "끝 아녔어?"}, {"end/dragon_breath", "양치질이 필요해 보이는걸"}, {"end/elytra", "불가능은 없다"}, {"adventure/totem_of_undying", "죽음을 초월한 자"}, {"adventure/summon_iron_golem", "도우미 고용"}};
    public String[][] challenge = new String[][]{{"nether/return_to_sender", "전해지지 않은 러브레터"}, {"nether/fast_travel", "천 리 길도 한 걸음"}, {"nether/uneasy_alliance", "쉽지 않은 동행"}, {"nether/all_potions", "뿅 가는 폭탄주"}, {"nether/all_effects", "어쩌다 이 지경까지"}, {"nether/netherite_armor", "잔해로 날 감싸줘"}, {"nether/explore_nether", "화끈한 관광 명소"}, {"end/levitate", "위쪽 공기 좋은데?"}, {"adventure/hero_of_the_village", "마을의 영웅"}, {"adventure/kill_all_mobs", "몬스터 사냥꾼"}, {"adventure/two_birds_one_arrow", "일전쌍조"}, {"adventure/arbalistic", "명사수"}, {"adventure/adventuring_time", "모험의 시간"}, {"adventure/sniper_duel", "저격 대결"}, {"adventure/bullseye", "명중"}, {"husbandry/bred_all_animals", "짝지어주기"}, {"husbandry/complete_catalogue", "집사 그 자체"}, {"husbandry/balanced_diet", "균형 잡힌 식단"}, {"husbandry/break_diamond_hoe", "도를 넘은 전념"}, {"husbandry/obtain_netherite_hoe", "도를 넘은 전념"}};


    public static JDA jda;
    public static boolean isChannelLimited = false;
    public static long SERVER_OPEN_TIME = System.currentTimeMillis();

    public static ArrayList<String> limitedChannel = new ArrayList<>();
    public static ArrayList<Player> mentionEffect_Enabled = new ArrayList<>();
    public static HashMap<Player,String> user_channel = new HashMap<>();
    public static HashMap<Player,String> user_account = new HashMap<>();


    // "💬just-talk"

    @Override
    public void onEnable() {
        // Plugin startup logic
        sender.sendMessage(ChatColor.AQUA + String.format("[%s] Initializing Plugin..",PLUGIN_NAME));

        // Getting Variables from config.yml
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        VOICECHAT_GENERATOR_CATEGORY = config.getString("generator_category");
        DISCORD_TOKEN = config.getString("bot_token");
        GeneratorName = config.getString("generator_name");
        DEFAULT_CHANNEL = config.getString("default_channel");
        JOIN_LEAVE_CHANNEL = config.getString("join_leave_channel");
        ACTIVATE_GENERATOR = config.getBoolean("activate_generator");
        ADVANCEMENT_NOTICE = config.getBoolean("advancement_notice");
        Account.TIMELIMIT = config.getInt("register_timelimit");
        Bot_Listener.PREFIX = config.getString("generated_prefix");

        // Build JDA
        JDABuilder builder = JDABuilder.create(DISCORD_TOKEN,GatewayIntent.GUILD_VOICE_STATES,GatewayIntent.GUILD_MESSAGES,GatewayIntent.MESSAGE_CONTENT,GatewayIntent.GUILD_MEMBERS,GatewayIntent.GUILD_MESSAGE_TYPING);

        builder.addEventListeners(new Bot_Listener());
        builder.setAutoReconnect(true);
        builder.setActivity(Activity.playing("Minecraft Server"));

        try {
            DiscordPlugin.jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            isErrorOnBot = true;
        }

        // Plugin Register

        getCommand("discordMessage").setExecutor(new UserMention());

        getCommand("toggleEffect").setExecutor(new ToggleEffect());

        getCommand("changeChannel").setExecutor(new ChangeChannel());
        getCommand("changeChannel").setTabCompleter(new ChangeChannelTab());

        getCommand("account").setExecutor(new Account());
        getCommand("account").setTabCompleter(new AccountTab());

        getCommand("limitChannel").setExecutor(new LimitChannel());
        getCommand("limitChannel").setTabCompleter(new LimitChannelTab());

        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        // Finish

        SERVER_OPEN_TIME = System.currentTimeMillis();

        if (isErrorOnBot){
            sender.sendMessage(ChatColor.RED+String.format("[%s] Error Occured during loading bot.",PLUGIN_NAME));
        }
        else{
            sender.sendMessage(ChatColor.YELLOW+String.format("[%s] Plugin Activated Succesfully.",PLUGIN_NAME));
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        sender.sendMessage(ChatColor.AQUA + String.format("[%s] Deactivating plugin..",PLUGIN_NAME));


        // Delete left Channel
        for (Guild guild : DiscordPlugin.jda.getGuilds()) {
            for (String channame : Bot_Listener.Bot_Made_VoiceChannel.keySet()) {
                VoiceChannel targetChan = guild.getVoiceChannelById(Bot_Listener.Bot_Made_VoiceChannel.get(channame));
                if (targetChan != null) {
                    System.out.println("아직 지워지지 않은 채널 <"+channame+">을 제거합니다..");
                    targetChan.delete().complete();
                }
            }
        }

        sender.sendMessage(ChatColor.GOLD + String.format("[%s] Deactivated.",PLUGIN_NAME));
    }


    // <Event Handle>
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Bukkit.getScheduler().callSyncMethod( this, () -> event.getPlayer().performCommand("discordMessage "+event.getMessage()) );
        StringBuilder rawmessage = Bot_Listener.mentionEffectText(event.getMessage(),Bot_Listener.recentEvent,false);
        String msg = ChatColor.translateAlternateColorCodes('&',rawmessage.toString());
        event.setMessage(msg);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getDisplayName();
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&',"[&a+&r] ")+playerName);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0,255,0));
        embed.setTitle(String.format("%s님 어서와요!",playerName));
        for (TextChannel chan : DiscordPlugin.jda.getTextChannelsByName(JOIN_LEAVE_CHANNEL,true)) {
            chan.sendMessageEmbeds(embed.build()).queue();
        }
        DiscordPlugin.mentionEffect_Enabled.add(event.getPlayer());
        user_channel.put(event.getPlayer(),DEFAULT_CHANNEL);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getDisplayName();
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&',"[&c-&r] ")+playerName);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255,0,0));
        embed.setTitle(String.format("%s님 잘 가요!",playerName));
        for (TextChannel chan : DiscordPlugin.jda.getTextChannelsByName(JOIN_LEAVE_CHANNEL,true)) {
            chan.sendMessageEmbeds(embed.build()).queue();
        }
    }

    private void AdvancementEmbed(String name,String adv,int type,boolean doMention) {
        FileConfiguration config = getConfig();
        String advtitle = config.getString("advancement_title");
        String advtext = MessageFormat.format(config.getString("advancement_message"),name,adv);
        String advChanName = config.getString("advancement_channel");
        String[] comments = config.getStringList("advancement_comment").toArray(new String[0]);

        // Prevent IndexError
        if (comments.length < 3) return;

        // Get Channels with Config value
        List<TextChannel> AdvChans = DiscordPlugin.jda.getTextChannelsByName(advChanName,false);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(advtitle);

        // Set Color of Advancement Embed
        switch (type) {
            case 0: // Challenge Advancement
                embed.setColor(new Color(186, 12, 255));
                embed.setFooter(comments[0]);
                break;
            case 1: // Goal Advancement
                embed.setColor(new Color(103, 208, 255));
                embed.setFooter(comments[1]);
                break;
            default: // Normal Advancement
                embed.setColor(new Color(164, 164, 164));
                embed.setFooter(comments[2]);
                break;
        }
        embed.setDescription(advtext);

        for (TextChannel AdvChan : AdvChans) {
            if (doMention) AdvChan.sendMessage("<@"+DiscordPlugin.user_account.get((Bukkit.getPlayer(name)))+">").queue();
            AdvChan.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @EventHandler
    public void AwardAdvancement(PlayerAdvancementDoneEvent advevent) {
        Player p = advevent.getPlayer();
        boolean haskey  = DiscordPlugin.user_account.containsKey(p);

        // Send Embed When Complete Advancement
        if (ADVANCEMENT_NOTICE) {
            String key = advevent.getAdvancement().getKey().getKey();
            System.out.println(key);
            for (int i = 0; i < challenge.length; i++) {
                if (challenge[i][0].equalsIgnoreCase(key)) {
                    String challenge_name = challenge[i][1];
                    AdvancementEmbed(p.getName(),
                            challenge_name,
                            0,
                            haskey);
                }
            }
            for (int i = 0; i < goal.length; i++) {
                if (goal[i][0].equalsIgnoreCase(key)) {
                    String goal_name = goal[i][1];
                    AdvancementEmbed(p.getName(),
                            goal_name,
                            1,
                            haskey);

                }
            }
            for (int i = 0; i < normal.length; i++) {
                if (normal[i][0].equalsIgnoreCase(key)) {
                    String normal_name = normal[i][1];
                    AdvancementEmbed(p.getName(),
                            normal_name,
                            2,
                            haskey);

                }
            }
        }
    }

}
