package com.example.aplicatiemanagementfilme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.database.dao.UserAccountDao;
import com.example.aplicatiemanagementfilme.database.model.UserAccount;
import com.example.aplicatiemanagementfilme.database.service.UserAccountService;
import com.example.aplicatiemanagementfilme.fragments.ProfileFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    public static final String USER_ACCOUNT_KEY = "USER_ACCOUNT_KEY";
    public static final String UPDATED_USER_ACCOUNT_KEY = "UPDATED_USER_ACCOUNT_KEY";
    private TextInputEditText tiet_username;
    private TextInputEditText tiet_password;
    private TextInputEditText tiet_email;
    private TextInputEditText tiet_fullname;
    private Button btn_signup;
    private TextView tv_title;

    private Intent intent;
    private UserAccountService userAccountService;
    private int codEroare = -1;

    private UserAccount userAccountGlobal;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initializare componente APELARE
        initComponents();

        // Atasare functie buton pt Signup APELARE
        btn_signup.setOnClickListener(onClickSignUpListener());
    }


    // Functii
    // Initializare componente
    private void initComponents() {
        tiet_username = findViewById(R.id.tiet_username_signup);
        tiet_password = findViewById(R.id.tiet_password_signup);
        tiet_email = findViewById(R.id.tiet_email_signup);
        tiet_fullname = findViewById(R.id.tiet_fullname_signup);
        btn_signup = findViewById(R.id.btn_signup_signup);
        tv_title = findViewById(R.id.tv_title_signup);

        // Preluare intent
        intent = getIntent();

        // Verificare existenta cont ( pentru editare )
        if (intent.hasExtra(ProfileFragment.CURRENT_USER_EDIT_KEY)) {
            userAccountGlobal = (UserAccount) intent.getSerializableExtra(ProfileFragment.CURRENT_USER_EDIT_KEY);
            createViewFromUserAccount(userAccountGlobal);
            isEditing = true;
        }

        // Initializare dao
        userAccountService = new UserAccountService(getApplicationContext());

    }


    // Atasare functie buton pt Signup
    private View.OnClickListener onClickSignUpListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    createAccountFromWidgets();
                    // validare db
                    userAccountService.getUsersByUsernameOrEmail(userAccountGlobal.getUsername(),
                            userAccountGlobal.getEmail(),
                            getUsersByUsernameOrEmailCallback(userAccountGlobal));

                }
            }
        };
    }

    //  Callback pt get users by username or email
    private Callback<List<UserAccount>> getUsersByUsernameOrEmailCallback(UserAccount userAccount) {
        return new Callback<List<UserAccount>>() {
            @Override
            public void runResultOnUiThread(List<UserAccount> result) {
                if (result.size() == 0) {
                    codEroare = 0;
                } else {
                    for (UserAccount userAccountResult : result) {
                        if(userAccount.getId() != userAccountResult.getId()) {
                            if (userAccount.getUsername().equals(userAccountResult.getUsername())) {
                                codEroare = 1;
                                break;
                            }
                            if (userAccount.getEmail().equals(userAccountResult.getEmail())) {
                                codEroare = 2;
                                break;
                            }
                        }
                        else{
                            codEroare = 0;
                        }
                    }
                }
                if (validateDB(codEroare)) {
                    if(!isEditing) {
                        // Inserare in baza de date
                        userAccountService.insert(userAccount, new Callback<UserAccount>() {
                            @Override
                            public void runResultOnUiThread(UserAccount result) {
                                intent.putExtra(USER_ACCOUNT_KEY, result);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    }
                    else{
                        // Update in baza de date
                        userAccountService.update(userAccount, new Callback<UserAccount>() {
                            @Override
                            public void runResultOnUiThread(UserAccount result) {
                                intent.putExtra(UPDATED_USER_ACCOUNT_KEY, result);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    }
                }
            }
        };
    }


    // Functie validare componente fara db
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

        // Bonus validare adresa email cu @ si .
        if (tiet_email.getText().toString().trim().length() <= 9
                || tiet_email.getText().toString().trim().length() >= 26) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_email_signup),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tiet_fullname.getText().toString().trim().length() <= 4
                || tiet_fullname.getText().toString().trim().length() >= 26) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_fullname_signup),
                    Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }


    // Creare account din widgets
    private void createAccountFromWidgets() {
        String username = tiet_username.getText().toString();
        String password = tiet_password.getText().toString();
        String email = tiet_email.getText().toString();
        String fullName = tiet_fullname.getText().toString();

        if(isEditing){
            userAccountGlobal.setUsername(username);
            userAccountGlobal.setPassword(password);
            userAccountGlobal.setEmail(email);
            userAccountGlobal.setFullName(fullName);
        }
        else {
            userAccountGlobal = new UserAccount(username, password, email, fullName);
        }
    }


    // Validare existenta username DB
    private boolean validateDB(int codEroare) {
        if (codEroare == 1) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_username_DB_signup),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (codEroare == 2) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_email_DB_signup),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (codEroare == 0) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_eroareNecunoscuta_DB_signup),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Populare campuri cu un cont primit
    private void createViewFromUserAccount(UserAccount userAccount){
        // Populare campuri
        tiet_username.setText(userAccountGlobal.getUsername());
        tiet_password.setText(userAccountGlobal.getPassword());
        tiet_email.setText(userAccount.getEmail());
        tiet_fullname.setText(userAccount.getFullName());

        // Editare texte
        tv_title.setText(R.string.edit_your_account_sign_up);
        btn_signup.setText(R.string.edit_btn_sign_up);
    }
}