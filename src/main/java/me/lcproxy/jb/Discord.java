package me.lcproxy.jb;

import lombok.SneakyThrows;
import me.lcproxy.jb.player.Player;
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
        if (msg.getContentRaw().equals("l!online"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("**" + WebServer.getInstance().getPlayerManager().getTotalOnline() + "** users currently online.").queue();
        }

        if(!msg.getMember().getRoles().contains(msg.getGuild().getRolesByName("Owner", false).get(0))) return;

        if(msg.getContentRaw().startsWith("l!announce")) {
            String[] args = msg.getContentRaw().split(" ");
            if (args.length < 2) return;
            String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
            WebServer.getInstance().getPlayerManager().getPlayerMap().forEach((uuid, player) -> {
                if(player.isOnline()) {
                    WebServer.getInstance().getServerHandler().sendPacket(player.getConn(), new WSSendChatMessage(String.join(" ", realArgs)));
                }
            });
        }
    }
}
