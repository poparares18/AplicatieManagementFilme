package com.example.aplicatiemanagementfilme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.database.model.UserAccount;
import com.example.aplicatiemanagementfilme.database.service.UserAccountService;
import com.example.aplicatiemanagementfilme.fragments.ProfileFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class LogInActivity extends AppCompatActivity {

    public static final int SIGN_UP_REQUEST_CODE = 200;
    public static final String PASSWORD_SP = "PASSWORD_SP";
    public static final String USERNAME_SP = "USERNAME_SP";
    public static final String REMEMBER_CHECKED = "REMEMBER_CHECKED";
    public static final int DELETE_ACCOUNT_REQUEST_CODE = 201;
    private static TextInputEditText tiet_username;
    private static TextInputEditText tiet_password;
    private CheckBox checkBoxRemember;
    private Button btn_login;
    private Button btn_signup;

    private UserAccount userAccount = null;
    private UserAccountService userAccountService;
    private int codEroare = 1;

    private SharedPreferences preferences;
    public static final String SHARED_PREF_FILE_NAME = "ApesTogetherSharedPreferences";
    public static final String USER_INFORMATION_KEY = "USER_INFORMATION_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initializare componente APELARE
        initComponents();

        // Atasare functie buton pt start activity Signup APELARE
        btn_signup.setOnClickListener(onClickOpenSignUpListener());

        // Atasare functie buton log in
        btn_login.setOnClickListener(onClickLogInListener());

        // Preluare shared preferences
        getLogInInfoFromSharedPreference();

        // Schimbare remember me
        checkBoxRemember.setOnCheckedChangeListener(onCheckedChangedRememberListener());
    }


    // Activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Primire userAccount nou
        if (requestCode == SIGN_UP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            userAccount = (UserAccount) data.getSerializableExtra(SignUpActivity.USER_ACCOUNT_KEY);
            // Populare log in
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_accountCreated_LogIn),
                    Toast.LENGTH_LONG).show();

            populateLogIn(userAccount);
        }

        // Stergere userAccount
        if (requestCode == DELETE_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Stergere shared preferences
            deleteLogInInfoToSharedPreference();
            // Stergere campuri log in
            deleteUserInfoFromLogIn();
            // Debifare remember me
            if (checkBoxRemember.isChecked()) {
                checkBoxRemember.setChecked(false);
            }
        }
    }


    // Functii
    // Initializare componente
    private void initComponents() {
        tiet_username = findViewById(R.id.tiet_username_login);
        tiet_password = findViewById(R.id.tiet_password_login);
        checkBoxRemember = findViewById(R.id.checkbox_remember_login);
        btn_login = findViewById(R.id.btn_login_login);
        btn_signup = findViewById(R.id.btn_signup_login);

        // Initializare user account service
        userAccountService = new UserAccountService(getApplicationContext());

        // Initializare shared preferences
        preferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
    }

    // Functie buton pt start activity sign up
    private View.OnClickListener onClickOpenSignUpListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, SIGN_UP_REQUEST_CODE);
            }
        };
    }

    // Schimbare check pe remember me
    private CompoundButton.OnCheckedChangeListener onCheckedChangedRememberListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    deleteLogInInfoToSharedPreference();
                }
            }
        };
    }

    // Populare campuri log in
    private void populateLogIn(UserAccount userAccount) {
        tiet_username.setText(userAccount.getUsername());
        tiet_password.setText(userAccount.getPassword());
    }


    // Functie pt log in
    private View.OnClickListener onClickLogInListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    // validare db
                    String username = tiet_username.getText().toString();
                    String password = tiet_password.getText().toString();

                    userAccountService.getUsersByUsername(username, getUsersByUsernameCallback(password));
                }
            }
        };
    }

    // Callback get users by username
    private Callback<List<UserAccount>> getUsersByUsernameCallback(String password) {
        return new Callback<List<UserAccount>>() {
            @Override
            public void runResultOnUiThread(List<UserAccount> result) {
                if (result.size() == 0) {
                    codEroare = 2;  // In caz ca usernameul nu exista
                } else {
                    codEroare = 1;  // In caz ca parola nu exista
                    for (UserAccount userAccount : result) {
                        if (password.equals(userAccount.getPassword())) {
                            // E bine
                            codEroare = 0;
                            break;
                        }
                    }
                }

                if (validateDB(codEroare)) {
                    // Testare Remember me
                    if (checkBoxRemember.isChecked()) {
                        saveLogInInfoToSharedPreference();
                    } else {
                        deleteLogInInfoToSharedPreference();
                    }

                    // Deschidere noua activitate
                    //Toast.makeText(getApplicationContext(), "Logare reusita!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(USER_INFORMATION_KEY, result.get(0));
                    startActivityForResult(intent, DELETE_ACCOUNT_REQUEST_CODE);
                }
            }
        };
    }

    // Validare componente log in
    private boolean validate() {
        if (tiet_username.getText().toString().trim().length() <= 5
                || tiet_username.getText().toString().trim().length() >= 16) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_username_signup),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tiet_password.getText().toString().trim().length() <= 5
                || tiet_password.getText().toString().trim().length() >= 16) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_password_signup),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    // Validare log in DB
    private boolean validateDB(int codEroare) {
        if (codEroare != 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_db_login)
                    , Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    // Shared preferences
    // Salvare shared pref
    private void saveLogInInfoToSharedPreference() {
        String username = tiet_username.getText().toString();
        String password = tiet_password.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME_SP, username);
        editor.putString(PASSWORD_SP, password);
        editor.putBoolean(REMEMBER_CHECKED, true);
        editor.apply();
    }

    // Stergere informatii log in shared pref
    private void deleteLogInInfoToSharedPreference() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME_SP, "");
        editor.putString(PASSWORD_SP, "");
        editor.putBoolean(REMEMBER_CHECKED, false);
        editor.putBoolean(ProfileFragment.AUTO_LOG_IN_CHECKED, false);
        editor.apply();
    }

    // Incarcare shared pref
    private void getLogInInfoFromSharedPreference() {
        String username = preferences.getString(USERNAME_SP, "");
        String password = preferences.getString(PASSWORD_SP, "");
        boolean rememberChecked = preferences.getBoolean(REMEMBER_CHECKED, false);
        boolean rememberAutoLogIn = preferences.getBoolean(ProfileFragment.AUTO_LOG_IN_CHECKED, false);

        tiet_username.setText(username);
        tiet_password.setText(password);
        if (rememberChecked) {
            checkBoxRemember.setChecked(true);
        }
        if (rememberAutoLogIn) {
            btn_login.performClick();
        }
    }

    // Stergere date din casute
    public static void deleteUserInfoFromLogIn() {
        tiet_username.setText("");
        tiet_password.setText("");
    }
}