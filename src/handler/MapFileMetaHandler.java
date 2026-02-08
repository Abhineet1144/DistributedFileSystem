package handler;

import meta.FileMeta;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

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

    @Override
    public void deleteFileMeta(String fileName) {
        for (Entry<Long, FileMeta> mapElement : fileMetaMap.entrySet()) {
            if (mapElement.getValue().getFileName().equals(fileName)) {
                deleteFileMeta(mapElement.getKey());
            }
        }
    }
}
