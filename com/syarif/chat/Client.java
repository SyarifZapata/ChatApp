package com.syarif.chat;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Created by Arkad on 3/9/2017.
 */
public class Client extends NetworkConnection{

    private String ip;
    private int port;

    public Client(String ip, int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return ip;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
