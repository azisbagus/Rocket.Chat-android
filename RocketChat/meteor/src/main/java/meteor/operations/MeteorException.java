package meteor.operations;

/**
 * Created by julio on 05/12/15.
 */
public class MeteorException extends Exception {
    private Protocol.Error error;

    public MeteorException(Protocol.Error error) {
        super();
        this.error = error;
    }

    public MeteorException(Exception e) {
        super(e);
        error = new Protocol.Error(e.toString(), e.getMessage(), e.getLocalizedMessage());
    }

    public Protocol.Error getError() {
        return error;
    }
}
