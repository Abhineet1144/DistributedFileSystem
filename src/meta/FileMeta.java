package meta;

public class FileMeta {
    public static final FileMeta ROOT = new FileMeta("root", FileType.FOLDER, 0, 0L,  null);

    private String fileName;
    private FileType type;
    private long size;
    private long date;
    private FileMeta parent;

    public FileMeta(String fileName, FileType type, int size, long date, FileMeta parent) {
        this.fileName = fileName;
        this.type = type;
        this.size = size;
        this.date = date;
        this.parent = parent;
    }

    public String getFileName() {
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
        return this.parent != null ? this.getParent().getFileName() + "/" + this.getFileName() + "/" : "";
    }
}
