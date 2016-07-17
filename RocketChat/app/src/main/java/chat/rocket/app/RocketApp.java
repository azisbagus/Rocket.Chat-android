package chat.rocket.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import javax.inject.Inject;

import chat.rocket.app.db.DBManager;
import chat.rocket.app.db.collections.LoginServiceConfiguration;
import chat.rocket.app.db.collections.StreamMessages;
import chat.rocket.app.db.collections.StreamNotifyRoom;
import chat.rocket.app.db.dao.CollectionDAO;
import chat.rocket.app.db.dao.MessageDAO;
import chat.rocket.app.db.dao.RcSubscriptionDAO;
import chat.rocket.app.di.modules.AppModule;
import chat.rocket.app.di.modules.RocketModule;
import chat.rocket.app.di.components.AppComponent;
import chat.rocket.app.di.components.DaggerAppComponent;
import chat.rocket.app.utils.Util;
import chat.rocket.models.NotifyRoom;
import chat.rocket.rc.enumerations.LoginService;
import chat.rocket.rc.enumerations.NotifyActionType;
import chat.rocket.rxrc.RxRocketMethods;
import chat.rocket.rxrc.RxRocketSubscriptions;
import io.fabric.sdk.android.Fabric;
import meteor.operations.Meteor;
import meteor.operations.MeteorException;
import meteor.operations.Persistence;
import meteor.operations.Protocol;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rxmeteor.operations.RxMeteor;
import timber.log.TLog;
import timber.log.Timber;


/**
 * Created by julio on 16/11/15.
 */
public class RocketApp extends Application implements Persistence {
    public static final String ACTION_DISCONNECTED = BuildConfig.APPLICATION_ID + ".METEOR.DISCONNECTED";
    public static final String ACTION_CONNECTED = BuildConfig.APPLICATION_ID + ".METEOR.CONNECTED";
    public static final String LOGGED_KEY = "logged";

    private Subscription mConnectionSubscription;

    //TODO: At some point It will be moved to a "ServersManager" where each connected server will have its own instances
    //TODO: @ServerScope ??
    //TODO: @SessionScope ??
    //TODO: ServerScope > SessionScope!
    @Inject
    RxMeteor mRxMeteor;
    @Inject
    Meteor mMeteor;
    @Inject
    RxRocketMethods mRxRocketMethods;
    @Inject
    RxRocketSubscriptions mRxRocketSubscriptions;

    private AppComponent mAppComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        setupTimber();
        // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
        if (!TextUtils.isEmpty(BuildConfig.TWITTER_KEY)) {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
            Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        } else {
            Fabric.with(this, new Crashlytics());
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        DBManager.getInstance().init(this);

        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

        String url = BuildConfig.WS_PROTOCOL + "://" + BuildConfig.WS_HOST + BuildConfig.WS_PATH;
        mAppComponent.plus(new RocketModule(url)).inject(this);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    private void setupTimber() {
        Timber.plant(new Timber.DebugTree(), new TLog() {
            @Override
            public void wtf(String tag, String message) {
                Log.wtf(tag, message);
            }

            @Override
            public void println(int priority, String tag, String message) {
                Log.println(priority, tag, message);
            }
        });
    }

    public static RocketApp get(Context context) {
        return (RocketApp) context.getApplicationContext();
    }

    public void connect() {
        mConnectionSubscription = mRxMeteor
                .setOnConnectObserver(signedInAutomatically -> onConnect(signedInAutomatically))
                .setOnDisconnectObserver(pair -> {
                    onDisconnect(pair.first, pair.second);
                    mConnectionSubscription.unsubscribe();
                })
                .create()
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    switch (data.getType()) {
                        case ADD:
                            onDataAdded(data.getCollectionName(), data.getDocumentID(), data.getNewValuesJson());
                            break;
                        case CHANGE:
                            onDataChanged(data.getCollectionName(), data.getDocumentID(), data.getNewValuesJson(), data.getRemovedValuesJson());
                            break;
                        case REMOVE:
                            onDataRemoved(data.getCollectionName(), data.getDocumentID());
                            break;
                    }
                }, throwable -> {
                    onException(throwable);
                    mRxMeteor.disconnect()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Void>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Timber.e(e, "disconnect() - onError");
                                }

                                @Override
                                public void onNext(Void aVoid) {
                                    Timber.d("disconnect() - onNext");
                                }
                            });
                });

        mRxMeteor.reconnect();
    }

    public void onConnect(boolean signedInAutomatically) {

        mRxRocketSubscriptions.loginServiceConfiguration()
                .flatMap(aVoid -> mRxRocketSubscriptions.settings())
                .flatMap(aVoid0 -> mRxRocketSubscriptions.streamNotifyRoom())
                .flatMap(aVoid1 -> mRxRocketSubscriptions.streamNotifyAll())
                .flatMap(aVoid2 -> mRxRocketSubscriptions.streamNotifyUser())
                .flatMap(aVoid3 -> mRxRocketSubscriptions.roles())
                .flatMap(aVoid4 -> mRxRocketSubscriptions.permissions())
                .flatMap(aVoid5 -> mRxRocketSubscriptions.streamMessages())
                .flatMap(aVoid6 -> mRxRocketSubscriptions.meteorAutoupdateClientVersions())
                .flatMap(aVoid7 -> mRxRocketSubscriptions.subscription())
                .flatMap(aVoid8 -> mRxRocketSubscriptions.userData())
                .flatMap(aVoid9 -> mRxRocketSubscriptions.activeUsers())
                .flatMap(aVoid10 -> mRxRocketSubscriptions.adminSettings())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Protocol.Error err = ((MeteorException) e).getError();
                        String error = err.getError();
                        String reason = err.getReason();
                        String details = err.getDetails();
                        Timber.d(error + ", " + reason + ", " + details);
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        String appId = LoginServiceConfiguration.query(LoginService.FACEBOOK);
                        Timber.d("appId: " + appId);
                        if (!TextUtils.isEmpty(appId)) {
                            FacebookSdk.setApplicationId(appId);
                        }
                        Intent intent = new Intent();
                        intent.setAction(ACTION_CONNECTED);
                        intent.putExtra(LOGGED_KEY, signedInAutomatically);
                        LocalBroadcastManager.getInstance(RocketApp.this).sendBroadcast(intent);
                    }
                });


    }

    public void onDisconnect(int code, String reason) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DISCONNECTED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Timber.d("Disconnect code:" + code + ", reason: " + reason);
    }

    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
        switch (collectionName) {
            case StreamMessages.COLLECTION_NAME:
                addStreamMessages(documentID, newValuesJson);
                break;
            case RcSubscriptionDAO.COLLECTION_NAME:
                addRcSubscription(documentID, newValuesJson);
                break;
            case StreamNotifyRoom.COLLECTION_NAME:
                Observable.create((OnSubscribe<StreamNotifyRoom>) subscriber -> {
                    StreamNotifyRoom stream = Util.GSON.fromJson(newValuesJson, StreamNotifyRoom.class);
                    stream.parseArgs();
                    subscriber.onNext(stream);
                    subscriber.onCompleted();
                })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(stream -> {
                            NotifyRoom notifyRoom = stream.getNotifyRoom();
                            if (notifyRoom != null) {
                                if (NotifyActionType.TYPING.equals(notifyRoom.getAction())) {
                                    executeRoomNotification(notifyRoom);
                                } else if (NotifyActionType.DELETE_MESSAGE.equals(notifyRoom.getAction())) {
                                    MessageDAO.remove(notifyRoom.getId());
                                }
                            } else {
                                Log.d("debug", newValuesJson);
                            }
                        });
                break;
            default:
                new CollectionDAO(collectionName, documentID, newValuesJson).insert();
        }
    }

    private void addRcSubscription(String documentID, String newValuesJson) {
        Observable.create((OnSubscribe<RcSubscriptionDAO>) subscriber -> {
            RcSubscriptionDAO sub = Util.GSON.fromJson(newValuesJson, RcSubscriptionDAO.class);
            subscriber.onNext(sub);
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sub -> {
                    sub.insert(documentID);
                });
    }

    private void addStreamMessages(String documentID, String newValuesJson) {
        Observable.create((OnSubscribe<StreamMessages>) subscriber -> {
            StreamMessages msg = Util.GSON.fromJson(newValuesJson, StreamMessages.class);
            msg.parseArgs();
            subscriber.onNext(msg);
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msg -> {
                    msg.insert();
                });
    }

    private void executeRoomNotification(NotifyRoom notifyRoom) {
        String rid = notifyRoom.getRid();
        if (!TextUtils.isEmpty(rid)) {
            Intent intent = new Intent();
            intent.setAction(StreamNotifyRoom.COLLECTION_NAME + rid);
            intent.putExtra(StreamNotifyRoom.COLLECTION_NAME, notifyRoom);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        //TODO: update the data that changed in the right table
        // I think I will not be able to use GSON, probable manual parsing and updating only the needed fields

        switch (collectionName) {
            case RcSubscriptionDAO.COLLECTION_NAME:
                updateRcSubscription(documentID, updatedValuesJson);
                return;
            case StreamNotifyRoom.COLLECTION_NAME:
                //ignore
                break;
        }

        updateCollectionDAO(collectionName, documentID, updatedValuesJson, removedValuesJson);

    }

    private void updateCollectionDAO(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        Observable.create(new OnSubscribe<CollectionDAO>() {
            @Override
            public void call(Subscriber<? super CollectionDAO> subscriber) {
                try {
                    final CollectionDAO dao = CollectionDAO.query(collectionName, documentID);
                    if (dao != null) {
                        dao.plusUpdatedValues(updatedValuesJson).lessUpdatedValues(removedValuesJson);
                    }
                    subscriber.onNext(dao);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dao -> {
                    if (dao != null) {
                        dao.update();
                    } else {
                        new CollectionDAO(collectionName, documentID, updatedValuesJson).insert();
                    }
                });
    }

    private void updateRcSubscription(final String documentID, final String updatedValuesJson) {
        Observable.create(new OnSubscribe<RcSubscriptionDAO>() {
            @Override
            public void call(Subscriber<? super RcSubscriptionDAO> subscriber) {
                RcSubscriptionDAO rcSub = RcSubscriptionDAO.get(documentID);
                subscriber.onNext(rcSub);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rcSub -> {
                    if (rcSub != null) {
                        rcSub.update(documentID, updatedValuesJson);
                    }
                });
    }

    public void onDataRemoved(String collectionName, String documentID) {
        new CollectionDAO(collectionName, documentID, null).remove();
    }

    public void onException(Throwable e) {
        Crashlytics.logException(e);
    }

    @Override
    public String getString(String key) {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(key, null);
    }

    @Override
    public void putString(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(key, value).apply();
    }

    public Meteor getMeteor() {
        return mMeteor;
    }

    public RxMeteor getRxMeteor() {
        return mRxMeteor;
    }

    public RxRocketMethods getRxMethods() {
        return mRxRocketMethods;
    }

    public RxRocketSubscriptions getRxSubscriptions() {
        return mRxRocketSubscriptions;
    }
}
