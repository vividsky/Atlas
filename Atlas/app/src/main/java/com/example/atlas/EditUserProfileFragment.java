package com.example.atlas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserProfileFragment extends Fragment {

    Button mChangePassword;
    Button mConfirmPassword;
    EditText mOldPassword;
    EditText mNewPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChangePassword = view.findViewById(R.id.bv_change_password);
        mOldPassword = view.findViewById(R.id.et_edit_old_password);
        mNewPassword = view.findViewById(R.id.et_edit_new_password);
        mConfirmPassword = view.findViewById(R.id.bv_confirm_password);

        mChangePassword.setOnClickListener(v -> {
            mChangePassword.setVisibility(View.GONE);
            mOldPassword.setVisibility(View.VISIBLE);
            mNewPassword.setVisibility(View.VISIBLE);
            mConfirmPassword.setVisibility(View.VISIBLE);
        });

        mConfirmPassword.setOnClickListener(v -> {
            // TODO: if successfully changed

            mChangePassword.setVisibility(View.VISIBLE);
            mOldPassword.setVisibility(View.GONE);
            mNewPassword.setVisibility(View.GONE);
            mConfirmPassword.setVisibility(View.GONE);
        });
    }
}