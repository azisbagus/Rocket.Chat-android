package chat.rocket.app.ui.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import chat.rocket.app.R;
import chat.rocket.app.RocketApp;
import chat.rocket.app.ui.base.BaseActivity;
import chat.rocket.app.ui.login.LoginActivity;


/**
 * Created by julio on 20/11/15.
 */
public class SplashActivity extends BaseActivity {
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RocketApp.ACTION_CONNECTED.equals(intent.getAction())) {
                Intent intent1 = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }
            if (RocketApp.ACTION_DISCONNECTED.equals(intent.getAction())) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        IntentFilter filter = new IntentFilter();
        filter.addAction(RocketApp.ACTION_CONNECTED);
        filter.addAction(RocketApp.ACTION_DISCONNECTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        startMeteorConnection();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endMeteorConnection();
    }
}
