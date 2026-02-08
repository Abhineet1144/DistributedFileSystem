package properties;

import java.io.File;
import java.io.FileInputStream;

public class Property {
    private static String mode;
    private static String port;
    private static String[] storageServers;

    public static void loadConfigurations() {
        java.util.Properties props = new java.util.Properties();
        try (FileInputStream fis = new FileInputStream(new File("config.properties"))) {
            props.load(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mode = props.getProperty("mode");
        port = props.getProperty("port");
        storageServers = props.getProperty("storage.sevrers").split(",");
    }

    public static String getMode() {
        return mode;
    }

    public static String getPort() {
        return port;
    }

    public static String[] getStorageServers() {
        return storageServers;
    }
}