package properties;

import java.io.File;
import java.io.FileInputStream;

public class Property {
    private static String mode;
    private static String port;
    private static String dumpFile;
    private static String[] storageServers;
    private static String storagePath;
    private static long allocatedBytes;

    public static void loadConfigurations() {
        java.util.Properties props = new java.util.Properties();
        try (FileInputStream fis = new FileInputStream(new File("config.properties"))) {
            props.load(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mode = props.getProperty("mode");
        port = props.getProperty("port");
        dumpFile = props.getProperty("dump.file");
        storageServers = props.getProperty("storage.servers").split(",");
        storagePath = props.getProperty("storage.path");
        allocatedBytes = getInBytes(props.getProperty("allocated.size"));
    }

    private static long getInBytes(String size) {
        String prefix = size.substring(size.length() - 1);
        long suffix = Long.parseLong(size.substring(0, size.length() - 1));

        return switch (prefix) {
            case "T" -> suffix * 1024 * 1024 * 1024 * 1024;
            case "G" -> suffix * 1024 * 1024 * 1024;
            case "M" -> suffix * 1024 * 1024;
            case "K" -> suffix * 1024;
            case "B" -> suffix;
            default -> throw new IllegalArgumentException("Invalid datatype suffix in config");
        };
    }

    public static String getMode() {
        return mode;
    }

    public static String getPort() {
        return port;
    }

    public static String getDumpFile() {
        return dumpFile;
    }

    public static String[] getStorageServers() {
        return storageServers;
    }

    public static String getStoragePath() {
        return storagePath;
    }

    public static long getAllocatedBytes() {
        return allocatedBytes;
    }
}