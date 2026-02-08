package meta.handler;

import meta.FileMeta;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class MapFileMetaHandler implements AbstractFileHandlerMeta {
    private static final MapFileMetaHandler INSTANCE = new MapFileMetaHandler();

    private static Map<Long, FileMeta> fileMetaMap;
    private static long ID;

    public static MapFileMetaHandler getInstance() {
        return INSTANCE;
    }

    public void init() {
        fileMetaMap = new Hashtable<>();
        ID = 0;
    }

    @Override
    public void setFileMeta(FileMeta fileMeta) {
        fileMetaMap.put(ID, fileMeta);
        ID++;
    }

    @Override
    public FileMeta getFileMeta(long ID) {
        return fileMetaMap.getOrDefault(ID, null);
    }

    @Override
    public FileMeta getFileMeta(String fileName) {
        for (Entry<Long, FileMeta> mapElement : fileMetaMap.entrySet()) {
            if (mapElement.getValue().getFileName().equals(fileName)) {
                return mapElement.getValue();
            }
        }
        return null;
    }

    @Override
    public void deleteFileMeta(long ID) {
        fileMetaMap.remove(ID);
    }
}
