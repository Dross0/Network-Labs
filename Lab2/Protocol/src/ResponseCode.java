import exceptions.UnknownResponseCode;

public enum ResponseCode {
    SUCCESS_FILENAME_TRANSFER(201),
    SUCCESS_FILE_TRANSFER(202),
    FAILURE_FILENAME_TRANSFER(101),
    FAILURE_FILE_TRANSFER(102);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ResponseCode getResponseByCode(int code) throws UnknownResponseCode {
        for (ResponseCode responseCode: ResponseCode.values()){
            if (responseCode.code == code){
                return responseCode;
            }
        }
        throw new UnknownResponseCode("No response code = " + code);
    }
}
