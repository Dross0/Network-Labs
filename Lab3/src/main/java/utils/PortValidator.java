package utils;

public final class PortValidator{
    public static void validate(int port){
        if (port < 0){
            throw new IllegalArgumentException("Port must be positive, actual = {"
                    + port + "}");
        }
    }

    public static boolean isValid(int port){
        return port >= 0;
    }

    private PortValidator() {throw new AssertionError("Cant create port validator instance");}
}
