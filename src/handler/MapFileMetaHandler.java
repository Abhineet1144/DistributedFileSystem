package handler;

import meta.FileMeta;

import java.util.Hashtable;
import java.util.Map;

public class MapFileMetaHandler implements AbstractFileHandlerMeta {
    Map<Long, FileMeta> fileMetaMap;
    private static long ID;

    public void Init() {
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
        for (Map.Entry mapElement : fileMetaMap.entrySet()) {
            if (mapElement.getValue().equals(fileName)) {
                return (FileMeta) mapElement.getValue();
            }
        }
        return null;
    }

    @Override
    public void deleteFileMeta(long ID) {
        fileMetaMap.remove(ID);
    }

    @Override
    public void deleteFileMeta(String fileName) {
        for (Map.Entry mapElement : fileMetaMap.entrySet()) {
            if (mapElement.getValue().equals(fileName)) {
                deleteFileMeta((Long) mapElement.getKey());
            }
        }
    }
}
