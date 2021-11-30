package me.lcproxy.jb;

import me.lcproxy.jb.player.Player;
import me.lcproxy.jb.player.PlayerManager;
import me.lcproxy.jb.server.packets.WSPacketCosmeticGive;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WebClient extends WebSocketClient {
    final WebServer webServer;

    public WebClient(WebServer pWebServer) throws URISyntaxException {
        super(new URI("ws://194.163.177.249:5684"), new HashMap<String, String>()
        {
            {
                put("secret", "lcProxyMessagingSocketWtfCat");
            }
        });

        webServer = pWebServer;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Opened websocket to load-balancer.");
    }

    @Override
    public void onMessage(String s) {
        String[] parsedMessage = s.split(">v<");
        if(parsedMessage[0] == "set_cosmetics" && !parsedMessage[1].equals(String.valueOf(webServer.getServerId()))) {
            ArrayList<Integer> enabledCosmetics = new ArrayList<>();
            for(String str : parsedMessage[3].split(">C<")) {
                enabledCosmetics.add(Integer.parseInt(str));
            }

            for(Player online : PlayerManager.getPlayerMap().values()) {
                webServer.getServerHandler().sendPacket(online.getConn(), new WSPacketCosmeticGive(UUID.fromString(parsedMessage[2]), true, true, enabledCosmetics));
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
