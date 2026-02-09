package main;

import meta.handler.AbstractFileHandlerMeta;
import socket.StorageServer;

public class StorageServerRunner {
    public static void main(String[] args) {
        StorageServer.start(1122);
    }
}
