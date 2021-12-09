package com.example.atlas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {

    private static final String TAG = UserDetailsFragment.class.getSimpleName();
    private static final String GENDER_MALE = "Male";
    private static final String GENDER_FEMALE = "Female";
    private static final String SERVICE_PROVIDER = "service provider";
    private static final String IN_NEED_OF_SERVICES = "in need of services";

    private EditText mName;
    private EditText mAddress;
    private EditText mAlternateContact;
    private RadioGroup mGender;
    private RadioGroup mServices;
    private Button mSave;

    private String currentUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get all the views to retrieve user inputs
        mName = (EditText) view.findViewById(R.id.et_name);
        mAddress = (EditText) view.findViewById(R.id.et_address);
        mAlternateContact = (EditText) view.findViewById(R.id.et_contact_alternate);

        mGender = (RadioGroup) view.findViewById(R.id.rg_gender);
        mServices = (RadioGroup) view.findViewById(R.id.rg_services);

        // setting gender to be default as male, if nothing clicked on radio button
        String[] gender = new String[]{GENDER_MALE};

        // setting type to be default as Service Provider, if nothing clicked on radio button
        String[] type = new String[]{SERVICE_PROVIDER};

        // updating gender on radio button click
        mGender.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_gender_male:
                    gender[0] = GENDER_MALE;
                    break;
                case R.id.rb_gender_female:
                    gender[0] = GENDER_FEMALE;
                    break;
                default:
                    // do nothing or we can set default gender here.
            }
        });

//        mServices.clearCheck();

        // updating type on radio button click
        mServices.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_service_provider:
                    type[0] = SERVICE_PROVIDER;
                    Log.d(TAG, "SP is clicked.");
                    // TODO: Go to service provider fragment and the data of this fragment should not be lost
                    break;
                case R.id.rb_require_services:
                    type[0] = IN_NEED_OF_SERVICES;
//                    TODO: dialog box to ask for needs
//                    ArrayList<String> requiredServices = saveInNeedOfServices(view);
//                    Log.d(TAG, String.valueOf(requiredServices));
                    break;
                default:
                    Log.d(TAG, "SP is clicked in default.");
                    // do nothing or we can set default type here.
                    // TODO: Go to service provider fragment and the data of this fragment should not
                    //  be lost and "need to see/resolve" if its to be done here or not since default type is SP and may be user clicks nothing
            }
        });

        mSave = (Button) view.findViewById(R.id.bv_save);

        // validation check will be performed when Sign up button is clicked
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mName.getText().toString();
                String address = mAddress.getText().toString();
                String alternateContact = mAlternateContact.getText().toString();

                // setting it to null so that after filling any data previous error is gone
                mName.setError(null);
                mAddress.setError(null);
                mAlternateContact.setError(null);

                /* validations check
                 * 1. name
                 * 2. address
                 * 3. alternate contact
                */
                if (TextUtils.isEmpty(name)) {
                    mName.setError("Name is required.");
                    return;
                }
                if (!Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$").matcher(name).matches()) {
                    mName.setError("Please enter a valid name.");
                }


                if (TextUtils.isEmpty(address)) {
                    mAddress.setError("Address is required.");
                    return;
                }

                if (!TextUtils.isEmpty(alternateContact) && !Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$")
                        .matcher(alternateContact).matches()) {
                    mAlternateContact.setError("Please enter a valid Contact detail.");
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                currentUserId = mAuth.getCurrentUser().getUid();

                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

                // getting the document users by its id because its unique always
                DocumentReference user = mFirestore.collection("Users").document(currentUserId);

                /* Updates
                 * 1. name
                 * 2. gender
                 * 3. address
                 * 4. type
                 * 5. alternate contact
                 */
                user.update("name", name).addOnSuccessListener(success -> {
                    Log.d(TAG, "name successfully updted");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "name updating failed");
                });

                user.update("gender", gender[0]).addOnSuccessListener(success -> {
                    Log.d(TAG, "gender successfully updted");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "gender updating failed");
                });

                user.update("address", address).addOnSuccessListener(success -> {
                    Log.d(TAG, "address successfully updted");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "address updating failed");
                });

                user.update("type", type[0]).addOnSuccessListener(success -> {
                    Log.d(TAG, "type successfully updted");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "type updating failed");
                });

                // update contact only when entered as its an optional field
                if (!alternateContact.isEmpty()) {
                    user.update("alternateContact", alternateContact).addOnSuccessListener(success -> {
                        Log.d(TAG, "alternate Contact successfully updted");
                    }).addOnFailureListener(failure -> {
                        Log.d(TAG, "alternate Contact updating failed");
                    });
                }

                // to indicate user save was successful
                Toast.makeText(getActivity(), "Successfully Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     *
     * @param view
     * @return
     */
    private ArrayList<String> saveInNeedOfServices(View view) {
        ArrayList<String> servicesChecked = new ArrayList<>();

        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.custom_services_dialog, null);
        CheckBox cook = (CheckBox) mView.findViewById(R.id.cb_cook);
        CheckBox housemaid = (CheckBox) mView.findViewById(R.id.cb_housemaid);
        CheckBox babysitter = (CheckBox) mView.findViewById(R.id.cb_babysitter);
        CheckBox dailyWager = (CheckBox) mView.findViewById(R.id.cb_daily_wager);
        CheckBox driver = (CheckBox) mView.findViewById(R.id.cb_driver);
        CheckBox tutor = (CheckBox) mView.findViewById(R.id.cb_tutor);
        Button save = (Button) mView.findViewById(R.id.bv_save_services);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        save.setOnClickListener(v -> {
            if (cook.isChecked()) {
                servicesChecked.add(cook.getText().toString());
                Log.d(TAG, "in cook" + "--" + servicesChecked);
            }
            if (housemaid.isChecked()) {
                servicesChecked.add(housemaid.getText().toString());
                Log.d(TAG, "in 2" + "--" + servicesChecked);
            }
            if (babysitter.isChecked()) {
                servicesChecked.add(babysitter.getText().toString());
                Log.d(TAG, "in 3" + "--" + servicesChecked);
            }
            if (dailyWager.isChecked()) {
                servicesChecked.add(dailyWager.getText().toString());
                Log.d(TAG, "in 4" + "--" + servicesChecked);
            }
            if (driver.isChecked()) {
                servicesChecked.add(driver.getText().toString());
                Log.d(TAG, "in 5" + "--" + servicesChecked);
            }
            if (tutor.isChecked()) {
                servicesChecked.add(tutor.getText().toString());
                Log.d(TAG, "in 6" + "--" + servicesChecked);
            }
            alertDialog.dismiss();
        });

        alertDialog.show();
        Log.d(TAG, String.valueOf(servicesChecked));
        return servicesChecked;
    }
}
