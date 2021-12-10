package com.example.atlas.authentication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.atlas.MainActivity;
import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ServiceProviderDetailsFragment extends Fragment {

    private static final String TAG = "ServiceProviderDetails";
    private static ArrayList<String> checkedSpecialities = new ArrayList<>();
    String[] options;
    boolean[] checkedItems;

    ImageButton speciality;
    Button mSaveButton;
    EditText mExperience;
    EditText mExpectedWage;
    RadioGroup vehicleOwnedRadioGroup;
    ProgressBar progressBar;
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

         options = getResources().getStringArray(R.array.speciality);
         checkedItems = new boolean[options.length];

        speciality = view.findViewById(R.id.iv_speciality);
        mExperience = view.findViewById(R.id.et_experience);
        mExpectedWage = view.findViewById(R.id.et_expected_wage);
        progressBar = view.findViewById(R.id.pb_service_provider_details);
        mSaveButton = view.findViewById(R.id.button_service_provider_details_save);
        vehicleOwnedRadioGroup = view.findViewById(R.id.rg_vehicle_owned);

        speciality.setOnClickListener(this::servicesProvided);

        String[] vehicleOwned = new String[] {getString(R.string.vehicle_owned_no)};
        vehicleOwnedRadioGroup.setOnCheckedChangeListener((group, id) -> {
            if (id == R.id.rb_vehicle_owned_yes) {
                vehicleOwned[0] = getString(R.string.vehicle_owned_yes);
            }
            if (id == R.id.rb_vehicle_owned_no) {
                vehicleOwned[0] = getString(R.string.vehicle_owned_no);
            }
        });

        mSaveButton.setOnClickListener(view1 -> {
            if (checkedSpecialities.isEmpty()) {
                Toast.makeText(getContext(), "Please choose atleast one Speciality", Toast.LENGTH_SHORT).show();
            } else {

                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "In button click");

                String expectedWage = TextUtils.isEmpty(mExpectedWage.getText().toString()) ? "0" : mExpectedWage.getText().toString();
                String experience = TextUtils.isEmpty(mExperience.getText().toString()) ? "0" : mExperience.getText().toString();

                firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();

                DocumentReference documentReference = firebaseFirestore.collection("Users")
                        .document(firebaseAuth.getCurrentUser().getUid());

                documentReference.get()
                    .addOnCompleteListener(task -> {
                        User user = null;
                        if (task.isSuccessful()) {
                            user = task.getResult().toObject(User.class);
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        firebaseFirestore.collection("ServiceProviders")
                                .document(firebaseAuth.getCurrentUser().getUid())
                                .set(new ServiceProvider(checkedSpecialities, experience, expectedWage, vehicleOwned[0], user))
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        documentReference.update("profile",getString(R.string.service_provider));
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Service Provider details saved successfully.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(getContext(), task2.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    });
            }
        });
    }

    private void servicesProvided(View view1) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle("Choose Specialities");

        dialogBuilder.setMultiChoiceItems(R.array.speciality, checkedItems , (dialog, indexSelected, isChecked) -> {
            if (isChecked) {
                checkedItems[indexSelected] = true;
            } else if (checkedItems[indexSelected]) {
                checkedItems[indexSelected] = false;
            }
            Log.d(TAG, checkedItems[indexSelected] + "");
        });

        dialogBuilder.setPositiveButton("Save", ((dialogInterface, id) -> {
            for (int i = 0; i < options.length; i++) {
                if (checkedItems[i]) {
                    checkedSpecialities.add(options[i]);
                }
            }
        }));

        Dialog dialog = dialogBuilder.create();
        dialog.show();
    }

}