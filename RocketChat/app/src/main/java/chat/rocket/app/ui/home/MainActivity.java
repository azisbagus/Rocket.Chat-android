package chat.rocket.app.ui.home;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

import chat.rocket.app.R;
import chat.rocket.app.ui.base.BaseActivity;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity {
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new HomeFragment()).commit();
        }

        //TODO: why that number 1 ??? is it the current number of users in the room?
        mRxRocketSubscriptions.fullUserData(null, 1)
                .flatMap(aVoid -> mRxRocketSubscriptions.filteredUsers())
                .flatMap(aVoid1 -> mRxRocketSubscriptions.channelAutocomplete())
                .flatMap(aVoid2 -> mRxRocketSubscriptions.streamMessages())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "fail");
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        Timber.i("ok");
                    }
                });


    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            endMeteorConnection();
        }

    }
}
