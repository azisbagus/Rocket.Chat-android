package chat.rocket.app.ui.login.password;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import chat.rocket.app.R;
import chat.rocket.app.ui.base.BaseActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by julio on 22/11/15.
 */
public class ForgotPasswordActivity extends BaseActivity {
    private EditText mEmailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mEmailEditText = (EditText) findViewById(R.id.EmailEditText);
        findViewById(R.id.ResetPasswordButton).setOnClickListener(v -> {
            String email = mEmailEditText.getText().toString().trim();
            resetPassword(email);
        });
    }

    private void resetPassword(String email) {
        mRxRocketMethods.sendForgotPasswordEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    Toast.makeText(ForgotPasswordActivity.this, R.string.we_sent_you_an_email_check_it, Toast.LENGTH_LONG).show();
                    finish();
                });
    }
}
