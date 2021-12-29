package com.example.atlas.UsefulCorner;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.atlas.R;
import com.example.atlas.Utils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {


    private static final String TAG = SettingsActivity.class.getSimpleName();

    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mConfirmNewPassword;
    private TextView mChangePassword;
    private Button mSaveNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");

        String email = getIntent().getStringExtra("UserEmail");

        mChangePassword = findViewById(R.id.tv_change_password);
        mOldPassword = findViewById(R.id.et_edit_old_password);
        mNewPassword = findViewById(R.id.et_edit_new_password);
        mConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        mSaveNewPassword = findViewById(R.id.bv_save_new_password);

        // changing password
        mChangePassword.setOnClickListener(v -> {
            mChangePassword.setVisibility(View.GONE);
            mOldPassword.setVisibility(View.VISIBLE);
            mNewPassword.setVisibility(View.VISIBLE);
            mConfirmNewPassword.setVisibility(View.VISIBLE);
            mSaveNewPassword.setVisibility(View.VISIBLE);
        });

        mSaveNewPassword.setOnClickListener(v -> {
            // TODO: if successfully changed
            //  1. new pass must follow pass standard requirements.
            //  2. toast to indicate save was successful on cloud.

            String oldPassword = mOldPassword.getText().toString();
            String newPassword = mNewPassword.getText().toString();
            String confirmNewPassword = mConfirmNewPassword.getText().toString();

            mOldPassword.setError(null);
            mNewPassword.setError(null);
            mConfirmNewPassword.setError(null);

            if (TextUtils.isEmpty(oldPassword)) {
                mOldPassword.setError("Password is required.");
                return;
            }

            if (TextUtils.isEmpty(newPassword)) {
                mNewPassword.setError("Password is required.");
                return;
            }

            /* Password regex
             * Minimum eight characters, at least one letter, one number and one special character
             */
            if (!Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$").matcher(newPassword).matches()) {
                mNewPassword.setError("Password should contain minimum eight characters, at least one letter, one number and one special character.");
                return;
            }

            if (TextUtils.isEmpty(confirmNewPassword)) {
                mConfirmNewPassword.setError("Confirm Password is required.");
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                mConfirmNewPassword.setError("Password does not match.");
                return;
            }

            FirebaseUser firebaseUser = Utils.getCurrentFirebaseUser();
            AuthCredential authCredential = EmailAuthProvider.getCredential(email, oldPassword);
            firebaseUser.reauthenticate(authCredential)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d(TAG, "task1 was successful");
                            firebaseUser.updatePassword(newPassword).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    Toast.makeText(this, "Password updated Successfully.", Toast.LENGTH_SHORT).show();
                                    mChangePassword.setVisibility(View.VISIBLE);
                                    mOldPassword.setVisibility(View.GONE);
                                    mNewPassword.setVisibility(View.GONE);
                                    mConfirmNewPassword.setVisibility(View.GONE);
                                    mSaveNewPassword.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(this, "Some error has occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Invalid old password", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}