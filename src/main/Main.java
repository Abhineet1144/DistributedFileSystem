package main;

import meta.handler.AbstractFileHandlerMeta;
import properties.Property;
import socket.ControlServer;

public class Main {
    public static void main(String[] args) {
        Property.loadConfigurations();
        ControlServer.start();
        AbstractFileHandlerMeta.getInstance().getFileMetaForAbsPath("");
    }
}