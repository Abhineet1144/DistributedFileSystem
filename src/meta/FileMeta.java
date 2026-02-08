package meta;

public class FileMeta {
    private String fileName;
    private String type;
    private int size;
    private String date;
    private String mainOwner;
    private String storedPath;

    public FileMeta(String fileName, String type, int size, String date, String mainOwner, String storedPath) {
        this.fileName = fileName;
        this.type = type;
        this.size = size;
        this.date = date;
        this.mainOwner = mainOwner;
        this.storedPath = storedPath;
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

    public int getSize() {
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
}
