package main;

import socket.StorageServer;

public class StorageServerRunner {
    static void main() {
        StorageServer.start(1122);
    }
}
