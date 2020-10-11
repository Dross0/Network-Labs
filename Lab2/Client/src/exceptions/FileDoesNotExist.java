package exceptions;

public class FileDoesNotExist extends Exception {
    public FileDoesNotExist(String errorMessage) {
        super(errorMessage);
    }
}
