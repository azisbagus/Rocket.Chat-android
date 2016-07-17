package chat.rocket.rc.listeners;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import meteor.operations.MeteorException;

/**
 * Created by julio on 19/11/15.
 */
abstract class TypedListener<T> extends LogListener {
    private static Gson mapper = new Gson();

    public abstract void onResult(T result);

    @Override
    public void onSuccess(String result) {
        super.onSuccess(result);
        if (result == null) {
            result = new JsonObject().toString();
        }
        Type[] listTypeArgs = ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments();

        try {
            if (listTypeArgs[0] instanceof Class) {
                onResult(mapper.fromJson(result, (Class<T>) listTypeArgs[0]));
            } else {
                Type listType = new TypeToken<T>() {
                }.getType();
                onResult((T) mapper.fromJson(result, listType));
            }

        } catch (Exception e) {
            onError(new MeteorException(e));
        }
    }
}
