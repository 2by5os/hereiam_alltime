package com.geekwims.hereiam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.geekwims.hereiam.R;
import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.api.UserInfo;
import com.geekwims.hereiam.api.request.AuthenticationRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public static final String PREF_NAME_LOGIN_INFO = "login_info";
    public static final String PREF_KEY_LOGIN_ID = "id";
    public static final String PREF_KEY_LOGIN_PW = "pw";
    public static final String PREF_KEY_LOGIN_IS_KEEP = "is_keep";

    @BindView(R.id.input_id)
    EditText _idText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.checkbox_keep_signed)
    CheckBox _keepSignedCheckbox;
//    @Bind(R.id.link_signup)
//    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME_LOGIN_INFO, MODE_PRIVATE);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

//        _signupLink.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);
//                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            }
//        });

        if (prefs.getBoolean(PREF_KEY_LOGIN_IS_KEEP, false)) {
            String id = prefs.getString(PREF_KEY_LOGIN_ID, "");
            String pw = prefs.getString(PREF_KEY_LOGIN_PW, "");
            _idText.setText(id);
            _passwordText.setText(pw);
            _keepSignedCheckbox.setChecked(true);

            login();
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.show();

        String id = _idText.getText().toString();
        String password = _passwordText.getText().toString();
        Boolean isKeep = _keepSignedCheckbox.isChecked();

        // TODO: Implement your own authentication logic here.

        new AuthRefreshTask().execute(id, password, progressDialog, isKeep);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(boolean isKeep) {
        _loginButton.setEnabled(true);

        UserInfo.getInstance().setId(_idText.getText().toString());

        SharedPreferences prefs = getSharedPreferences(PREF_NAME_LOGIN_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (isKeep) {
            editor.putString(PREF_KEY_LOGIN_ID, _idText.getText().toString());
            editor.putString(PREF_KEY_LOGIN_PW, _passwordText.getText().toString());
        } else {
            editor.putString(PREF_KEY_LOGIN_ID, "");
            editor.putString(PREF_KEY_LOGIN_PW, "");
        }
        editor.putBoolean(PREF_KEY_LOGIN_IS_KEEP, isKeep);
        editor.apply();

        finish();

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String id = _idText.getText().toString();
        String password = _passwordText.getText().toString();

        if (id.isEmpty()) {
            _idText.setError("학번을 입력해주세요");
            valid = false;
        } else {
            _idText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError("4자리이상 입력해주세요");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    // login authentication task
    // param[0] : id(String), param[1] : password(String), param[2] : progressDialog(ProgressDialog), param[3] : is keep (Boolean)
    private class AuthRefreshTask extends AsyncTask<Object, Void, Boolean> {
        private ProgressDialog progressDialog = null;
        private boolean isKeep = false;

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            progressDialog = (ProgressDialog) params[2];
            ApiService apiService = ApiService.getInstance();
            isKeep = (Boolean) params[3];

            return apiService.refreshAccessToken(new AuthenticationRequest((String) params[0], (String) params[1], "ROLE_STUDENT"));
        }
        // onPostExecute displays the results of the AsyncTask.

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                onLoginSuccess(isKeep);
            } else {
                onLoginFailed();
            }

            progressDialog.dismiss();
        }
    }
}
