package me.lcproxy.jb;

import lombok.SneakyThrows;
import me.lcproxy.jb.player.Player;
import me.lcproxy.jb.player.PlayerManager;
import me.lcproxy.jb.player.Rank;
import me.lcproxy.jb.server.packets.WSPacketForceCrash;
import me.lcproxy.jb.server.packets.WSSendChatMessage;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Discord extends ListenerAdapter {
    @SneakyThrows
    public void initialize() {
        JDABuilder.createLight("OTE1MDU5MzkwODEyNDAxNjg1.YaWFdw.tgHiGvYC_5-9pnwM1I9pPcHNaRQ", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Discord())
                .setActivity(Activity.playing("with the Lunar websocket"))
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();

        if (msg.getContentRaw().equals("l!online"))
        {
            msg.reply("**" + WebServer.getInstance().getPlayerManager().getTotalOnline() + "** users currently online.").queue();
        }

        if(!msg.getMember().getRoles().contains(msg.getGuild().getRoleById("916455992705835018"))) return;

        if(msg.getContentRaw().startsWith("l!announce")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 2) return;
            String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
            WebServer.getInstance().getPlayerManager().getPlayerMap().forEach((uuid, player) -> {
                if(player.isOnline()) {
                    WebServer.getInstance().getServerHandler().sendPacket(player.getConn(), new WSSendChatMessage(String.join(" ", realArgs)));
                }
            });
            msg.reply("Successfully announced message.").queue();
        }

        if(msg.getContentRaw().startsWith("l!crash")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 2) return;
            WebServer.getInstance().getPlayerManager().getPlayerMap().forEach((uuid, player) -> {
                if(player.isOnline()) {
                    if(player.getUsername().equalsIgnoreCase(args[1])) {
                        WebServer.getInstance().getServerHandler().sendPacket(player.getConn(), new WSPacketForceCrash());
                        msg.reply("Crashed " + args[1] + ".").queue();
                    }
                }
            });
        }

        if(msg.getContentRaw().startsWith("l!rank")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 3) return;
            WebServer.getInstance().getPlayerManager().getPlayerMap().forEach((uuid, player) -> {
                if(player.isOnline()) {
                    if(player.getUsername().equalsIgnoreCase(args[1])) {
                        Rank rank = Rank.getRankById(Integer.parseInt(args[2]));
                        player.setRank(rank);
                        msg.reply("Gave " + args[1] + " the rank " + rank.getName() + ".").queue();
                    }
                }
            });
        }

        if(msg.getContentRaw().startsWith("l!players")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 2) return;
            String responseMsg = "Players on **" + args[1] + "**:";
            for(Player player : PlayerManager.getPlayerMap().values()) {
                if (player != null && player.isOnline() && player.getServer() != null) {
                    if(player.getServer().toLowerCase().contains(args[1])) {
                        responseMsg += "\n- `" + player.getUsername() + "`";
                    }
                }
            }
            msg.reply(responseMsg).queue();
        }

        if(msg.getContentRaw().startsWith("l!color")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 3) return;
            for(Player player : PlayerManager.getPlayerMap().values()) {
                if (player.getUsername().equalsIgnoreCase(args[1])) {
                    player.setCustomColor(Integer.parseInt(args[2]));
                    msg.reply("Set **" + args[1] + "**'s color to **" + args[2] + "**.").queue();
                }
            }
        }
    }
}
