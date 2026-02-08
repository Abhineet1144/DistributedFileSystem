package meta;

public class FileMeta {
    public static final FileMeta ROOT = new FileMeta("root", "folder", 0, 0L, "", "", null);

    private String fileName;
    private String type;
    private long size;
    private long date;
    private String mainOwner;
    private String storedPath;
    private FileMeta parent;

    public FileMeta(String fileName, String type, int size, long date, String mainOwner, String storedPath, FileMeta parent) {
        this.fileName = fileName;
        this.type = type;
        this.size = size;
        this.date = date;
        this.mainOwner = mainOwner;
        this.storedPath = storedPath;
        this.parent = parent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMainOwner() {
        return mainOwner;
    }

    public void setMainOwner(String mainOwner) {
        this.mainOwner = mainOwner;
    }

    public String getStoredPath() {
        return storedPath;
    }

    public void setStoredPath(String storedPath) {
        this.storedPath = storedPath;
    }

    public FileMeta getParent() {
        return parent;
    }

    public void setParent(FileMeta parent) {
        this.parent = parent;
    }
}
