package com.example.atlas.authentication;

import android.annotation.SuppressLint;
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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atlas.home.MainActivity;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {


    private static final String TAG = "LoginFragment";
    TextView loginToSignup;
    Button loginButton;
    TextInputLayout email;
    TextInputLayout password;
    String signInText;
    ProgressBar progressBar;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @SuppressLint("ShowToast")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loginToSignup  = view.findViewById(R.id.tv_login_to_signup);
        loginButton    = view.findViewById(R.id.bv_login);
        email       = view.findViewById(R.id.et_login_email);
        password       = view.findViewById(R.id.et_password);
        signInText     = getString(R.string.asking_for_sign_up);
        progressBar = view.findViewById(R.id.pb_login_page);
        SpannableString ss = new SpannableString(signInText);

        // creating clickable span to be implemented as a link
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View widget) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.auth_fragment_container, new RegisterFragment())
                        .commit();
            }
        };
        // setting the part of string to be act as a link
        ss.setSpan(clickableSpan, signInText.indexOf("Sign"), signInText.indexOf('.'), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginToSignup.setText(ss);
        loginToSignup.setMovementMethod(LinkMovementMethod.getInstance());

        loginButton.setOnClickListener(view1 -> {

            String emailText = email.getEditText().getText().toString();
            String passwordText = password.getEditText().getText().toString();

            email.setError(null);
            password.setError(null);

            if(TextUtils.isEmpty(emailText)) {
                email.setError("Email is required.");
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.setError("Please enter a valid Email address.");
                return;
            }

            if(TextUtils.isEmpty(passwordText)) {
                password.setError("Password is required.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            isUserProfileSavedSuccessful();
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    });
        });

//        loginButton.setOnClickListener(view1 ->  {
//            String phoneNumber = "+91" + email.getEditText().getText().toString();
//            String smsCode = "123456";
//
//            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//            FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
//
//// Configure faking the auto-retrieval with the whitelisted numbers.
//            firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode);
//
//            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
//                    .setPhoneNumber(phoneNumber)
//                    .setTimeout(60L, TimeUnit.SECONDS)
//                    .setActivity(getActivity())
//                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                        @Override
//                        public void onVerificationCompleted(PhoneAuthCredential credential) {
//                           startActivity(new Intent(getContext(), MainActivity.class));
//                        }
//
//                        @Override
//                        public void onVerificationFailed(@NonNull FirebaseException e) {
//                            Log.d(TAG, e.getLocalizedMessage());
//                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                        }
//
//                        // ...
//                    })
//                    .build();
//            PhoneAuthProvider.verifyPhoneNumber(options);
//        });
    }
    private void isUserProfileSavedSuccessful() {

        DocumentReference user = firebaseFirestore.collection(getString(R.string.user)).document(firebaseAuth.getCurrentUser().getUid());

        user.get().addOnCompleteListener(task -> {
            User userObj;
            if (task.isSuccessful()) {
                userObj = task.getResult().toObject(User.class);
                if (userObj != null) {
                    if (userObj.getName() == null)
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.auth_fragment_container, new UserDetailsFragment())
                                .commit();
                    else if(userObj.getProfile() == null) {
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.auth_fragment_container, new UserProfileFragment())
                                .commit();
                    }
                    else {
                        Toast.makeText(getContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    }
                }
            } else {
                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        progressBar.setVisibility(View.GONE);

    }
}