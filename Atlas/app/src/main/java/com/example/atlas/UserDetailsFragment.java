package com.example.atlas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.atlas.authentication.AuthenticationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

    private ArrayList<String> servicesSelected = new ArrayList<>();


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

        String[] gender = new String[1];
        String[] type = new String[1];

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
                    // do nothing.
            }
        });

        // updating type on radio button click
        mServices.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_service_provider:
                    type[0] = SERVICE_PROVIDER;
                    Log.d(TAG, "SP is clicked.");
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.auth_fragment_container, new ServiceProviderDetailsFragment())
                            .commit();
                    // TODO: Go to service provider fragment and the data of this fragment should not be lost

                    break;
                case R.id.rb_require_services:
                    type[0] = IN_NEED_OF_SERVICES;
                    Log.d(TAG, "calling choose");
                    saveInNeedOfServices(view);
                    break;
                default:
                    // do nothing.
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
                 * 2. gender
                 * 3. address
                 * 4. alternate contact
                 * 5. services
                */
                if (TextUtils.isEmpty(name)) {
                    mName.setError("Name is required.");
                    return;
                }
                if (!Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$").matcher(name).matches()) {
                    mName.setError("Please enter a valid name.");
                }

                if (mGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Gender is required.", Toast.LENGTH_SHORT).show();
                    return;
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

                if (mServices.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Services Type is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(type[0].equals(IN_NEED_OF_SERVICES) && servicesSelected.isEmpty()) {
                    Toast.makeText(getContext(), "At least one Need should be selected.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, String.valueOf(servicesSelected));
                // Todo : use servicesSelected array to save "in need of Service user model."

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
                    Log.d(TAG, "name successfully updated");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "name updating failed");
                });

                user.update("gender", gender[0]).addOnSuccessListener(success -> {
                    Log.d(TAG, "gender successfully updated");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "gender updating failed");
                });

                user.update("address", address).addOnSuccessListener(success -> {
                    Log.d(TAG, "address successfully updated");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "address updating failed");
                });


                user.update("alternateContact", alternateContact).addOnSuccessListener(success -> {
                    Log.d(TAG, "alternate Contact successfully updated");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "alternate Contact updating failed");
                });

                user.update("type", type[0]).addOnSuccessListener(success -> {
                    Log.d(TAG, "type successfully updated");
                }).addOnFailureListener(failure -> {
                    Log.d(TAG, "type updating failed");
                });

                // to indicate user save was successful
                Toast.makeText(getContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void saveInNeedOfServices(View view) {
        final String[] servicesList = new String[]{"Cook", "Driver", "Babysitter", "Tutor", "Daily wager", "HouseMaid"};
        final boolean[] checkedItems = new boolean[servicesList.length];
        servicesSelected.clear();

        // initialise the alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // set the title for the alert dialog
        builder.setTitle("Choose Services");

        // now this is the function which sets the alert dialog for multiple item selection ready
        builder.setMultiChoiceItems(servicesList, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        // alert dialog shouldn't be cancellable
        builder.setCancelable(false);

        builder.setPositiveButton("Done", (dialog, which) -> {
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    servicesSelected.add(servicesList[i]);
                }
            }
        });

        // create the builder
        builder.create();

        // create the alert dialog with the
        // alert dialog builder instance
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}

