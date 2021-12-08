package com.example.atlas.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atlas.MainActivity;
import com.example.atlas.Models.UsersModel;
import com.example.atlas.R;
import com.example.atlas.UserDetailsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    public static final String TAG = RegisterFragment.class.getSimpleName();

    Button mSignUp;

    TextInputLayout mEmailId;
    TextInputLayout mContact;
    TextInputLayout mPassword;
    TextInputLayout mConfirmPassword;

    ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //work of moving back from signUp page to login page
        TextView signupToLogin = (TextView) view.findViewById(R.id.tv_signup_to_login);
        String logInText = getString(R.string.asking_for_login);
        SpannableString ss = new SpannableString(logInText);

        // creating clickable span to be implemented as a link
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View widget) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.auth_fragment_container, new LoginFragment())
                        .commit();
            }
        };

        // setting the part of string to be act as a link
        ss.setSpan(clickableSpan, logInText.indexOf("Login"), logInText.indexOf('.'), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signupToLogin.setText(ss);
        signupToLogin.setMovementMethod(LinkMovementMethod.getInstance());

        /* validations check
         * 1. email
         * 2. contact
         * 3. password
         * 4. confirm password
         */
        mEmailId = (TextInputLayout) view.findViewById(R.id.et_email);
        mContact = (TextInputLayout) view.findViewById(R.id.et_contact);
        mPassword = (TextInputLayout) view.findViewById(R.id.et_register_password);
        mConfirmPassword = (TextInputLayout) view.findViewById(R.id.et_confirm_password);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_progressbar);

        mSignUp = (Button) view.findViewById(R.id.bv_signup);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailId.getEditText().getText().toString();
                String contact = mContact.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                String confirmPassword = mConfirmPassword.getEditText().getText().toString();

                // setting it to null so that after filling any data previous error is gone
                mEmailId.setError(null);
                mContact.setError(null);
                mPassword.setError(null);
                mConfirmPassword.setError(null);

                if(TextUtils.isEmpty(email)) {
                    mEmailId.setError("Email is required.");
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailId.setError("Please enter a valid Email address.");
                    return;
                }

                if(TextUtils.isEmpty(contact)) {
                    mContact.setError("Contact is required.");
                    return;
                }

                if(!Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$").matcher(contact).matches()) {
                    mContact.setError("Please enter a valid Contact detail.");
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required.");
                    return;
                }

                /* Password regex
                 * Minimum eight characters, at least one letter, one number and one special character
                 */
                if(!Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$").matcher(password).matches()) {
                    mPassword.setError("Password should contain minimum eight characters, at least one letter, one number and one special character.");
                    return;
                }

                if(TextUtils.isEmpty(confirmPassword)) {
                    mConfirmPassword.setError("Confirm Password is required.");
                    return;
                }

                if(!password.equals(confirmPassword)) {
                    mConfirmPassword.setError("Password does not match.");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    UsersModel mUser = new UsersModel(mAuth.getCurrentUser().getUid(), email, contact, password);
                                    CollectionReference firestore = FirebaseFirestore.getInstance().collection("Users");
                                    firestore.document(mAuth.getCurrentUser().getUid()).set(mUser)
                                            .addOnCompleteListener(task2 -> {
                                                if(task2.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "SignUp successful.",
                                                            Toast.LENGTH_SHORT).show();
                                                    // work of moving from signUp page to userDetails Page
                                                    getParentFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.auth_fragment_container, new UserDetailsFragment())
                                                            .commit();
                                                } else {
                                                    Log.d(TAG, "SignUp failed due to task2.");
                                                }
                                                mProgressBar.setVisibility(View.GONE);
                                            });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    mProgressBar.setVisibility(View.GONE);
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "SignUp failed due to task1.",
                                            Toast.LENGTH_SHORT).show();
                                }
                        });
            }
        });

    }

}
