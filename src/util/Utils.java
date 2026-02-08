package util;

import java.io.File;

public class Utils {
    public static String getCleansedPath(String path) {
        return new File(path).getPath();
    }
}
