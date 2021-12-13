package com.example.atlas.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

public class EditUserProfileFragment extends Fragment {

    public static final String TAG = EditUserProfileFragment.class.getSimpleName();

    User userObj;
    ServiceProvider serviceProviderObj;

    private TextInputLayout mEditContact;
    private TextInputLayout mEditName;
    private TextInputLayout mEditEmail;
    private TextInputLayout mEditAddress;
    private TextInputLayout mEditAlternateContact;
    private TextInputLayout mEditExperience;
    private TextInputLayout mEditExpectedWage;
    private LinearLayout mEditLLVehicleOwned;
    private RadioGroup mEditGender;
    private RadioGroup mEditProfile;
    private RadioGroup mEditVehicleOwned;

    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mConfirmNewPassword;
    private Button mChangePassword;
    private Button mSaveNewPassword;
    private Button mSave;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // hiding bottom navigation
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nv_bottom);
        bottomNavigationView.setVisibility(View.GONE);

        // hiding swipe refresh
        SwipeRefreshLayout swipeRefresh = getActivity().findViewById(R.id.swipe_refresh);
        swipeRefresh.setEnabled(false);

        // to get the options and hide it then
        setHasOptionsMenu(true);

        userObj = (User) getArguments().getSerializable(getString(R.string.user));
        if(userObj.getProfile().equals(getString(R.string.service_provider))) {
            serviceProviderObj = (ServiceProvider) getArguments().getSerializable(getString(R.string.service_provider));
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);

        mEditContact = view.findViewById(R.id.et_edit_contact);
        mEditName = view.findViewById(R.id.et_edit_name);
        mEditEmail = view.findViewById(R.id.et_edit_email);
        mEditGender = view.findViewById(R.id.rg_edit_gender);
        mEditProfile = view.findViewById(R.id.rg_edit_profile);
        mEditAddress = view.findViewById(R.id.et_edit_address);
        mEditAlternateContact = view.findViewById(R.id.et_edit_alternate_contact);
        mEditExperience = view.findViewById(R.id.et_edit_experience);
        mEditExpectedWage = view.findViewById(R.id.et_edit_wage);
        mEditLLVehicleOwned = view.findViewById(R.id.ll_edit_vehicle_owned);
        mEditVehicleOwned = view.findViewById(R.id.rg_edit_vehicle_owned);
        mEditGender = view.findViewById(R.id.rg_edit_gender);
        mSave = view.findViewById(R.id.bv_save_edit_profile);


        // setting user's data
        mEditContact.getEditText().setText(userObj.getContact());
        mEditName.getEditText().setText(userObj.getName());
        mEditEmail.getEditText().setText(userObj.getEmail());
        if(userObj.getGender().equals(getString(R.string.gender_female))) {
            mEditGender.check(R.id.rb_edit_gender_female);
        } else {
            mEditGender.check(R.id.rb_edit_gender_male);
        }
        mEditAddress.getEditText().setText(userObj.getAddress());
        mEditAlternateContact.getEditText().setText(userObj.getAlternateContact());

        if(userObj.getProfile().equals(getString(R.string.service_receiver))) {
            mEditProfile.check(R.id.rb_edit_sr);
            mEditExperience.setVisibility(View.GONE);
            mEditExpectedWage.setVisibility(View.GONE);
            mEditLLVehicleOwned.setVisibility(View.GONE);
        } else {
            mEditProfile.check(R.id.rb_edit_sp);

            // set experience, wage, vehicle owned
            if (serviceProviderObj != null) {
                mEditExperience.getEditText().setText(serviceProviderObj.getExperience());
                mEditExpectedWage.getEditText().setText(serviceProviderObj.getExpectedWage());

                if(serviceProviderObj.getVehicleOwned().equals(getString(R.string.vehicle_owned_yes))) {
                    mEditVehicleOwned.check(R.id.rb_edit_vehicle_owned_yes);
                } else {
                    mEditVehicleOwned.check(R.id.rb_edit_vehicle_owned_no);
                }
            } else {
                Log.w(TAG, "serviceProviderObj is null");
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChangePassword = view.findViewById(R.id.bv_change_password);
        mOldPassword = view.findViewById(R.id.et_edit_old_password);
        mNewPassword = view.findViewById(R.id.et_edit_new_password);
        mConfirmNewPassword = view.findViewById(R.id.et_edit_confirm_new_password);
        mSaveNewPassword = view.findViewById(R.id.bv_save_new_password);

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

            mChangePassword.setVisibility(View.VISIBLE);
            mOldPassword.setVisibility(View.GONE);
            mNewPassword.setVisibility(View.GONE);
            mConfirmNewPassword.setVisibility(View.GONE);
            mSaveNewPassword.setVisibility(View.GONE);
        });


        // valid checks on clicking save



    }

    // hide the menu item refresh
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_refresh).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}