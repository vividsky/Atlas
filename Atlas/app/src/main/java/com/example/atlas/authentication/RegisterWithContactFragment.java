package com.example.atlas.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.Utils;
import com.example.atlas.home.MainActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;

import java.util.concurrent.TimeUnit;

public class RegisterWithContactFragment extends Fragment {

    private static final String TAG = RegisterWithContactFragment.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private String phoneNumber;
    private String otp;

    private EditText mGetOTP;
    private TextInputLayout mGetContactNumber;
    private Button mSendOTPButton;
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_with_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGetOTP = view.findViewById(R.id.et_get_otp);
        mGetContactNumber = view.findViewById(R.id.et_get_contact_number);
        mSendOTPButton = view.findViewById(R.id.bv_send_otp);
        mLoginButton = view.findViewById(R.id.bv_contact_login);
        mProgressBar = view.findViewById(R.id.pb_contact_progressbar);
        firebaseAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "onVerificationFailed", e);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                // TODO use resend token method
//                mResendToken = token;
            }
        };

        // TODO Switch visiblity to gone
        mGetContactNumber.setOnClickListener(view3 -> {
            mSendOTPButton.setVisibility(View.VISIBLE);
            mGetOTP.setVisibility(View.GONE);
            mLoginButton.setVisibility(View.GONE);
        });

        mSendOTPButton.setOnClickListener(view1 -> {

            phoneNumber = "+91" + mGetContactNumber.getEditText().getText().toString();
            mSendOTPButton.setVisibility(View.GONE);
            mGetOTP.setVisibility(View.VISIBLE);
            mLoginButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            // To send OTP to the given number
            startPhoneNumberVerification(phoneNumber);
        });

        mLoginButton.setOnClickListener(view2 -> {
            otp = mGetOTP.getText().toString();
            if (otp.isEmpty()) {
                Toast.makeText(getContext(), "OTP is required", Toast.LENGTH_SHORT).show();
            } else if (otp.length() != 6) {
                Toast.makeText(getContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
            } else {
                // While entering OTP manually
                // mVerificationId -> by FirebaseAuth (onCodeSent method)
                // otp -> entered by user
                signInWithPhoneAuthCredential(verifyPhoneNumberWithCode(mVerificationId, otp));
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            updateUI();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                });
    }

    private PhoneAuthCredential verifyPhoneNumberWithCode(String verificationId, String code) {
        return PhoneAuthProvider.getCredential(verificationId, code);
    }

    private void updateUI() {
        Log.i(TAG, Utils.getCurrentFirebaseUser().getPhoneNumber());
        DocumentReference currentUserDR = Utils.getCurrentUserDocumentReference();
        currentUserDR.get().addOnCompleteListener(task -> {
            User user = task.getResult().toObject(User.class);
            if (TextUtils.isEmpty(user.getContact())) {
                User mUser = new User(firebaseAuth.getCurrentUser().getUid(), "", phoneNumber);

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
                startActivity(new Intent(getContext(), AuthenticationActivity.class));
                getActivity().finish();
            }
        });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}