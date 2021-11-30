package me.lcproxy.jb.server.packets;

import me.lcproxy.jb.WebServer;
import me.lcproxy.jb.player.Player;
import me.lcproxy.jb.player.PlayerManager;
import me.lcproxy.jb.server.ByteBufWrapper;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class WSPacketCosmeticSet extends WSPacket {
    UUID uuid;
    long cosmeticId;
    public WSPacketCosmeticSet() {}

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {

    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        cosmeticId = -1;
        int inAmount = in.readInt();
        Player player = WebServer.getInstance().getPlayerManager().getPlayerById(conn.getAttachment());
        player.getEnabledCosmetics().clear();
        for(int i = 0; i < inAmount; i++) {
            long cosmeticId = in.readLong();
            boolean state = in.readBoolean();
            if(state) {
                this.cosmeticId = cosmeticId;
                player.getEnabledCosmetics().add((int)cosmeticId);
            }
        }

        if(this.cosmeticId == -1) {
            this.cosmeticId = 1;
            //System.out.println("Still -1");
        }
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player player = WebServer.getInstance().getPlayerManager().getPlayerById(conn.getAttachment());
        for(Player online : PlayerManager.getPlayerMap().values()) {
            if (online != player) {
                handler.sendPacket(online.getConn(), new WSPacketCosmeticGive(player.getPlayerId(), true));
                //System.out.println("Sending cosmetics to player " + online.getUsername());
            }
        }
    }
}
