package main;

import properties.Property;
import socket.ControlServer;

public class Main {
    public static void main(String[] args) {
        Property.loadConfigurations();
        ControlServer.start();
    }
}