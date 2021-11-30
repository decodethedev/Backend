package me.lcproxy.jb.util;

import lombok.SneakyThrows;
import me.lcproxy.jb.WebServer;

public class UpdateTagThread extends Thread {

    @SneakyThrows
    @Override
    public void run() {
        while(true) {
            Thread.sleep(5000);
            WebServer.getInstance().updateTags();
        }
    }
}
