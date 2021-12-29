package com.example.atlas.authentication;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RegisterWithEmailFragment extends Fragment {

    public static final String TAG = RegisterWithEmailFragment.class.getSimpleName();

    private Button mSignUp;
    private TextInputLayout mEmailId;
    private TextInputLayout mContact;
    private TextInputLayout mPassword;
    private TextInputLayout mConfirmPassword;
    private ProgressBar mProgressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

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

        mEmailId         = view.findViewById(R.id.et_email);
        mContact         = view.findViewById(R.id.et_contact);
        mPassword        =  view.findViewById(R.id.et_register_password);
        mConfirmPassword = view.findViewById(R.id.et_confirm_password);

        mProgressBar = view.findViewById(R.id.pb_progressbar);

        mSignUp = view.findViewById(R.id.bv_signup);

        // validation check will be performed when Sign up button is clicked
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

                /* validations check
                 * 1. email
                 * 2. contact
                 * 3. password
                 * 4. confirm password
                 */
                if (TextUtils.isEmpty(email)) {
                    mEmailId.setError("Email is required.");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailId.setError("Please enter a valid Email address.");
                    return;
                }

                if (TextUtils.isEmpty(contact)) {
                    mContact.setError("Contact is required.");
                    return;
                }

                if (!Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$").matcher(contact).matches()) {
                    mContact.setError("Please enter a valid Contact detail.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required.");
                    return;
                }

                /* Password regex
                 * Minimum eight characters, at least one letter, one number and one special character
                 */
                if (!Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$").matcher(password).matches()) {
                    mPassword.setError("Password should contain minimum eight characters, at least one letter, one number and one special character.");
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    mConfirmPassword.setError("Confirm Password is required.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    mConfirmPassword.setError("Password does not match.");
                    return;
                }

                // set progress bar visibility to visible to tell user sign Up is in process
                mProgressBar.setVisibility(View.VISIBLE);

                // authentication
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(createUserTask -> {
                            if (createUserTask.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                // creating user model to save it in fireStore
                                User mUser = new User(firebaseAuth.getCurrentUser().getUid(), email, contact);

                                Utils.getCurrentUserDocumentReference().set(mUser)
                                        .addOnCompleteListener(isUserCreatedTask -> {

                                            // once done with sign up, progress bar visibility set to GONE
                                            mProgressBar.setVisibility(View.GONE);

                                            if (isUserCreatedTask.isSuccessful()) {
                                                Toast.makeText(getContext(), "Sign up successful.",
                                                        Toast.LENGTH_SHORT).show();

                                                // working of moving from signUp page to userDetails Page
                                                getParentFragmentManager()
                                                        .beginTransaction()
                                                        .replace(R.id.auth_fragment_container, new UserDetailsFragment())
                                                        .commit();
                                            } else {
                                                Log.d(TAG, "SignUp failed.User does not exist.");
                                            }
                                        });

                            } else {
                                // once done with sign up, progress bar visibility set to GONE
                                mProgressBar.setVisibility(View.GONE);

                                // If sign up fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure" + createUserTask.getException().getMessage());

                                Toast.makeText(getActivity(), createUserTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }

}

