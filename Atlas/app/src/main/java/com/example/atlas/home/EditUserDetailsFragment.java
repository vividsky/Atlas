package com.example.atlas.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class EditUserDetailsFragment extends Fragment {

    public static final String TAG = EditUserDetailsFragment.class.getSimpleName();

    User userObj;
    ServiceProvider serviceProviderObj;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    DrawerLayout mDrawerLayout;
    Toolbar toolbar;

    private TextInputLayout mEditContact;
    private TextInputLayout mEditName;
    private TextInputLayout mEditEmail;
    private TextInputLayout mEditAddress;
    private TextInputLayout mEditAlternateContact;
    private TextInputLayout mEditExperience;
    private TextInputLayout mEditExpectedWage;
    private LinearLayout mEditLLVehicleOwned;
    private RadioGroup mEditGender;
    private RadioGroup mEditVehicleOwned;

    private String name;
    private String email;
    private String address;
    private String alternateContact;
    private String experience;
    private String expectedWage;
    private String[] gender = new String[1];
    private String[] vehicleOwned = new String[1];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set toolbar head to "Profile" and remove hamburger icon from it
        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        toolbar.setNavigationIcon(null);

        // hiding side navigation view
        mDrawerLayout = getActivity().findViewById(R.id.main_activity_drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // hiding bottom navigation
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nv_bottom);
        bottomNavigationView.setVisibility(View.GONE);

        // hiding swipe refresh
        SwipeRefreshLayout swipeRefresh = getActivity().findViewById(R.id.swipe_refresh_home);
        swipeRefresh.setEnabled(false);

        // to get the options and hide it then
        setHasOptionsMenu(true);

        // getting values from bundles
        userObj = (User) getArguments().getSerializable(getString(R.string.user));
        if (userObj.getProfile().equals(getString(R.string.service_provider))) {
            serviceProviderObj = (ServiceProvider) getArguments().getSerializable(getString(R.string.service_provider));
        }

        View view = inflater.inflate(R.layout.fragment_edit_user_details, container, false);
        mEditContact = view.findViewById(R.id.et_edit_contact);
        mEditName = view.findViewById(R.id.et_edit_name);
        mEditEmail = view.findViewById(R.id.et_edit_email);
        mEditGender = view.findViewById(R.id.rg_edit_gender);
        mEditAddress = view.findViewById(R.id.et_edit_address);
        mEditAlternateContact = view.findViewById(R.id.et_edit_alternate_contact);
        mEditExperience = view.findViewById(R.id.et_edit_experience);
        mEditExpectedWage = view.findViewById(R.id.et_edit_wage);
        mEditLLVehicleOwned = view.findViewById(R.id.ll_edit_vehicle_owned);
        mEditVehicleOwned = view.findViewById(R.id.rg_edit_vehicle_owned);
        mEditGender = view.findViewById(R.id.rg_edit_gender);

        // setting user's data
        mEditContact.getEditText().setText(userObj.getContact());
        mEditName.getEditText().setText(userObj.getName());
        mEditEmail.getEditText().setText(userObj.getEmail());
        mEditAddress.getEditText().setText(userObj.getAddress());
        mEditAlternateContact.getEditText().setText(userObj.getAlternateContact());

        if (userObj.getGender().equals(getString(R.string.gender_female))) {
            gender[0] = getString(R.string.gender_female);
            mEditGender.check(R.id.rb_edit_gender_female);
        } else {
            gender[0] = getString(R.string.gender_male);
            mEditGender.check(R.id.rb_edit_gender_male);
        }

        if (userObj.getProfile().equals(getString(R.string.service_receiver))) {
            mEditExperience.setVisibility(View.GONE);
            mEditExpectedWage.setVisibility(View.GONE);
            mEditLLVehicleOwned.setVisibility(View.GONE);
        } else {
            // set experience, wage, vehicle owned
            if (serviceProviderObj != null) {
                mEditExperience.getEditText().setText(serviceProviderObj.getExperience());
                mEditExpectedWage.getEditText().setText(serviceProviderObj.getExpectedWage());

                if (serviceProviderObj.getVehicleOwned().equals(getString(R.string.vehicle_owned_yes))) {
                    vehicleOwned[0] = getString(R.string.vehicle_owned_yes);
                    mEditVehicleOwned.check(R.id.rb_edit_vehicle_owned_yes);
                } else {
                    vehicleOwned[0] = getString(R.string.vehicle_owned_no);
                    mEditVehicleOwned.check(R.id.rb_edit_vehicle_owned_no);
                }
            } else {
                Log.i(TAG, "serviceProviderObj is null");
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // updating gender on radio button click
        mEditGender.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.rb_edit_gender_male) {
                Log.d(TAG, gender[0]);
                gender[0] = getString(R.string.gender_male);
            }
            if (id == R.id.rb_edit_gender_female) {
                Log.d(TAG, gender[0]);
                gender[0] = getString(R.string.gender_female);
            }
        });

        if (userObj.getProfile().equals(getString(R.string.service_provider))) {
            mEditVehicleOwned.setOnCheckedChangeListener((group, id) -> {
                if (id == R.id.rb_edit_vehicle_owned_yes) {
                    vehicleOwned[0] = getString(R.string.vehicle_owned_yes);
                }
                if (id == R.id.rb_edit_vehicle_owned_no) {
                    vehicleOwned[0] = getString(R.string.vehicle_owned_no);
                }
            });
        }

    }

    // hide the menu item refresh
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_refresh).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.save_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_save);
        item.setOnMenuItemClickListener(menuItem -> {

            // getting the texts from Views
            name = mEditName.getEditText().getText().toString();
            email = mEditEmail.getEditText().getText().toString();
            address = mEditAddress.getEditText().getText().toString();
            alternateContact = mEditAlternateContact.getEditText().getText().toString();

            // setting it to null so that after filling any data previous error is gone
            mEditName.setError(null);
            mEditEmail.setError(null);
            mEditAddress.setError(null);
            mEditAlternateContact.setError(null);

            /* validations check
             * 1. name
             * 2. email
             * 3. gender
             * 4. address
             * 5. alternate contact
             */

            if (TextUtils.isEmpty(name)) {
                mEditName.setError("Name is required.");
                return false;
            }
            if (!Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$").matcher(name).matches()) {
                mEditName.setError("Please enter a valid name.");
                return false;
            }

            if (TextUtils.isEmpty(email)) {
                mEditEmail.setError("Email is required.");
                return false;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEditEmail.setError("Please enter a valid Email address.");
                return false;
            }

            if (mEditGender.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getContext(), "Gender is required.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(address)) {
                mEditAddress.setError("Address is required.");
                return false;
            }

            if (!TextUtils.isEmpty(alternateContact) &&
                    !Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$")
                            .matcher(alternateContact).matches()) {
                mEditAlternateContact.setError("Please enter a valid Contact detail.");
                return false;
            }

            //if Service Provider
            if (userObj.getProfile().equals(getString(R.string.service_provider))) {
                experience = mEditExperience.getEditText().getText().toString();
                expectedWage = mEditExpectedWage.getEditText().getText().toString();

                mEditExperience.setError(null);
                mEditExpectedWage.setError(null);

                /* validations check
                 * 1. experience
                 * 2. expected wage
                 * 3. vehicle owned
                 */
                if (mEditVehicleOwned.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Vehicle owned or not is required.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(experience)) {
                    mEditExperience.setError("Experience is required.If not, Enter 0.");
                    return false;
                }
                if (!Pattern.compile("[0-9]|[1-9][0-9]").matcher(experience).matches()) {
                    mEditExperience.setError("Please enter a valid experience years.");
                    return false;
                }
                if (TextUtils.isEmpty(expectedWage)) {
                    mEditExpectedWage.setError("Expected Wage is required.If not, Enter 0.");
                    return false;
                }
                if (!Pattern.compile("([0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-9][0-9][0-9][0-9][0-9])")
                        .matcher(expectedWage).matches()) {
                    mEditExpectedWage.setError("Please enter a valid amount.");
                    return false;
                }
            }

            // saving new updated data to cloud in User as well as sp/sp collection
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();

            // getting the document users by its id because its unique always
            DocumentReference userDocumentReference = Utils.getCurrentUserDocumentReference();

            /* Updates
             * 1. name
             * 2. gender
             * 3. address
             * 4. alternate contact
             */
            userDocumentReference.update("name", name);
            userDocumentReference.update("email", email);
            userDocumentReference.update("gender", gender[0]);
            userDocumentReference.update("address", address);
            userDocumentReference.update("alternateContact", alternateContact);

            userDocumentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User updatedUserObj = task.getResult().toObject(User.class);
                    if (updatedUserObj.getProfile().equals(getString(R.string.service_provider))) {
                        DocumentReference serviceProviderDR = Utils.getCurrentServiceProviderDocumentReference();
                        serviceProviderDR.update("experience", experience);
                        serviceProviderDR.update("expectedWage", expectedWage);
                        serviceProviderDR.update("vehicleOwned", vehicleOwned[0]);
                        serviceProviderDR.update("userDetails", updatedUserObj);

                    } else {
                        DocumentReference serviceReceiverDR = Utils.getCurrentServiceReceiverDocumentReference();
                        serviceReceiverDR.update("userDetails", updatedUserObj);
                    }
                }
            });

            // to indicate user save was successful
            Toast.makeText(getContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
            return true;
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onStop() {
        super.onStop();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }
}