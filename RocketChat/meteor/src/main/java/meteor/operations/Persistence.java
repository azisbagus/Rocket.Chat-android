package meteor.operations;

/**
 * Created by julio on 16/11/15.
 */
public interface Persistence {
    String getString(String key);

    void putString(String key, String value);

}
