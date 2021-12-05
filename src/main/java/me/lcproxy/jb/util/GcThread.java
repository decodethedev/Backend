package me.lcproxy.jb.util;

import lombok.SneakyThrows;
import me.lcproxy.jb.WebServer;

public class GcThread extends Thread {

    @SneakyThrows
    @Override
    public void run() {
        while(true) {
            Thread.sleep(30000);
            System.gc();
        }
    }
}
