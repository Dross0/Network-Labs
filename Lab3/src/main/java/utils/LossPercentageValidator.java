package utils;

public final class LossPercentageValidator {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    public static void validate(int lossPercentage){
        if (lossPercentage < MIN_VALUE || lossPercentage > MAX_VALUE){
            throw new IllegalArgumentException("Loss percentage not from valid interval = ["
                    + MIN_VALUE + ", "
                    + MAX_VALUE + "]");
        }
    }

    public static boolean isValid(int lossPercentage){
        return lossPercentage >= MIN_VALUE && lossPercentage <= MAX_VALUE;
    }

    public LossPercentageValidator() { throw new AssertionError("Cant create loss percentage validator instance");}
}
