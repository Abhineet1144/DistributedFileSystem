package main;

import properties.Property;
import socket.ControlServer;
import socket.StorageServer;

public class Main {
    public static void main(String[] args) {
        Property.loadConfigurations();
        ControlServer.start();
    }
}