package com.example.atlas.authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.atlas.R;
import com.example.atlas.UserDetailsFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    Button mSignUp;

    TextInputLayout mUsername;
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
         * 1. username
         * 2. email
         * 3. contact
         * 4. password
         * 5. confirm password
         */
        mUsername = (TextInputLayout) view.findViewById(R.id.et_register_username);
        mEmailId = (TextInputLayout) view.findViewById(R.id.et_email);
        mContact = (TextInputLayout) view.findViewById(R.id.et_contact);
        mPassword = (TextInputLayout) view.findViewById(R.id.et_register_password);
        mConfirmPassword = (TextInputLayout) view.findViewById(R.id.et_confirm_password);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_progressbar);

        mSignUp = (Button) view.findViewById(R.id.bv_signup);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mUsername.getEditText().getText().toString();
                String email = mEmailId.getEditText().getText().toString();
                String contact = mContact.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                String confirmPassword = mConfirmPassword.getEditText().getText().toString();

                // setting it to null so that after filling any data previous error is gone
                mUsername.setError(null);
                mEmailId.setError(null);
                mContact.setError(null);
                mPassword.setError(null);
                mConfirmPassword.setError(null);

                if(TextUtils.isEmpty(username)) {
                    mUsername.setError("Username is required.");
                    return;
                }

                if(username.length() > 25) {
                    mUsername.setError("Username should be less than 25 characters.");
                    return;
                }

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
                    mContact.setError("Please enter a valid Contact details.");
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

//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                auth.createUserWithEmailAndPassword(email, password)


                // work of moving from signUp page to userDetails Page
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.auth_fragment_container, new UserDetailsFragment())
                        .commit();
            }
        });



    }

}