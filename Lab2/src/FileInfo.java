import java.io.Serializable;

public class FileInfo implements Serializable {
    private final long fileSize;
    private final String fileName;

    public FileInfo(String fileName, long fileSize) {
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getFileNameLength(){
        return fileName.length();
    }
}
