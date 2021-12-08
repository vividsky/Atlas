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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.atlas.Models.ServiceProviderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ServiceProviderDetailsFragment extends Fragment {
    Button mSaveButton;
    EditText mExperience;
    EditText mExpectedWage;
    RadioGroup vehicleOwnedRadioGroup;
    RadioButton vehicleOwnedNo, vehicleOwnedYes;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_provider_details, container, false);
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mSaveButton = view.findViewById(R.id.button_service_provider_details_save);
//
//        mSaveButton.setOnClickListener(view1 -> {
//            String expectedWage = mExpectedWage.getText().toString();
//            String experience = mExperience.getText().toString();
//            String vehicleOwned = "No";
//
//
//            firebaseAuth = FirebaseAuth.getInstance();
//            firebaseFirestore = FirebaseFirestore.getInstance();
//            firebaseFirestore.collection("ServiceProvider")
//                    .document(firebaseAuth.getCurrentUser().getUid())
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        User user = task.getResult().toObject(User.class);
//                    });
//
//
//        });
//    }
}