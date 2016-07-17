package chat.rocket.app.db.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class AsyncQueryHandler<T> extends Handler {
    private static final int EVENT_ARG_QUERY = 1;
    private static final int EVENT_ARG_INSERT = 2;
    private static final int EVENT_ARG_UPDATE = 3;
    private static final int EVENT_ARG_DELETE = 4;
    private static final int EVENT_ARG_BULK_INSERT = 5;
    private static Looper sLooper = null;
    private final WeakReference<ContentResolver> mResolver;
    private Handler mWorkerThreadHandler;

    public AsyncQueryHandler(ContentResolver cr) {
        super();
        mResolver = new WeakReference<ContentResolver>(cr);
        synchronized (AsyncQueryHandler.class) {
            if (sLooper == null) {
                HandlerThread thread = new HandlerThread("AsyncQueryWorker");
                thread.start();

                sLooper = thread.getLooper();
            }
        }
        mWorkerThreadHandler = createHandler(sLooper);
    }

    protected Handler createHandler(Looper looper) {
        return new WorkerHandler(looper);
    }

    /**
     * This method begins an asynchronous query. When the query is done
     * {@link #onQueryComplete} is called.
     *
     * @param token         A token passed into {@link #onQueryComplete} to identify the
     *                      query.
     * @param cookie        An object that gets passed into {@link #onQueryComplete}
     * @param uri           The URI, using the content:// scheme, for the content to
     *                      retrieve.
     * @param projection    A listUserCards of which columns to return. Passing null will return
     *                      all columns, which is discouraged to prevent reading data from
     *                      storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an SQL
     *                      WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the
     *                      values from selectionArgs, in the order that they appear in
     *                      the selection. The values will be bound as Strings.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     */
    public void startQuery(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_QUERY;

        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.uri = uri;
        args.projection = projection;
        args.selection = selection;
        args.selectionArgs = selectionArgs;
        args.orderBy = orderBy;
        args.cookie = cookie;
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    /**
     * Attempts to cancel operation that has not already started. Note that
     * there is no guarantee that the operation will be canceled. They still may
     * result in a call to on[Query/Insert/Update/Delete]Complete after this
     * call has completed.
     *
     * @param token The token representing the operation to be canceled. If
     *              multiple operations have the same token they will all be
     *              canceled.
     */
    public final void cancelOperation(int token) {
        mWorkerThreadHandler.removeMessages(token);
    }

    /**
     * This method begins an asynchronous insertOrReplace. When the insertOrReplace operation is
     * done {@link #onInsertComplete} is called.
     *
     * @param token         A token passed into {@link #onInsertComplete} to identify the
     *                      insertOrReplace operation.
     * @param cookie        An object that gets passed into {@link #onInsertComplete}
     * @param uri           the Uri passed to the insertOrReplace operation.
     * @param initialValues the ContentValues parameter passed to the insertOrReplace operation.
     */
    public final void startInsert(int token, Object cookie, Uri uri, ContentValuables initialValues) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_INSERT;

        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.uri = uri;
        args.cookie = cookie;
        args.values = new ContentValuables[]{initialValues};
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    public final void startBulkInsert(int token, Object cookie, Uri uri, ContentValuables[] initialValues) {
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_BULK_INSERT;

        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.uri = uri;
        args.cookie = cookie;
        args.values = initialValues;
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    /**
     * This method begins an asynchronous update. When the update operation is
     * done {@link #onUpdateComplete} is called.
     *
     * @param token  A token passed into {@link #onUpdateComplete} to identify the
     *               update operation.
     * @param cookie An object that gets passed into {@link #onUpdateComplete}
     * @param uri    the Uri passed to the update operation.
     * @param values the ContentValues parameter passed to the update operation.
     */
    public final void startUpdate(int token, Object cookie, Uri uri, ContentValuables values, String selection, String[] selectionArgs) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_UPDATE;

        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.uri = uri;
        args.cookie = cookie;
        args.values = new ContentValuables[]{values};
        args.selection = selection;
        args.selectionArgs = selectionArgs;
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    /**
     * This method begins an asynchronous delete. When the delete operation is
     * done {@link #onDeleteComplete} is called.
     *
     * @param token     A token passed into {@link #onDeleteComplete} to identify the
     *                  delete operation.
     * @param cookie    An object that gets passed into {@link #onDeleteComplete}
     * @param uri       the Uri passed to the delete operation.
     * @param selection the where clause.
     */
    public final void startDelete(int token, Object cookie, Uri uri, String selection, String[] selectionArgs) {
        // Use the token as what so cancelOperations works properly
        Message msg = mWorkerThreadHandler.obtainMessage(token);
        msg.arg1 = EVENT_ARG_DELETE;

        WorkerArgs args = new WorkerArgs();
        args.handler = this;
        args.uri = uri;
        args.cookie = cookie;
        args.selection = selection;
        args.selectionArgs = selectionArgs;
        msg.obj = args;

        mWorkerThreadHandler.sendMessage(msg);
    }

    /**
     * Called when an asynchronous query is completed.
     *
     * @param token  the token to identify the query, passed in from
     *               {@link #startQuery}.
     * @param cookie the cookie object passed in from {@link #startQuery}.
     * @param object The cursor holding the results from the query.
     */
    protected void onQueryComplete(int token, Object cookie, T object) {
    }

    /**
     * Called when an asynchronous insertOrReplace is completed.
     *
     * @param token  the token to identify the query, passed in from
     *               {@link #startInsert}.
     * @param cookie the cookie object that's passed in from {@link #startInsert}.
     * @param uri    the uri returned from the insertOrReplace operation.
     */
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
    }

    protected void onBulkInsertComplete(int token, Object cookie, int result) {
    }

    /**
     * Called when an asynchronous update is completed.
     *
     * @param token  the token to identify the query, passed in from
     *               {@link #startUpdate}.
     * @param cookie the cookie object that's passed in from {@link #startUpdate}.
     * @param result the result returned from the update operation
     */
    protected void onUpdateComplete(int token, Object cookie, int result) {
    }

    /**
     * Called when an asynchronous delete is completed.
     *
     * @param token  the token to identify the query, passed in from
     *               {@link #startDelete}.
     * @param cookie the cookie object that's passed in from {@link #startDelete}.
     * @param result the result returned from the delete operation
     */
    protected void onDeleteComplete(int token, Object cookie, int result) {
    }

    @Override
    public void handleMessage(Message msg) {
        WorkerArgs args = (WorkerArgs) msg.obj;
        int token = msg.what;
        int event = msg.arg1;

        // pass token back to caller on each callback.
        switch (event) {
            case EVENT_ARG_QUERY:
                onQueryComplete(token, args.cookie, (T) args.result);
                break;

            case EVENT_ARG_INSERT:
                onInsertComplete(token, args.cookie, (Uri) args.result);
                break;

            case EVENT_ARG_BULK_INSERT:
                onBulkInsertComplete(token, args.cookie, (Integer) args.result);
                break;

            case EVENT_ARG_UPDATE:
                onUpdateComplete(token, args.cookie, (Integer) args.result);
                break;

            case EVENT_ARG_DELETE:
                onDeleteComplete(token, args.cookie, (Integer) args.result);
                break;
        }
    }


    protected static final class WorkerArgs {
        public Uri uri;
        public Handler handler;
        public String[] projection;
        public String selection;
        public String[] selectionArgs;
        public String orderBy;
        public Object result;
        public Object cookie;
        public ContentValuables[] values;
    }

    protected class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            final ContentResolver resolver = mResolver.get();
            if (resolver == null)
                return;

            WorkerArgs args = (WorkerArgs) msg.obj;

            int token = msg.what;
            int event = msg.arg1;

            switch (event) {
                case EVENT_ARG_QUERY:
                    Cursor cursor;
                    try {
                        cursor = resolver.query(args.uri, args.projection, args.selection, args.selectionArgs, args.orderBy);
                        // Calling getCount() causes the cursor window to be filled,
                        // which will make the first access on the main thread a lot
                        // faster.
                        if (cursor != null) {
                            cursor.getCount();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        cursor = null;
                    }

                    args.result = cursor;
                    break;

                case EVENT_ARG_INSERT:
                    args.result = resolver.insert(args.uri, args.values[0].toContentValues());
                    break;

                case EVENT_ARG_BULK_INSERT:
                    args.result = resolver.bulkInsert(args.uri, convertToContentValues(args.values));
                    break;

                case EVENT_ARG_UPDATE:
                    args.result = resolver.update(args.uri, args.values[0].toContentValues(), args.selection, args.selectionArgs);
                    break;

                case EVENT_ARG_DELETE:
                    args.result = resolver.delete(args.uri, args.selection, args.selectionArgs);
                    break;
            }

            // passing the original token value back to the caller
            // on top of the event values in arg1.
            Message reply = args.handler.obtainMessage(token);
            reply.obj = args;
            reply.arg1 = msg.arg1;
            reply.sendToTarget();
        }

        private ContentValues[] convertToContentValues(ContentValuables[] values) {
            ContentValues[] contentValues = new ContentValues[values.length];
            for (int i = 0; i < values.length; ++i) {
                ContentValuables valuables = values[i];
                contentValues[i] = valuables.toContentValues();
            }
            return contentValues;
        }
    }
}