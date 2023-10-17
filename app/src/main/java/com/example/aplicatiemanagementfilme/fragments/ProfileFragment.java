package com.example.aplicatiemanagementfilme.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aplicatiemanagementfilme.LogInActivity;
import com.example.aplicatiemanagementfilme.MainActivity;
import com.example.aplicatiemanagementfilme.R;
import com.example.aplicatiemanagementfilme.SignUpActivity;
import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.database.model.UserAccount;
import com.example.aplicatiemanagementfilme.database.service.UserAccountService;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.aplicatiemanagementfilme.LogInActivity.PASSWORD_SP;
import static com.example.aplicatiemanagementfilme.LogInActivity.REMEMBER_CHECKED;
import static com.example.aplicatiemanagementfilme.LogInActivity.USERNAME_SP;


public class ProfileFragment extends Fragment {

    public static final String CURRENT_USER_EDIT_KEY = "CURRENT_USER_EDIT_KEY";
    public static final int EDIT_USER_ACCOUNT_REQUEST_CODE = 202;
    private TextView tvTitle;
    private TextView tvFullName;
    private TextView tvEmail;
    private ImageView ivProfile;
    private Button btnEditAccount;
    private Button btnDeleteAccount;
    private Button btnSignOut;
    private CheckBox checkBoxAutoLogIn;

    public static final String AUTO_LOG_IN_CHECKED = "AUTO_LOG_IN_CHECKED";
    private SharedPreferences preferences;

    private UserAccountService userAccountService;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initializare componente
        initComponents(view);

        // Creare pagina pentru utilizatorul logat
        createProfileFromUserAccount(MainActivity.currentUserAccount);

        // Butoane
        // Sign out
        btnSignOut.setOnClickListener(onClickSignOutListener());

        // Verificare auto log in
        getAutoLogInFromSharedPreferences();

        // Auto log in
        checkBoxAutoLogIn.setOnCheckedChangeListener(onCheckedChangedAutoLogInListener());

        // Delete account
        btnDeleteAccount.setOnClickListener(onClickDeleteAccountListener());

        // Edit account
        btnEditAccount.setOnClickListener(onClickEditAccountListener());

        return view;
    }


    // Functii
    // Initializare componente
    private void initComponents(View view) {
        // Componente
        tvTitle = view.findViewById(R.id.tv_title_profile);
        tvFullName = view.findViewById(R.id.tv_fullName_profile);
        tvEmail = view.findViewById(R.id.tv_userEmail_profile);
        ivProfile = view.findViewById(R.id.iv_avatar_profile);
        btnEditAccount = view.findViewById(R.id.btn_editAccount_profile);
        btnDeleteAccount = view.findViewById(R.id.btn_deleteAccount_profile);
        btnSignOut = view.findViewById(R.id.btn_signOut_profile);
        checkBoxAutoLogIn = view.findViewById(R.id.checkbox_autoLogIn_profile);

        // Preferinte
        preferences = getActivity().getSharedPreferences(LogInActivity.SHARED_PREF_FILE_NAME, MODE_PRIVATE);

        // User account service
        userAccountService = new UserAccountService(getContext());
    }


    // Creare pagina pentru utilizatorul logat
    private void createProfileFromUserAccount(UserAccount userAccount) {
        // Profile
        tvTitle.setText(getString(R.string.profile_username_title_param, userAccount.getUsername()));
        // Full name
        tvFullName.setText(userAccount.getFullName());
        // Email
        tvEmail.setText(userAccount.getEmail());
        // Imagine
        Glide.with(getContext()).load(getString(R.string.url_image_profile)).into(ivProfile);
    }

    // Verificare auto log in
    private void getAutoLogInFromSharedPreferences() {
        boolean rememberAutoLogIn = preferences.getBoolean(AUTO_LOG_IN_CHECKED, false);
        if (rememberAutoLogIn) {
            checkBoxAutoLogIn.setChecked(true);
        }
    }

    // Schimbare check pt auto log in
    private CompoundButton.OnCheckedChangeListener onCheckedChangedAutoLogInListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean rememberChecked = preferences.getBoolean(REMEMBER_CHECKED, false);
                    if (rememberChecked) {
                        putAutoLogInInSharedPreferences();
                    } else {
                        putUserAccountInSharedPreferences();
                    }
                } else {
                    removeAutoLogInInSharedPreferences();
                }
            }
        };
    }


    // Punere auto logIn in shared preferences
    private void putAutoLogInInSharedPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AUTO_LOG_IN_CHECKED, true);
        editor.apply();
    }

    // Punere user account in shared preferences
    private void putUserAccountInSharedPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME_SP, MainActivity.currentUserAccount.getUsername());
        editor.putString(PASSWORD_SP, MainActivity.currentUserAccount.getPassword());
        editor.putBoolean(REMEMBER_CHECKED, true);
        editor.putBoolean(AUTO_LOG_IN_CHECKED, true);
        editor.apply();
    }

    // Stergere auto logIn in shared preferences
    private void removeAutoLogInInSharedPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AUTO_LOG_IN_CHECKED, false);
        editor.apply();
    }


    // Butoane
    // Sign out
    private View.OnClickListener onClickSignOutListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean rememberChecked = preferences.getBoolean(REMEMBER_CHECKED, false);
                if (!rememberChecked) {
                    LogInActivity.deleteUserInfoFromLogIn();
                }
                getActivity().finish();
            }
        };
    }

    // Delete account
    private View.OnClickListener onClickDeleteAccountListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Warning");

                final TextView tvMessage = new TextView(v.getContext());
                tvMessage.setText(R.string.dialog_warning_delete_tv);
                builder.setView(tvMessage);

                builder.setPositiveButton("OK", onClickDialogDeleteAccountOk());
                builder.setNegativeButton("Cancel", onClickDialogDeleteAccountCancel());

                builder.show();
            }
        };
    }

    // Dialog delete account ok
    private DialogInterface.OnClickListener onClickDialogDeleteAccountOk() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Stergere
                userAccountService.delete(MainActivity.currentUserAccount, callbackDeleteUserAccount());
            }
        };
    }

    // Callback pentru stergere user account
    private Callback<Integer> callbackDeleteUserAccount() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                Toast.makeText(getContext(),
                        getString(R.string.toast_account_was_deleted_param,
                                MainActivity.currentUserAccount.getUsername()),
                        Toast.LENGTH_LONG).show();

                getActivity().setResult(RESULT_OK, null);
                getActivity().finish();
            }
        };
    }

    // Dialog delete account cancel
    private DialogInterface.OnClickListener onClickDialogDeleteAccountCancel() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
    }

    // Update account
    // On click update account
    private View.OnClickListener onClickEditAccountListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SignUpActivity.class);
                intent.putExtra(CURRENT_USER_EDIT_KEY, MainActivity.currentUserAccount);
                startActivityForResult(intent, EDIT_USER_ACCOUNT_REQUEST_CODE);
            }
        };
    }

    // On activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_USER_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Toast.makeText(getContext(), getString(R.string.toast_update_success_profileFragment), Toast.LENGTH_SHORT).show();

            UserAccount userAccount = (UserAccount) data.getSerializableExtra(SignUpActivity.UPDATED_USER_ACCOUNT_KEY);
            MainActivity.currentUserAccount = userAccount;
            createProfileFromUserAccount(MainActivity.currentUserAccount);
            updatePreferences(MainActivity.currentUserAccount);
        }
    }


    // Actualizare shared preferences
    private void updatePreferences(UserAccount userAccount) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERNAME_SP, MainActivity.currentUserAccount.getUsername());
        editor.putString(PASSWORD_SP, MainActivity.currentUserAccount.getPassword());
        editor.apply();
    }
}