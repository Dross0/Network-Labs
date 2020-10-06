package exceptions;

public class UnknownResponseCode extends Exception {
    public UnknownResponseCode(String message){
        super(message);
    }
}
