package rxmeteor.operations;


import android.util.Pair;

import meteor.operations.Meteor;
import meteor.operations.MeteorCallback;
import meteor.operations.Persistence;
import rx.Observable;
import rx.functions.Action1;

import static rxmeteor.operations.DataEvent.DataType.ADD;
import static rxmeteor.operations.DataEvent.DataType.CHANGE;
import static rxmeteor.operations.DataEvent.DataType.REMOVE;

/**
 * Created by julio on 05/12/15.
 */
public class RxMeteor {
    private final Meteor mMeteor;
    private final Persistence mPersistence = new Persistence() {
        @Override
        public String getString(String key) {
            return null;
        }

        @Override
        public void putString(String key, String value) {

        }
    };
    private Action1<Boolean> mConnectObserver;
    private Action1<Pair<Integer, String>> mDisconnectObserver;

    public RxMeteor(Meteor meteor) {
        mMeteor = meteor;
    }

    public RxMeteor setOnConnectObserver(Action1<Boolean> connectObserver) {
        mConnectObserver = connectObserver;
        return this;
    }

    public RxMeteor setOnDisconnectObserver(Action1<Pair<Integer, String>> disconnectObserver) {
        mDisconnectObserver = disconnectObserver;
        return this;
    }

    public Observable<DataEvent> create() {
        return Observable.create((Observable.OnSubscribe<DataEvent>) subscriber -> mMeteor.setCallback(new MeteorCallback() {
            @Override
            public void onConnect(boolean signedInAutomatically) {
                if (mConnectObserver != null && !subscriber.isUnsubscribed())
                    mConnectObserver.call(signedInAutomatically);
            }

            @Override
            public void onDisconnect(int code, String reason) {
                if (mDisconnectObserver != null && !subscriber.isUnsubscribed())
                    mDisconnectObserver.call(new Pair<Integer, String>(code, reason));
            }

            @Override
            public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(new DataEvent(ADD, collectionName, documentID, newValuesJson, null));
            }

            @Override
            public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(new DataEvent(CHANGE, collectionName, documentID, updatedValuesJson, removedValuesJson));
            }

            @Override
            public void onDataRemoved(String collectionName, String documentID) {
                if (!subscriber.isUnsubscribed())
                    subscriber.onNext(new DataEvent(REMOVE, collectionName, documentID, null, null));
            }

            @Override
            public void onException(Exception e) {
                if (!subscriber.isUnsubscribed())
                    subscriber.onError(e);
            }
        }));
    }


    public boolean isConnected() {
        return mMeteor.isConnected();
    }

    public boolean isConnecting() {
        return mMeteor.isConnecting();
    }

    public Observable<Void> disconnect() {
        return Observable.create((Observable.OnSubscribe<Void>) subscriber -> {
            try {
                mMeteor.disconnect();
                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public void reconnect() {
        mMeteor.reconnect();
    }

    public String getUserId() {
        return mMeteor.getUserId();
    }

    public boolean isLoggedIn() {
        return mMeteor.isLoggedIn();
    }
}
