package meta.handler;

import java.util.Collection;

import meta.FileMeta;

public abstract class AbstractFileHandlerMeta {
    public abstract void setFileMeta(FileMeta fileMeta);

    public abstract void updateFileMeta(FileMeta fileMeta);

    public abstract FileMeta getFileMeta(long ID);

    public abstract FileMeta getFileMeta(String fileName);

    public abstract void deleteFileMeta(long ID);

    public abstract FileMeta getFileMetaForAbsPath(String absPath);

    public abstract Collection<FileMeta> getChildren(FileMeta dir);

    public abstract void getFolderTree(FileMeta dir, StringBuilder fileTree);

    public static AbstractFileHandlerMeta getInstance() {
        return MapFileMetaHandler.getInst();
    }
}
