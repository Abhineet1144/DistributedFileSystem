package meta.handler;

import meta.FileMeta;

public interface AbstractFileHandlerMeta {
    void setFileMeta(FileMeta fileMeta);
    FileMeta getFileMeta(long ID);
    FileMeta getFileMeta(String fileName);
    void deleteFileMeta(long ID);
}
