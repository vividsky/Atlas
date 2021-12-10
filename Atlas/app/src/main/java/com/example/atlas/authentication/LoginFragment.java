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
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atlas.MainActivity;
import com.example.atlas.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

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

         loginToSignup  = view.findViewById(R.id.tv_login_to_signup);
         loginButton    = view.findViewById(R.id.bv_login);
         email       = view.findViewById(R.id.et_login_email);
         password       = view.findViewById(R.id.et_password);

         signInText     = getString(R.string.asking_for_sign_up);
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

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
}