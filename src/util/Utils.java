package util;

public class Utils {
    public static String getCleansedPath(String path) {
        return path;
    }

    public static String findPathFromIPs(String[] ipPorts, String[] paths, String target) {
        if (ipPorts == null) {
            return null;
        }
        for (int i = 0; i < ipPorts.length; i++) {
            if (ipPorts[i].equals(target)) {
                return paths[i];
            }
        }
        return null;
    }

}
