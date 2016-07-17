package chat.rocket.app.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.List;
import java.util.UnknownFormatConversionException;

import chat.rocket.app.R;
import chat.rocket.app.db.collections.StreamNotifyRoom;
import chat.rocket.app.db.dao.MessageDAO;
import chat.rocket.app.ui.adapters.MessagesAdapter;
import chat.rocket.app.ui.base.BaseActivity;
import chat.rocket.app.ui.chat.files.FileCallback;
import chat.rocket.app.ui.chat.files.FileListFragment;
import chat.rocket.app.ui.chat.members.RoomMembersFragment;
import chat.rocket.app.ui.chat.menu.PinnedMessagesFragment;
import chat.rocket.app.ui.chat.menu.RoomSettingsFragment;
import chat.rocket.app.ui.chat.menu.SearchFragment;
import chat.rocket.app.ui.chat.menu.StaredMessagesFragment;
import chat.rocket.app.ui.chat.record.AudioRecordFragment;
import chat.rocket.app.ui.widgets.FabMenuLayout;
import chat.rocket.app.utils.Util;
import chat.rocket.models.NotifyRoom;
import chat.rocket.models.RcSubscription;
import chat.rocket.rc.models.Message;
import meteor.operations.MeteorException;
import meteor.operations.Protocol;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by julio on 29/11/15.
 */
public class ChatActivity extends BaseActivity implements FabMenuLayout.MenuClickListener, FileCallback, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String RC_SUB = "sub";
    private static final int LOADER_ID = 3;
    private RcSubscription mRcSubscription;
    private FabMenuLayout mFabMenu;

    private ListView mListView;
    private MessagesAdapter mAdapter;

    private EditText mSendEditText;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotifyRoom notif = (NotifyRoom) intent.getSerializableExtra(StreamNotifyRoom.COLLECTION_NAME);
            if (mRcSubscription.getRid().equals(notif.getRid())) {
                if (notif.isHappening()) {
                    getSupportActionBar().setSubtitle(notif.getUsername() + " is " + notif.getAction());
                } else {
                    getSupportActionBar().setSubtitle("");
                }
            }
        }
    };
    private ProgressBar mUploadProgress;

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(StreamNotifyRoom.COLLECTION_NAME + mRcSubscription.getRid());
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mRcSubscription = (RcSubscription) getIntent().getSerializableExtra(RC_SUB);
        mFabMenu = (FabMenuLayout) findViewById(R.id.FabMenu);
        mListView = (ListView) findViewById(R.id.listview);
        mSendEditText = (EditText) findViewById(R.id.SubmitEditText);
        mFabMenu.setTopView(mListView);
        mFabMenu.setContentView(mListView);
        mFabMenu.setMenuClickListener(this);
        mSendEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mRxRocketMethods.sendMessage(mRcSubscription.getRid(), mSendEditText.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(message -> {
                            mSendEditText.getText().clear();
                            mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
                        });
                return true;
            }
            return false;
        });
        mUploadProgress = (ProgressBar) findViewById(R.id.UploadProgress);
        mAdapter = new MessagesAdapter(this);
        mListView.setAdapter(mAdapter);
        int unread = mRcSubscription.getUnread();
        if (unread == 0) {
            unread = 25;
        }
        mRxRocketMethods.loadHistory(mRcSubscription.getRid(), null, unread, mRcSubscription.getLs())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messages -> {
                    mRxRocketMethods.readMessages(mRcSubscription.getRid())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    for (Message m : messages.getMessages()) {
                        MessageDAO msg = new MessageDAO(m);
                        msg.insert();
                    }
                });

        mRxRocketSubscriptions.room(mRcSubscription.getName(), mRcSubscription.getType())
                .map(aVoid -> mRxRocketSubscriptions.fullUserData(null, 1))
                .map(voidObservable -> mRxRocketSubscriptions.filteredUsers())
                .map(voidObservable1 -> mRxRocketSubscriptions.channelAutocomplete())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();


        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mRcSubscription.getFormattedName());

    }

    private void uploadFile(String name, String media, long size, String[] parts) {

        String extension = name.substring(name.length() - 3);
        mRxRocketMethods.uploadFile(Util.getServerUrl(), mRxMeteor.getUserId(),
                mRcSubscription.getRid(), name, parts, media, extension, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Float>() {
                    @Override
                    public void onCompleted() {
                        mUploadProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Protocol.Error err = ((MeteorException) e).getError();
                        String error = err.getError();
                        String reason = err.getReason();
                        String details = err.getDetails();
                        Timber.e(e, "upload - onError" + error + ", " + reason + ", " + details);
                        mUploadProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Float progress) {
                        Timber.d("progress:" + progress);
                        mUploadProgress.setProgress((int) (progress * 100));
                    }
                });

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return MessageDAO.getLoader(mRcSubscription.getRid());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mFabMenu.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Note: why it does not surprise me?  Workaround for: https://code.google.com/p/android/issues/detail?id=189121
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    @Override
    public void onMenuItemClick(int id) {
        switch (id) {
            case R.id.SettingsButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.MenuContentLayout, new RoomSettingsFragment()).commit();
                break;
            case R.id.SearchButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.MenuContentLayout, new SearchFragment()).commit();
                break;
            case R.id.MembersButton:
                openMemberList(null);
                break;
            case R.id.FilesButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.MenuContentLayout, new FileListFragment()).commit();
                break;
            case R.id.StaredButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.MenuContentLayout, new StaredMessagesFragment()).commit();
                break;
            case R.id.PinnedButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.MenuContentLayout, new PinnedMessagesFragment()).commit();
                break;
            case R.id.MicButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.MenuContentLayout, new AudioRecordFragment()).commit();
                break;

        }
    }


    @Override
    public void processFile(String filePath, String media) {
        mFabMenu.onBackPressed();
        mUploadProgress.setVisibility(View.VISIBLE);
        File file = new File(Util.getPath(getApplicationContext(), filePath));
        if (file.exists()) {
            Observable.create(new Observable.OnSubscribe<String[]>() {
                @Override
                public void call(Subscriber<? super String[]> subscriber) {
                    String str = Util.decodeFile(file);
                    if (str != null) {
                        int chunkSize = 4 * 8 * 1024;
                        subscriber.onNext(Util.splitStringBySize(str, chunkSize).toArray(new String[0]));
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new UnknownFormatConversionException("failed to convert " + filePath + " to string"));
                    }
                }
            })
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String[]>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mUploadProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNext(String[] strings) {
                            uploadFile(file.getName(), media, file.length(), strings);
                        }
                    });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //NOTE: this method is called when the user clicks in some message that looks like an username
        String content = intent.getData().toString();
        String username = content.substring(content.indexOf("@") + 1);
        openMemberList(username);
    }

    private void openMemberList(String username) {
        getSupportFragmentManager().beginTransaction().replace(R.id.MenuContentLayout, RoomMembersFragment.newInstace(mRcSubscription.getRid(), username)).commit();
    }
}
