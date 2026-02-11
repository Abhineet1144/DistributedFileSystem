package meta.handler;

import meta.FileMeta;
import meta.FileType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import properties.Property;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class MapFileMetaHandler extends AbstractFileHandlerMeta implements Serializable {
    private static final long serialVersionUID = 1L;
    private static MapFileMetaHandler INSTANCE;
    private static volatile  boolean writing = false;

    private Map<Long, FileMeta> fileMetaMap;
    private long id;


    static {
        init();
    }

    private MapFileMetaHandler() {
    }

    public static MapFileMetaHandler getInst() {
        return INSTANCE;
    }

    private static void init() {
        boolean flag = initFromFile();
        if (!flag) {
            initDefault();
        }
    }

    private static boolean initFromFile() {
        try {
            FileInputStream in = new FileInputStream(Property.getDumpFile());
            if (in.available() == 0) {
                return false;
            }
            byte[] arr = IOUtils.toByteArray(in);
            INSTANCE = (MapFileMetaHandler) SerializationUtils.deserialize(arr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void initDefault() {
        INSTANCE = new MapFileMetaHandler();
        INSTANCE.fileMetaMap = new Hashtable<>();
        INSTANCE.fileMetaMap.put(1L, FileMeta.ROOT);
        INSTANCE.id = 2L;
    }

    private void backUp() {
        if (writing) {
            return;
        }
        writing = true;
        byte[] arr = SerializationUtils.serialize(this);
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(Property.getDumpFile()))) {
            out.write(arr);
        } catch (Exception e) {
            System.out.println("Error writing dump file:");
            e.printStackTrace();
        } finally {
            writing = false;
        }
    }

    @Override
    public void setFileMeta(FileMeta fileMeta) {
        fileMetaMap.put(id, fileMeta);
        fileMeta.setId(id);
        id++;
        backUp();
    }

    @Override
    public void updateFileMeta(FileMeta fileMeta) {
        fileMetaMap.put(fileMeta.getId(), fileMeta);
        backUp();
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
        backUp();
    }

    @Override
    public FileMeta getFileMetaForAbsPath(String absPath) {
        for (FileMeta meta : fileMetaMap.values()) {
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
            if (meta.getParent() == null) {
                continue;
            } else if (meta.getParent().equals(dir)) {
                children.add(meta);
            }
        }
        return children;
    }

    @Override
    public void getFolderTree(FileMeta dir, StringBuilder fileTree) {
        for (FileMeta child : getChildren(dir)) {
            fileTree.append(child.getAbsolutePath()).append("\n");
            if (child.getType().equals(FileType.FOLDER)) {
                getFolderTree(child, fileTree);
            }
        }
    }
}
