package timber.log;

/**
 * Created by julio on 05/12/15.
 */
public abstract class TLog {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int ASSERT = 6;

    public abstract void wtf(String tag, String message);

    public abstract void println(int priority, String tag, String message);
}
