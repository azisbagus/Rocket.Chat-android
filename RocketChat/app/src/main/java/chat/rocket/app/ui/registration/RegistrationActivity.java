package chat.rocket.app.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import chat.rocket.app.R;
import chat.rocket.app.ui.base.BaseActivity;
import meteor.operations.MeteorException;
import meteor.operations.Protocol;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by julio on 20/11/15.
 */
public class RegistrationActivity extends BaseActivity {
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmationEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mNameEditText = (EditText) findViewById(R.id.NameEditText);
        mEmailEditText = (EditText) findViewById(R.id.EmailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.PasswordEditText);
        mPasswordConfirmationEditText = (EditText) findViewById(R.id.PasswordConfirmationEditText);

        findViewById(R.id.RegistrationButton).setOnClickListener(v -> {

            String name = mNameEditText.getText().toString().trim();
            String email = mEmailEditText.getText().toString().trim();
            String password = mPasswordEditText.getText().toString().trim();
            String passwordConfirmation = mPasswordConfirmationEditText.getText().toString().trim();
            //TODO: Handle the input in a nice way and show a proper message to the user about what is wrong.
            if (validadeFields(name, email, password, passwordConfirmation)) {
                executeRegistration(name, email, password);
            } else {
                Toast.makeText(this, "Invalid input, check your fields", Toast.LENGTH_LONG).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean validadeFields(String name, String email, String password, String passwordConfirmation) {
        //TODO: What would be valid values here?
        return name.length() > 3 && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() > 5 && password.equals(passwordConfirmation);
    }

    private void executeRegistration(String name, String email, String password) {
        mRxRocketMethods.registerUser(name, email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Protocol.Error err = ((MeteorException) e).getError();
                        String error = err.getError();
                        String reason = err.getReason();
                        String details = err.getDetails();
                        //TODO: Think about a nice error message
                        Toast.makeText(RegistrationActivity.this, "Ops, something is wrong: " + reason, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s) {
                        returnUserData(email, password);
                    }
                });
    }

    private void returnUserData(String email, String password) {
        Intent data = new Intent();
        data.putExtra(EMAIL, email);
        data.putExtra(PASSWORD, password);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}