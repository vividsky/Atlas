package com.example.atlas.authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.atlas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {

    private static final String TAG = UserDetailsFragment.class.getSimpleName();

    private static final String GENDER_MALE = "Male";
    private static final String GENDER_FEMALE = "Female";

    private EditText mName;
    private EditText mAddress;
    private EditText mAlternateContact;
    private RadioGroup mGender;
    private RadioGroup mServices;
    private Button mSave;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

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

        String[] gender = new String[1];

        // updating gender on radio button click
        mGender.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.rb_gender_male) {
                gender[0] = GENDER_MALE;
            }
            if (id == R.id.rb_gender_female) {
                gender[0] = GENDER_FEMALE;
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
                    return;
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

                firebaseAuth = FirebaseAuth.getInstance();
                String currentUserId = firebaseAuth.getCurrentUser().getUid();
                firebaseFirestore = FirebaseFirestore.getInstance();

                // getting the document users by its id because its unique always
                DocumentReference user = firebaseFirestore.collection("Users").document(currentUserId);

                /* Updates
                 * 1. name
                 * 2. gender
                 * 3. address
                 * 4. alternate contact
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

                // to indicate user save was successful
                Toast.makeText(getContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.auth_fragment_container, new UserProfileFragment())
                        .commit();

            }
        });

    }

}

