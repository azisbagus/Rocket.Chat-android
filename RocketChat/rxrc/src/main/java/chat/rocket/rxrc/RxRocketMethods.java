package chat.rocket.rxrc;

import java.util.List;

import chat.rocket.rc.RocketMethods;
import chat.rocket.rc.listeners.AddUserToRoomListener;
import chat.rocket.rc.listeners.ArchiveRoomListener;
import chat.rocket.rc.listeners.FileUploadListener;
import chat.rocket.rc.listeners.LoadHistoryListener;
import chat.rocket.rc.listeners.LoginListener;
import chat.rocket.rc.listeners.ReadMessagesListener;
import chat.rocket.rc.listeners.RegisterUserListener;
import chat.rocket.rc.listeners.SendForgotPasswordEmailListener;
import chat.rocket.rc.listeners.SendMessageListener;
import chat.rocket.rc.models.Message;
import chat.rocket.rc.models.Messages;
import chat.rocket.rc.models.TimeStamp;
import chat.rocket.rc.models.Token;
import meteor.operations.MeteorException;
import meteor.operations.ResultListener;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by julio on 05/12/15.
 */
public class RxRocketMethods {
    private RocketMethods mMethods;

    public RxRocketMethods(RocketMethods methods) {
        mMethods = methods;
    }

    public Observable<Boolean> addUserToRoom(final String rid, final String username) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                mMethods.addUserToRoom(rid, username, new AddUserToRoomListener() {
                    @Override
                    public void onResult(Boolean result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<List<Integer>> archiveRoom(final String rid) {
        return Observable.create(new Observable.OnSubscribe<List<Integer>>() {
            @Override
            public void call(final Subscriber<? super List<Integer>> subscriber) {
                mMethods.archiveRoom(rid, new ArchiveRoomListener() {

                    @Override
                    public void onResult(List<Integer> result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Token> loginWithUsername(final String username, final String pass) {
        return Observable.create(new Observable.OnSubscribe<Token>() {
            @Override
            public void call(final Subscriber<? super Token> subscriber) {
                mMethods.loginWithUsername(username, pass, new LoginListener() {

                    @Override
                    public void onResult(Token result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);

                    }
                });
            }
        });
    }

    public Observable<Token> loginWithEmail(final String email, final String pass) {
        return Observable.create(new Observable.OnSubscribe<Token>() {
            @Override
            public void call(final Subscriber<? super Token> subscriber) {
                mMethods.loginWithEmail(email, pass, new LoginListener() {

                    @Override
                    public void onResult(Token result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Token> loginWithOAuth(final String credentialToken, final String credentialSecret) {
        return Observable.create(new Observable.OnSubscribe<Token>() {
            @Override
            public void call(final Subscriber<? super Token> subscriber) {
                mMethods.loginWithOAuth(credentialToken, credentialSecret, new LoginListener() {

                    @Override
                    public void onResult(Token result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Token> loginWithFacebook(final String accessToken, final long expiresIn) {
        return Observable.create(new Observable.OnSubscribe<Token>() {
            @Override
            public void call(final Subscriber<? super Token> subscriber) {
                mMethods.loginWithFacebook(accessToken, expiresIn, new LoginListener() {

                    @Override
                    public void onResult(Token result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Integer> readMessages(String rid) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                mMethods.readMessages(rid, new ReadMessagesListener() {

                    @Override
                    public void onResult(Integer result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Message> sendMessage(String rid, String msg) {
        return Observable.create(new Observable.OnSubscribe<Message>() {
            @Override
            public void call(final Subscriber<? super Message> subscriber) {
                mMethods.sendMessage(rid, msg, new SendMessageListener() {

                    @Override
                    public void onResult(Message result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Messages> loadHistory(String rid, TimeStamp end, int unread, TimeStamp ls) {
        return Observable.create(new Observable.OnSubscribe<Messages>() {
            @Override
            public void call(final Subscriber<? super Messages> subscriber) {
                mMethods.loadHistory(rid, end, unread, ls, new LoadHistoryListener() {

                    @Override
                    public void onResult(Messages result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        super.onError(e);
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Float> uploadFile(final String host, final String userId, final String rid,
                                        final String name, final String[] strings,
                                        final String type, final String extension,
                                        final long size) {
        return Observable.create(new Observable.OnSubscribe<Float>() {
            @Override
            public void call(final Subscriber<? super Float> subscriber) {
                mMethods.uploadFile(host, userId, rid,
                        name, strings, type,
                        extension, size, new FileUploadListener() {

                            @Override
                            public void onProgress(float progress) {
                                subscriber.onNext(progress);
                            }

                            @Override
                            public void onSuccess(String result) {
                                subscriber.onNext(1.0f);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(MeteorException e) {
                                subscriber.onError(e);
                            }
                        });
            }
        });
    }

    public Observable<String> getUsernameSuggestion() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                mMethods.getUsernameSuggestion(new ResultListener() {
                    @Override
                    public void onSuccess(String result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<String> setUsername(String name) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                mMethods.setUsername(name, new ResultListener() {
                    @Override
                    public void onSuccess(String result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Boolean> sendForgotPasswordEmail(String email) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                mMethods.sendForgotPasswordEmail(email, new SendForgotPasswordEmailListener() {
                    @Override
                    public void onResult(Boolean result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }


    public Observable<String> registerUser(String name, String email, String password) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                mMethods.registerUser(name, email, password, new RegisterUserListener() {
                    @Override
                    public void onResult(String result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<String> setPushUser() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                mMethods.setPushUser(new RegisterUserListener() {
                    @Override
                    public void onResult(String result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<String> updatePush(String token) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                mMethods.updatePush(token, new ResultListener() {
                    @Override
                    public void onSuccess(String result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(MeteorException e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }
}
