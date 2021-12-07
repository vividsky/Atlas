package com.example.atlas.authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.atlas.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView signupToLogin = view.findViewById(R.id.tv_signup_to_login);
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
    }
}