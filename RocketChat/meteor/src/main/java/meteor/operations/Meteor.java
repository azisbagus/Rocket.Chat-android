package meteor.operations;

/**
 * Copyright 2014 www.delight.im <info@delight.im>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.TimeUnit;

import okio.Buffer;
import timber.log.Timber;

/**
 * Client that connects to Meteor servers implementing the DDP protocol
 */
public class Meteor {

    /**
     * Supported versions of the DDP protocol in order of preference
     */
    public static final String[] SUPPORTED_DDP_VERSIONS = {"1", "pre2", "pre1"};

    /**
     * The maximum number of attempts to re-connect to the server over WebSocket
     */
    private static final int RECONNECT_ATTEMPTS_MAX = 5;
    /**
     * Instance of Gson library's ObjectMapper that converts between JSON and Java objects (POJOs)
     */
    private static final Gson sGson = new Gson();

    /**
     * The WebSocket connection that will be used for the data transfer
     */
    private WebSocket mConnection;
    /**
     * The callback that handles messages and events received from the WebSocket connection
     */
    private final WebSocketListener mWebSocketObserver;

    /**
     * Map that tracks all pending Listener instances
     */
    private final Map<String, Listener> mListeners;
    /**
     * Messages that couldn't be dispatched yet and thus had to be queued
     */
    private final Persistence mPersistence;
    private String mServerUri;
    private String mDdpVersion;
    /**
     * The number of unsuccessful attempts to re-connect in sequence
     */
    private int mReconnectAttempts;
    /**
     * The callback that will handle events and receive messages from this client
     */
    protected MeteorCallback mCallback;
    private String mSessionID;
    private boolean mConnecting;
    private String mLoggedInUserId;

    /**
     * Returns a new instance for a client connecting to a server via DDP over websocket
     * <p>
     * The server URI should usually be in the form of `ws://example.meteor.com/websocket`
     * or `wss://example.meteor.com/websocket`
     *
     * @param persistence a `Context` reference (e.g. an `Activity` or `Service` instance)
     * @param serverUri   the server URI to connect to
     */
    public Meteor(final Persistence persistence, final String serverUri) {
        this(persistence, serverUri, null);
    }

    /**
     * Returns a new instance for a client connecting to a server via DDP over websocket
     * <p>
     * The server URI should usually be in the form of `ws://example.meteor.com/websocket`
     * or `wss://example.meteor.com/websocket`
     *
     * @param persistence     a `Context` reference (e.g. an `Activity` or `Service` instance)
     * @param serverUri       the server URI to connect to
     * @param protocolVersion the desired DDP protocol version, default version if null given
     */
    public Meteor(final Persistence persistence, final String serverUri, String protocolVersion) {

        if (protocolVersion == null) {
            protocolVersion = SUPPORTED_DDP_VERSIONS[0];
        } else if (!isVersionSupported(protocolVersion)) {
            throw new RuntimeException("DDP protocol version not supported: " + protocolVersion);
        }

        if (persistence == null) {
            throw new RuntimeException("The Persistence reference may not be null");
        }

        // save the context reference
        this.mPersistence = persistence;

        mWebSocketObserver = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                mConnecting = false;
                mReconnectAttempts = 0;
                mConnection = webSocket;
                connect(mSessionID);
            }

            @Override
            public void onFailure(IOException e, Response response) {
                if (mCallback != null) {
                    mCallback.onException(e);
                }
            }

            @Override
            public void onMessage(ResponseBody message) throws IOException {
                handleMessage(message.string());
            }

            @Override
            public void onPong(Buffer payload) {

            }

            @Override
            public void onClose(int code, String reason) {
                if (code != CloseCode.NORMAL) {
                    mConnecting = false;
                    mConnection = null;
                    mReconnectAttempts++;
                    if (mReconnectAttempts <= RECONNECT_ATTEMPTS_MAX) {
                        // try to re-connect automatically
                        openConnection(false);
                    }
                } else {
                    mReconnectAttempts = 0;
                    mListeners.clear();
                    mSessionID = null;
                    mConnecting = false;
                }
                if (mCallback != null) {
                    mCallback.onDisconnect(code, reason);
                }
            }
        };


        // create a map that holds the pending Listener instances
        mListeners = new HashMap<String, Listener>();

        // save the server URI
        mServerUri = serverUri;
        // try with the preferred DDP protocol version first
        mDdpVersion = protocolVersion;
        // count the number of failed attempts to re-connect
        mReconnectAttempts = 0;
    }


    /**
     * Returns whether the given JSON result is from a previous login attempt
     *
     * @param result the JSON result
     * @return whether the result is from a login attempt (`true`) or not (`false`)
     */
    private static boolean isLoginResult(final JsonObject result) {
        return result.has(Protocol.Field.TOKEN) && result.has(Protocol.Field.ID);
    }

    /**
     * Returns whether the specified version of the DDP protocol is supported or not
     *
     * @param protocolVersion the DDP protocol version
     * @return whether the version is supported or not
     */
    public static boolean isVersionSupported(final String protocolVersion) {
        return Arrays.asList(SUPPORTED_DDP_VERSIONS).contains(protocolVersion);
    }

    /**
     * Creates and returns a new unique ID
     *
     * @return the new unique ID
     */
    public static String uniqueID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns whether this client is connected or not
     *
     * @return whether this client is connected
     */
    public boolean isConnected() {
        return mConnection != null;
    }

    /**
     * Manually attempt to re-connect if necessary
     */
    public void reconnect() {
        openConnection(true);
    }

    public boolean isConnecting() {
        return mConnecting;
    }

    /**
     * Opens a connection to the server over websocket
     *
     * @param isReconnect whether this is a re-connect attempt or not
     */
    public void openConnection(final boolean isReconnect) {

        if (isReconnect) {
            if (isConnected()) {
                connect(mSessionID);
                return;
            }
        }

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(1, TimeUnit.MINUTES);
        client.setReadTimeout(1, TimeUnit.MINUTES);
        client.setWriteTimeout(1, TimeUnit.MINUTES);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.networkInterceptors().add(loggingInterceptor);

        Request request = new Request.Builder()
                .url(mServerUri)
                .build();
        WebSocketCall.create(client, request).enqueue(mWebSocketObserver);
    }

    /**
     * Establish the connection to the server as requested by the DDP protocol (after the websocket has been opened)
     *
     * @param existingSessionID an existing session ID or `null`
     */
    private void connect(final String existingSessionID) {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put(Protocol.Field.MESSAGE, Protocol.Message.CONNECT);
        data.put(Protocol.Field.VERSION, mDdpVersion);
        data.put(Protocol.Field.SUPPORT, SUPPORTED_DDP_VERSIONS);
        if (existingSessionID != null) {
            data.put(Protocol.Field.SESSION, existingSessionID);
        }
        send("", data);
    }

    public void disconnect() {
        disconnect(CloseCode.NORMAL, "bye");
    }

    /**
     * Disconnect the client from the server
     */
    public void disconnect(int code, String reason) {
        try {
            if (mConnection != null) {
                mConnection.close(CloseCode.NORMAL, "Goodbye and thanks for the fishes!");
            }
            mConnection = null;
        } catch (Exception e) {
            if (mCallback != null) {
                mCallback.onException(e);
            }
        }
    }

    /**
     * Sends a Java object (POJO) over the websocket after serializing it with the Jackson library
     *
     * @param obj the Java object to send
     */
    private void send(String callId, final Object obj) {
        // serialize the object to JSON
        final String jsonStr = toJson(obj);

        if (jsonStr == null) {
            throw new RuntimeException("Object would be serialized to `null`");
        }

        // send the JSON string
        send(callId, jsonStr);
    }

    /**
     * Sends a string over the websocket
     *
     * @param message the string to send
     */
    private void send(String callId, final String message) {
        if (message == null) {
            throw new RuntimeException("You cannot send `null` messages");
        }

        try {
            Timber.d("-->" + message);
            synchronized (mConnection) {
                RequestBody request = RequestBody.create(WebSocket.TEXT, message);
                mConnection.sendMessage(request);
            }
        } catch (Exception e) {
            final Listener listener = mListeners.remove(callId);
            if (listener != null) {
                if (listener instanceof ResultListener) {
                    ((ResultListener) listener).onError(new MeteorException(e));
                }
            }
            if (mCallback != null) {
                mCallback.onException(e);
            }
        }
    }

    /**
     * Sets the callback that will handle events and receive messages from this client
     *
     * @param callback the callback instance
     */
    public void setCallback(MeteorCallback callback) {
        mCallback = callback;
    }

    /**
     * Serializes the given Java object (POJO) with the Jackson library
     *
     * @param obj the object to serialize
     * @return the serialized object in JSON format
     */
    private String toJson(Object obj) {
        try {
            return sGson.toJson(obj);
        } catch (Exception e) {
            if (mCallback != null) {
                mCallback.onException(e);
            }
            return null;
        }
    }

    /**
     * Called whenever a JSON payload has been received from the websocket
     *
     * @param payload the JSON payload to process
     */
    private void handleMessage(final String payload) {
        Timber.d("<--" + payload);
        JsonObject data = null;
        try {
            data = new JsonParser().parse(payload).getAsJsonObject();
        } catch (Exception e) {
            if (mCallback != null) {
                mCallback.onException(e);
            }
        }
        if (data != null) {
            if (data.has(Protocol.Field.MESSAGE)) {
                final String message = data.get(Protocol.Field.MESSAGE).getAsString();

                if (message.equals(Protocol.Message.CONNECTED)) {
                    if (data.has(Protocol.Field.SESSION)) {
                        mSessionID = data.get(Protocol.Field.SESSION).getAsString();
                    }

                    // initialize the new session
                    initSession();
                } else if (message.equals(Protocol.Message.FAILED)) {
                    if (data.has(Protocol.Field.VERSION)) {
                        final String desiredVersion = data.get(Protocol.Field.VERSION).getAsString();

                        if (isVersionSupported(desiredVersion)) {
                            mDdpVersion = desiredVersion;
                            openConnection(true);

                        } else {
                            throw new RuntimeException("Protocol version not supported: " + desiredVersion);
                        }
                    }
                } else if (message.equals(Protocol.Message.PING)) {
                    final String id;
                    if (data.has(Protocol.Field.ID)) {
                        id = data.get(Protocol.Field.ID).getAsString();
                    } else {
                        id = null;
                    }
                    sendPong(id);

                } else if (message.equals(Protocol.Message.ADDED) || message.equals(Protocol.Message.ADDED_BEFORE)) {
                    final String documentID;
                    if (data.has(Protocol.Field.ID)) {
                        documentID = data.get(Protocol.Field.ID).getAsString();
                    } else {
                        documentID = null;
                    }

                    final String collectionName;
                    if (data.has(Protocol.Field.COLLECTION)) {
                        collectionName = data.get(Protocol.Field.COLLECTION).getAsString();
                    } else {
                        collectionName = null;
                    }

                    final String newValuesJson;
                    if (data.has(Protocol.Field.FIELDS)) {
                        newValuesJson = data.get(Protocol.Field.FIELDS).toString();
                    } else {
                        newValuesJson = null;
                    }

                    if (mCallback != null) {
                        mCallback.onDataAdded(collectionName, documentID, newValuesJson);
                    }

                } else if (message.equals(Protocol.Message.CHANGED)) {
                    final String documentID;
                    if (data.has(Protocol.Field.ID)) {
                        documentID = data.get(Protocol.Field.ID).getAsString();
                    } else {
                        documentID = null;
                    }

                    final String collectionName;
                    if (data.has(Protocol.Field.COLLECTION)) {
                        collectionName = data.get(Protocol.Field.COLLECTION).getAsString();
                    } else {
                        collectionName = null;
                    }

                    final String updatedValuesJson;
                    if (data.has(Protocol.Field.FIELDS)) {
                        updatedValuesJson = data.get(Protocol.Field.FIELDS).toString();
                    } else {
                        updatedValuesJson = null;
                    }

                    final String removedValuesJson;
                    if (data.has(Protocol.Field.CLEARED)) {
                        removedValuesJson = data.get(Protocol.Field.CLEARED).toString();
                    } else {
                        removedValuesJson = null;
                    }


                    if (mCallback != null) {
                        mCallback.onDataChanged(collectionName, documentID, updatedValuesJson, removedValuesJson);
                    }

                } else if (message.equals(Protocol.Message.REMOVED)) {
                    final String documentID;
                    if (data.has(Protocol.Field.ID)) {
                        documentID = data.get(Protocol.Field.ID).getAsString();
                    } else {
                        documentID = null;
                    }

                    final String collectionName;
                    if (data.has(Protocol.Field.COLLECTION)) {
                        collectionName = data.get(Protocol.Field.COLLECTION).getAsString();
                    } else {
                        collectionName = null;
                    }

                    if (mCallback != null) {
                        mCallback.onDataRemoved(collectionName, documentID);
                    }

                } else if (message.equals(Protocol.Message.RESULT)) {
                    // check if we have to process any result data internally
                    if (data.has(Protocol.Field.RESULT)) {
                        JsonElement resultData = data.get(Protocol.Field.RESULT);

                        // if the result is from a previous login attempt
                        if (resultData.isJsonObject()) {
                            JsonObject result = resultData.getAsJsonObject();
                            if (isLoginResult(result)) {
                                // extract the login token for subsequent automatic re-login
                                final String loginToken = result.get(Protocol.Field.TOKEN).getAsString();
                                saveLoginToken(loginToken);

                                // extract the user's ID
                                mLoggedInUserId = result.get(Protocol.Field.ID).getAsString();
                            }
                        }
                    }

                    final String id;
                    if (data.has(Protocol.Field.ID)) {
                        id = data.get(Protocol.Field.ID).getAsString();
                    } else {
                        id = null;
                    }

                    final Listener listener = mListeners.get(id);

                    if (listener instanceof ResultListener) {
                        mListeners.remove(id);

                        final String result;
                        if (data.has(Protocol.Field.RESULT)) {
                            result = data.get(Protocol.Field.RESULT).toString();
                        } else {
                            result = null;
                        }

                        if (data.has(Protocol.Field.ERROR)) {
                            final Protocol.Error error = Protocol.Error.fromJson(data.get(Protocol.Field.ERROR).getAsJsonObject());
                            if (listener != null) {
                                ((ResultListener) listener).onError(new MeteorException(error));
                            }
                        } else {
                            if (listener != null) {
                                ((ResultListener) listener).onSuccess(result);
                            }
                        }
                    }
                } else if (message.equals(Protocol.Message.READY)) {
                    if (data.has(Protocol.Field.SUBS)) {
                        Iterator<JsonElement> elements = data.get(Protocol.Field.SUBS).getAsJsonArray().iterator();
                        String subscriptionId;
                        while (elements.hasNext()) {
                            subscriptionId = elements.next().getAsString();

                            final Listener listener = mListeners.get(subscriptionId);

                            if (listener instanceof ResultListener) {
                                mListeners.remove(subscriptionId);

                                if (listener != null) {
                                    ((ResultListener) listener).onSuccess("");
                                }
                            }
                        }
                    }
                } else if (message.equals(Protocol.Message.NOSUB)) {
                    final String subscriptionId;
                    if (data.has(Protocol.Field.ID)) {
                        subscriptionId = data.get(Protocol.Field.ID).getAsString();
                    } else {
                        subscriptionId = null;
                    }

                    final Listener listener = mListeners.get(subscriptionId);

                    if (listener instanceof ResultListener) {
                        mListeners.remove(subscriptionId);

                        if (data.has(Protocol.Field.ERROR)) {
                            final Protocol.Error error = Protocol.Error.fromJson(data.get(Protocol.Field.ERROR).getAsJsonObject());
                            if (listener != null) {
                                ((ResultListener) listener).onError(new MeteorException(error));
                            }
                        } else {
                            if (listener != null) {
                                ((ResultListener) listener).onError(new MeteorException(new UnknownFormatConversionException(data.toString())));
                            }
                        }
                    } else if (listener instanceof UnsubscribeListener) {
                        mListeners.remove(subscriptionId);

                        if (listener != null) {
                            ((UnsubscribeListener) listener).onSuccess();
                        }
                    }
                }
            }
        }
    }


    /**
     * Returns whether the client is currently logged in as some user
     *
     * @return whether the client is logged in (`true`) or not (`false`)
     */
    public boolean isLoggedIn() {
        return mLoggedInUserId != null;
    }

    /**
     * Returns the ID of the user who is currently logged in
     *
     * @return the ID or `null`
     */
    public String getUserId() {
        return mLoggedInUserId;
    }

    /**
     * Sends a `pong` over the websocket as a reply to an incoming `ping`
     *
     * @param id the ID extracted from the `ping` or `null`
     */
    private void sendPong(final String id) {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put(Protocol.Field.MESSAGE, Protocol.Message.PONG);
        if (id != null) {
            data.put(Protocol.Field.ID, id);
        }
        send("", data);
    }

    /**
     * Insert given data into the specified collection
     *
     * @param collectionName the collection to insert the data into
     * @param data           the data to insert
     */
    public void insert(final String collectionName, final Map<String, Object> data) {
        insert(collectionName, data, null);
    }

    /**
     * Insert given data into the specified collection
     *
     * @param collectionName the collection to insert the data into
     * @param data           the data to insert
     * @param listener       the listener to call on success/error
     */
    public void insert(final String collectionName, final Map<String, Object> data, final ResultListener listener) {
        call("/" + collectionName + "/insert", new Object[]{data}, listener);
    }

    /**
     * Insert given data into the specified collection
     *
     * @param collectionName the collection to insert the data into
     * @param data           the data to insert
     * @param randomSeed     an arbitrary seed for pseudo-random generators or `null`
     * @param listener       the listener to call on success/error
     */
    public void insert(final String collectionName, String randomSeed, final Map<String, Object> data, final ResultListener listener) {
        callWithSeed("/" + collectionName + "/insert", randomSeed, new Object[]{data}, listener);
    }

    /**
     * Insert given data into the specified collection
     *
     * @param collectionName the collection to insert the data into
     * @param query          the query to select the document to update with
     * @param data           the list of keys and values that should be set
     */
    public void update(final String collectionName, final Map<String, Object> query, final Map<String, Object> data) {
        update(collectionName, query, data, emptyMap());
    }

    /**
     * Insert given data into the specified collection
     *
     * @param collectionName the collection to insert the data into
     * @param query          the query to select the document to update with
     * @param data           the list of keys and values that should be set
     * @param options        the list of option parameters
     */
    public void update(final String collectionName, final Map<String, Object> query, final Map<String, Object> data, final Map<String, Object> options) {
        update(collectionName, query, data, options, null);
    }

    /**
     * Insert given data into the specified collection
     *
     * @param collectionName the collection to insert the data into
     * @param query          the query to select the document to update with
     * @param data           the list of keys and values that should be set
     * @param options        the list of option parameters
     * @param listener       the listener to call on success/error
     */
    public void update(final String collectionName, final Map<String, Object> query, final Map<String, Object> data, final Map<String, Object> options, final ResultListener listener) {
        call("/" + collectionName + "/update", new Object[]{query, data, options}, listener);
    }

    /**
     * Insert given data into the specified collection
     *
     * @param collectionName the collection to insert the data into
     * @param documentID     the ID of the document to remove
     */
    public void remove(final String collectionName, final String documentID) {
        remove(collectionName, documentID, null);
    }

    /**
     * Insert given data into the specified collection
     *
     * @param collectionName the collection to insert the data into
     * @param documentId     the ID of the document to remove
     * @param listener       the listener to call on success/error
     */
    public void remove(final String collectionName, final String documentId, final ResultListener listener) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(MongoDb.Field.ID, documentId);
        call("/" + collectionName + "/remove", new Object[]{query}, listener);
    }

    /**
     * Sign in the user with the given username and password
     * <p>
     * Please note that this requires the `accounts-password` package
     *
     * @param username the username to sign in with
     * @param password the password to sign in with
     * @param listener the listener to call on success/error
     */
    public void loginWithUsername(final String username, final String password, final ResultListener listener) {
        login(username, null, password, listener);
    }


    /**
     * Sign in the user with the given email address and password
     * <p>
     * Please note that this requires the `accounts-password` package
     *
     * @param email    the email address to sign in with
     * @param password the password to sign in with
     * @param listener the listener to call on success/error
     */
    public void loginWithEmail(final String email, final String password, final ResultListener listener) {
        login(null, email, password, listener);
    }

    /**
     * Sign in the user with the given username or email address and the specified password
     * <p>
     * Please note that this requires the `accounts-password` package
     *
     * @param username the username to sign in with (either this or `email` is required)
     * @param email    the email address to sign in with (either this or `username` is required)
     * @param password the password to sign in with
     * @param listener the listener to call on success/error
     */
    private void login(final String username, final String email, final String password, final ResultListener listener) {
        final Map<String, Object> userData = new HashMap<String, Object>();
        if (username != null) {
            userData.put("username", username);
        } else if (email != null) {
            userData.put("email", email);
        } else {
            throw new RuntimeException("You must provide either a username or an email address");
        }

        final Map<String, Object> authData = new HashMap<String, Object>();
        authData.put("user", userData);
        authData.put("password", password);

        call("login", new Object[]{authData}, listener);
    }

    /**
     * Attempts to sign in with the given login token
     *
     * @param token    the login token
     * @param listener the listener to call on success/error
     */
    private void loginWithToken(final String token, final ResultListener listener) {
        final Map<String, Object> authData = new HashMap<String, Object>();
        authData.put("resume", token);

        call("login", new Object[]{authData}, listener);
    }

    public void logout() {
        logout(null);
    }

    public void logout(final ResultListener listener) {
        call("logout", new Object[]{}, new ResultListener() {

            @Override
            public void onSuccess(final String result) {
                // remember that we're not logged in anymore
                mLoggedInUserId = null;

                // delete the last login token which is now invalid
                saveLoginToken(null);

                if (listener != null) {
                    listener.onSuccess(result);
                }
            }

            @Override
            public void onError(MeteorException e) {
                if (listener != null) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }

        });
    }


    /**
     * Registers a new user with the specified username, email address and password
     * <p>
     * This method will automatically login as the new user on success
     * <p>
     * Please note that this requires the `accounts-password` package
     *
     * @param username the username to register with (either this or `email` is required)
     * @param email    the email address to register with (either this or `username` is required)
     * @param password the password to register with
     * @param listener the listener to call on success/error
     */
    public void registerAndLogin(final String username, final String email, final String password, final ResultListener listener) {
        registerAndLogin(username, email, password, null, listener);
    }

    /**
     * Registers a new user with the specified username, email address and password
     * <p>
     * This method will automatically login as the new user on success
     * <p>
     * Please note that this requires the `accounts-password` package
     *
     * @param username the username to register with (either this or `email` is required)
     * @param email    the email address to register with (either this or `username` is required)
     * @param password the password to register with
     * @param profile  the user's profile data, typically including a `name` field
     * @param listener the listener to call on success/error
     */
    public void registerAndLogin(final String username, final String email, final String password, final HashMap<String, Object> profile, final ResultListener listener) {
        if (username == null && email == null) {
            throw new RuntimeException("You must provide either a username or an email address");
        }

        final Map<String, Object> accountData = new HashMap<String, Object>();
        if (username != null) {
            accountData.put("username", username);
        }
        if (email != null) {
            accountData.put("email", email);
        }
        accountData.put("password", password);
        if (profile != null) {
            accountData.put("profile", profile);
        }

        call("createUser", new Object[]{accountData}, listener);
    }

    /**
     * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
     *
     * @param methodName the name of the method to call, e.g. `/someCollection.insert`
     */
    public void call(final String methodName) {
        call(methodName, null, null);
    }

    /**
     * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
     *
     * @param methodName the name of the method to call, e.g. `/someCollection.insert`
     * @param params     the objects that should be passed to the method as parameters
     */
    public void call(final String methodName, final Object[] params) {
        call(methodName, params, null);
    }

    /**
     * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
     *
     * @param methodName the name of the method to call, e.g. `/someCollection.insert`
     * @param listener   the listener to trigger when the result has been received or `null`
     */
    public void call(final String methodName, final ResultListener listener) {
        call(methodName, null, listener);
    }

    /**
     * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
     *
     * @param methodName the name of the method to call, e.g. `/someCollection.insert`
     * @param params     the objects that should be passed to the method as parameters
     * @param listener   the listener to trigger when the result has been received or `null`
     */
    public void call(final String methodName, final Object[] params, final ResultListener listener) {
        callWithSeed(methodName, null, params, listener);
    }

    /**
     * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
     *
     * @param methodName the name of the method to call, e.g. `/someCollection.insert`
     * @param randomSeed an arbitrary seed for pseudo-random generators or `null`
     */
    public void callWithSeed(final String methodName, final String randomSeed) {
        callWithSeed(methodName, randomSeed, null, null);
    }

    /**
     * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
     *
     * @param methodName the name of the method to call, e.g. `/someCollection.insert`
     * @param randomSeed an arbitrary seed for pseudo-random generators or `null`
     * @param params     the objects that should be passed to the method as parameters
     */
    public void callWithSeed(final String methodName, final String randomSeed, final Object[] params) {
        callWithSeed(methodName, randomSeed, params, null);
    }

    /**
     * Executes a remote procedure call (any Java objects (POJOs) will be serialized to JSON by the Jackson library)
     *
     * @param methodName the name of the method to call, e.g. `/someCollection.insert`
     * @param randomSeed an arbitrary seed for pseudo-random generators or `null`
     * @param params     the objects that should be passed to the method as parameters
     * @param listener   the listener to trigger when the result has been received or `null`
     */
    public void callWithSeed(final String methodName, final String randomSeed, final Object[] params, final ResultListener listener) {
        // create a new unique ID for this request
        final String callId = uniqueID();

        // save a reference to the listener to be executed later
        if (listener != null) {
            mListeners.put(callId, listener);
        }

        // send the request
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put(Protocol.Field.MESSAGE, Protocol.Message.METHOD);
        data.put(Protocol.Field.METHOD, methodName);
        data.put(Protocol.Field.ID, callId);
        if (params != null) {
            data.put(Protocol.Field.PARAMS, params);
        }
        if (randomSeed != null) {
            data.put(Protocol.Field.RANDOM_SEED, randomSeed);
        }
        send(callId, data);
    }

    /**
     * Subscribes to a specific subscription from the server
     *
     * @param subscriptionName the name of the subscription
     * @return the generated subscription ID (must be used when unsubscribing)
     */
    public String subscribe(final String subscriptionName) {
        return subscribe(subscriptionName, null);
    }

    /**
     * Subscribes to a specific subscription from the server
     *
     * @param subscriptionName the name of the subscription
     * @param params           the subscription parameters
     * @return the generated subscription ID (must be used when unsubscribing)
     */
    public String subscribe(final String subscriptionName, final Object[] params) {
        return subscribe(subscriptionName, params, null);
    }

    /**
     * Subscribes to a specific subscription from the server
     *
     * @param subscriptionName the name of the subscription
     * @param params           the subscription parameters
     * @param listener         the listener to call on success/error
     * @return the generated subscription ID (must be used when unsubscribing)
     */
    public String subscribe(final String subscriptionName, final Object[] params, final ResultListener listener) {
        // create a new unique ID for this request
        final String subscriptionId = uniqueID();

        // save a reference to the listener to be executed later
        if (listener != null) {
            mListeners.put(subscriptionId, listener);
        }

        // send the request
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put(Protocol.Field.MESSAGE, Protocol.Message.SUBSCRIBE);
        data.put(Protocol.Field.NAME, subscriptionName);
        data.put(Protocol.Field.ID, subscriptionId);
        if (params != null) {
            data.put(Protocol.Field.PARAMS, params);
        }
        send(subscriptionId, data);

        // return the generated subscription ID
        return subscriptionId;
    }

    /**
     * Unsubscribes from the subscription with the specified name
     *
     * @param subscriptionId the ID of the subscription
     */
    public void unsubscribe(final String subscriptionId) {
        unsubscribe(subscriptionId, null);
    }

    /**
     * Unsubscribes from the subscription with the specified name
     *
     * @param subscriptionId the ID of the subscription
     * @param listener       the listener to call on success/error
     */
    public void unsubscribe(final String subscriptionId, final UnsubscribeListener listener) {
        // save a reference to the listener to be executed later
        if (listener != null) {
            mListeners.put(subscriptionId, listener);
        }

        // send the request
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put(Protocol.Field.MESSAGE, Protocol.Message.UNSUBSCRIBE);
        data.put(Protocol.Field.ID, subscriptionId);
        send(subscriptionId, data);
    }

    /**
     * Creates an empty map for use as default parameter
     *
     * @return an empty map
     */
    private static Map<String, Object> emptyMap() {
        return new HashMap<String, Object>();
    }

    /**
     * Saves the given login token to the preferences
     *
     * @param token the login token to save
     */
    private void saveLoginToken(final String token) {
        mPersistence.putString(Preferences.Keys.LOGIN_TOKEN, token);
    }

    /**
     * Retrieves the last login token from the preferences
     *
     * @return the last login token or `null`
     */
    private String getLoginToken() {
        return mPersistence.getString(Preferences.Keys.LOGIN_TOKEN);
    }


    private void initSession() {
        // get the last login token
        final String loginToken = getLoginToken();

        // if we found a login token that might work
        if (loginToken != null) {
            // try to sign in with that token
            loginWithToken(loginToken, new ResultListener() {

                @Override
                public void onSuccess(final String result) {
                    announceSessionReady(true);
                }

                @Override
                public void onError(MeteorException e) {
                    announceSessionReady(false);
                }

            });
        }
        // if we didn't find any login token
        else {
            announceSessionReady(false);
        }
    }

    /**
     * Announces that the new session is now ready to use
     *
     * @param signedInAutomatically whether we have already signed in automatically (`true`) or not (`false)`
     */
    private void announceSessionReady(final boolean signedInAutomatically) {
        // run the callback that waits for the connection to open
        if (mCallback != null) {
            mCallback.onConnect(signedInAutomatically);
        }
    }
}