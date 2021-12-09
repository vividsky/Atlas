package com.example.atlas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.atlas.Models.ServiceProviderModel;
import com.example.atlas.Models.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ServiceProviderDetailsFragment extends Fragment {

    private static final String TAG = "ServiceProviderDetails";
    private static final String VEHICLE_OWNED_NO = "No";
    private static final String VEHICLE_OWNED_YES = "Yes";

    Button mSaveButton;
    EditText mExperience;
    EditText mExpectedWage;
    RadioGroup vehicleOwnedRadioGroup;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_provider_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mExperience = view.findViewById(R.id.et_experience);
        mExpectedWage = view.findViewById(R.id.et_expected_wage);
        mSaveButton = view.findViewById(R.id.button_service_provider_details_save);
        vehicleOwnedRadioGroup = view.findViewById(R.id.rg_vehicle_owned);

        String[] vehicleOwned = new String[1];
        vehicleOwnedRadioGroup.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.rb_vehicle_owned_yes) {
                vehicleOwned[0] = VEHICLE_OWNED_YES;
            }
            if (id == R.id.rb_vehicle_owned_no) {
                vehicleOwned[0] = VEHICLE_OWNED_NO;
            }
        });

        mSaveButton.setOnClickListener(view1 -> {
            Log.d(TAG, "In button click");

            String expectedWage = mExpectedWage.getText().toString();
            String experience = mExperience.getText().toString();

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();

            firebaseFirestore.collection("Users")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        UsersModel user = null;
                        if (task.isSuccessful()) {
                            user = task.getResult().toObject(UsersModel.class);
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        firebaseFirestore.collection("ServiceProviders")
                                .document(firebaseAuth.getCurrentUser().getUid())
                                .set(new ServiceProviderModel( null, experience, expectedWage, vehicleOwned[0], user))
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        Log.d(TAG, "details of service provider saved successfully");
                                        Toast.makeText(getContext(), "Service Provider details saved successfully.", Toast.LENGTH_SHORT).show();
                                        getChildFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.auth_fragment_container, new UserDetailsFragment());
                                    } else {
                                        Toast.makeText(getContext(), task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    });


        });
    }
}