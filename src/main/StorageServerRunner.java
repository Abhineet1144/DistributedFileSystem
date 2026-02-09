package main;

import socket.StorageServer;

public class StorageServerRunner {
    public static void main(String[] args) {
        StorageServer.start(1122);
    }
}
