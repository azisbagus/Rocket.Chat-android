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

import java.util.LinkedList;
import java.util.List;


/**
 * Provides a single access point to the `Meteor` class that can be used across `Activity` instances
 */
public class MeteorSingleton extends Meteor implements meteor.operations.MeteorCallback {

    private static final String TAG = "MeteorSingleton";
    private static MeteorSingleton mInstance;
    private final List<meteor.operations.MeteorCallback> mCallbacks = new LinkedList<meteor.operations.MeteorCallback>();

    private MeteorSingleton(final Persistence persistence, final String serverUri) {
        super(persistence, serverUri);
    }

    private MeteorSingleton(final Persistence persistence, final String serverUri, final String protocolVersion) {
        super(persistence, serverUri, protocolVersion);
    }

    public synchronized static MeteorSingleton createInstance(final Persistence persistence, final String serverUri) {
        return createInstance(persistence, serverUri, null);
    }

    public synchronized static MeteorSingleton createInstance(final Persistence persistence, final String serverUri, final String protocolVersion) {
        if (mInstance != null) {
            throw new RuntimeException("An instance has already been created");
        }

        if (protocolVersion == null) {
            mInstance = new MeteorSingleton(persistence, serverUri);
        } else {
            mInstance = new MeteorSingleton(persistence, serverUri, protocolVersion);
        }

        mInstance.mCallback = mInstance;

        return mInstance;
    }

    public synchronized static MeteorSingleton getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("Please call `createInstance(...)` first");
        }

        return mInstance;
    }

    public synchronized static boolean hasInstance() {
        return mInstance != null;
    }

    @Override
    public void setCallback(final meteor.operations.MeteorCallback callback) {
        if (callback != null) {
            mCallbacks.add(callback);
        }
    }

    public void unsetCallback(final meteor.operations.MeteorCallback callback) {
        if (callback != null) {
            mCallbacks.remove(callback);
        }
    }

    @Override
    public void onConnect(final boolean signedInAutomatically) {
        for (meteor.operations.MeteorCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onConnect(signedInAutomatically);
            }
        }
    }

    @Override
    public void onDisconnect(final int code, final String reason) {
        for (meteor.operations.MeteorCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onDisconnect(code, reason);
            }
        }
    }

    @Override
    public void onDataAdded(final String collectionName, final String documentID, final String newValuesJson) {
        for (meteor.operations.MeteorCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onDataAdded(collectionName, documentID, newValuesJson);
            }
        }
    }

    @Override
    public void onDataChanged(final String collectionName, final String documentID, final String updatedValuesJson, final String removedValuesJson) {
        for (meteor.operations.MeteorCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onDataChanged(collectionName, documentID, updatedValuesJson, removedValuesJson);
            }
        }
    }

    @Override
    public void onDataRemoved(final String collectionName, final String documentID) {
        for (meteor.operations.MeteorCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onDataRemoved(collectionName, documentID);
            }
        }
    }

    @Override
    public void onException(final Exception e) {
        for (meteor.operations.MeteorCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onException(e);
            }
        }
    }
}
