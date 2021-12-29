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

import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {

    private static final String TAG = UserDetailsFragment.class.getSimpleName();

    private EditText mName;
    private EditText mAddress;
    private EditText mAlternateContact;
    private RadioGroup mGender;
    private Button mSave;

    private String name;
    private String address;
    private String alternateContact;
    private String[] gender;

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
        mName = view.findViewById(R.id.et_name);
        mAddress = view.findViewById(R.id.et_address);
        mAlternateContact = view.findViewById(R.id.et_contact_alternate);
        mGender = view.findViewById(R.id.rg_gender);
        mSave = view.findViewById(R.id.bv_save);
        gender = new String[]{getString(R.string.gender_male)};

        // updating gender on radio button click
        mGender.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.rb_gender_male) {
                gender[0] = getString(R.string.gender_male);
            }
            if (id == R.id.rb_gender_female) {
                gender[0] = getString(R.string.gender_female);
            }
        });

        // validation check will be performed when Sign up button is clicked
        mSave.setOnClickListener(v -> {

            // getting the texts from Views
            name = mName.getText().toString();
            address = mAddress.getText().toString();
            alternateContact = mAlternateContact.getText().toString();

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
            firebaseFirestore = FirebaseFirestore.getInstance();

            // getting the document users by its id because its unique always
            DocumentReference user = firebaseFirestore.collection(getString(R.string.user)).document(firebaseAuth.getCurrentUser().getUid());

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
            user.get().addOnCompleteListener(task -> {
                User userObj;
                if (task.isSuccessful()) {
                    userObj = task.getResult().toObject(User.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(getString(R.string.user), (Serializable) userObj);
                    UserProfileFragment userProfileFragment = new UserProfileFragment();
                    userProfileFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.auth_fragment_container, userProfileFragment)
                            .commit();
                }
            });


        });

    }
}

