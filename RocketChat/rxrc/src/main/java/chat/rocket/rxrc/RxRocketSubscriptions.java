package chat.rocket.rxrc;

import chat.rocket.rc.RocketSubscriptions;
import chat.rocket.rc.enumerations.ChannelType;
import meteor.operations.MeteorException;
import meteor.operations.ResultListener;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by julio on 05/12/15.
 */
public class RxRocketSubscriptions {
    private RocketSubscriptions mRocketSubscriptions;

    private ResultListener getListener(Subscriber<? super Void> subscriber) {
        return new ResultListener() {
            @Override
            public void onSuccess(String result) {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }

            @Override
            public void onError(MeteorException e) {
                subscriber.onError(e);
            }
        };
    }

    public RxRocketSubscriptions(RocketSubscriptions rocketSubscriptions) {
        mRocketSubscriptions = rocketSubscriptions;
    }

    public Observable<Void> userData() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.userData(getListener(subscriber));
            }
        });
    }

    public Observable<Void> loginServiceConfiguration() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.loginServiceConfiguration(getListener(subscriber));
            }
        });
    }

    public Observable<Void> settings() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.settings(getListener(subscriber));
            }
        });
    }

    public Observable<Void> streamNotifyRoom() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.streamNotifyRoom(getListener(subscriber));
            }
        });
    }

    public Observable<Void> streamNotifyAll() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.streamNotifyAll(getListener(subscriber));
            }
        });
    }

    public Observable<Void> streamNotifyUser() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.streamNotifyUser(getListener(subscriber));
            }
        });
    }

    public Observable<Void> roles() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.roles(getListener(subscriber));
            }
        });
    }

    public Observable<Void> permissions() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.permissions(getListener(subscriber));
            }
        });
    }

    public Observable<Void> streamMessages() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.streamMessages(getListener(subscriber));
            }
        });
    }

    public Observable<Void> meteorAutoupdateClientVersions() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.meteorAutoupdateClientVersions(getListener(subscriber));
            }
        });
    }

    public Observable<Void> subscription() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.subscription(getListener(subscriber));
            }
        });
    }

    public Observable<Void> activeUsers() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.activeUsers(getListener(subscriber));
            }
        });
    }

    public Observable<Void> adminSettings() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.adminSettings(getListener(subscriber));
            }
        });
    }

    public Observable<Void> channelAutocomplete() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.channelAutocomplete(getListener(subscriber));
            }
        });
    }

    public Observable<Void> filteredUsers() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.filteredUsers(getListener(subscriber));
            }
        });
    }

    public Observable<Void> fullUserData(final String filter, final int limit) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.fullUserData(filter, limit, getListener(subscriber));
            }
        });
    }

    public Observable<Void> room(String name, ChannelType type) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                mRocketSubscriptions.room(name, type, getListener(subscriber));
            }
        });
    }

}
