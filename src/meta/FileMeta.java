package meta;

import java.util.Collection;

public class FileMeta {
    public static final FileMeta ROOT = new FileMeta("root", FileType.FOLDER, 0, 0L, null, "");
    static {
        ROOT.setId(1);
        System.out.println(ROOT.getAbsolutePath());
    }

    private String fileName;
    private FileType type;
    private long size;
    private long date;
    private FileMeta parent;
    private long id;
    private String ipport;

    public FileMeta(String fileName, FileType type, long size, long date, FileMeta parent, String ipport) {
        this.fileName = fileName;
        this.type = type;
        this.size = size;
        this.date = date;
        this.parent = parent;
        this.ipport = ipport;
    }

    public String getFileName()  {

        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void increaseSize(long size) {
        this.size += size;
        if (parent != null) {
            parent.increaseSize(size);
        }
    }

    public void decreaseSize(long size) {
        this.size += size;
        if (parent != null) {
            parent.decreaseSize(size);
        }
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public FileMeta getParent() {
        return parent;
    }

    public void setParent(FileMeta parent) {
        this.parent = parent;
    }

    public String getAbsolutePath() {
        return this.parent != null ? this.getParent().getAbsolutePath() + "/" + this.getFileName() + "/" : "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIpport() {
        return ipport;
    }

    public void setIpport(String ipport) {
        this.ipport = ipport;
    }
}
