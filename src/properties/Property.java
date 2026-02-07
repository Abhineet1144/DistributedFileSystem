package properties;

import java.io.FileInputStream;

public class Property {
    private static String mode;
    private static String port;

    public static void loadConfigurations() {
        java.util.Properties props = new java.util.Properties();

        try (FileInputStream fis = new FileInputStream("./config.properties")) {
            props.load(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mode = props.getProperty("mode");
        port = props.getProperty("port");
    }

    public static String getMode() {
        return mode;
    }

    public static String getPort() {
        return port;
    }
}