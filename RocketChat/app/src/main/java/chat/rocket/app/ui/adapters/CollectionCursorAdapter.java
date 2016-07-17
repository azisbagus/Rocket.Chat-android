package chat.rocket.app.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import chat.rocket.app.db.dao.CollectionDAO;
import chat.rocket.app.utils.Util;


/**
 * Created by julio on 29/11/15.
 */
public abstract class CollectionCursorAdapter<T> extends CursorAdapter {
    private LruCache<String, T> mCache = new LruCache<>(5);

    public CollectionCursorAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return newView(context, getItem(cursor), parent);
    }

    @Override
    public T getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return getItem(cursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        bindView(view, context, getItem(cursor));
    }

    public abstract View newView(Context context, T item, ViewGroup parent);


    public abstract void bindView(View view, Context context, T item);


    private T getItem(Cursor cursor) {
        String docId = cursor.getString(cursor.getColumnIndex(CollectionDAO.COLUMN_DOCUMENT_ID));
        T item = mCache.get(docId);
        if (item != null) {
            return item;
        }
        String result = cursor.getString(cursor.getColumnIndex(CollectionDAO.COLUMN_FIELDS));
        if (result == null) {
            result = new JSONObject().toString();
        }
        Type[] listTypeArgs = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

        if (listTypeArgs[0] instanceof Class) {
            item = Util.GSON.fromJson(result, (Class<T>) listTypeArgs[0]);
        } else {
            Type listType = new TypeToken<T>() {
            }.getType();
            item = Util.GSON.fromJson(result, listType);
        }
        mCache.put(docId, item);
        return item;
    }
}
