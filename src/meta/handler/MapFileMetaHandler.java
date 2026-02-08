package meta.handler;

import meta.FileMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class MapFileMetaHandler extends AbstractFileHandlerMeta {
    private static final MapFileMetaHandler INSTANCE = new MapFileMetaHandler();

    private static Map<Long, FileMeta> fileMetaMap;
    private static long id;

    private MapFileMetaHandler() {
        init();
    }

    public static MapFileMetaHandler getInst() {
        return INSTANCE;
    }

    public void init() {
        fileMetaMap = new Hashtable<>();
        fileMetaMap.put(1L, FileMeta.ROOT);
        id = 2L;
    }

    @Override
    public void setFileMeta(FileMeta fileMeta) {
        fileMetaMap.put(id, fileMeta);
        fileMeta.setId(id);
        id++;
    }

    @Override
    public void updateFileMeta(FileMeta fileMeta) {
        fileMetaMap.put(fileMeta.getId(), fileMeta);
    }

    @Override
    public FileMeta getFileMeta(long id) {
        return fileMetaMap.getOrDefault(id, null);
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
    public void deleteFileMeta(long id) {
        fileMetaMap.remove(id);
    }

    @Override
    public FileMeta getFileMetaForAbsPath(String absPath) {
        for (FileMeta meta : fileMetaMap.values()) {
            System.out.println(meta.getAbsolutePath());
            if (absPath.equals(meta.getAbsolutePath())) {
                return meta;
            }
        }
        return null;
    }

    @Override
    public Collection<FileMeta> getChildren(FileMeta dir) {
        Collection<FileMeta> children = new ArrayList<>();
        for (FileMeta meta : fileMetaMap.values()) {
            if (meta.equals(FileMeta.ROOT)) {
                continue;
            }
            if (meta.getParent().equals(dir)) {
                children.add(meta);
            }
        }
        return children;
    }
}
