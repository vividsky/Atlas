package com.example.atlas.authentication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.atlas.Models.ServiceReceiver;
import com.example.atlas.Models.User;
import com.example.atlas.R;
import com.example.atlas.home.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

public class UserProfileFragment extends Fragment {

    public static final String TAG = UserProfileFragment.class.getSimpleName();
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private final ArrayList<String> servicesSelected = new ArrayList<>();
    private String[] servicesList;
    private boolean[] checkedItems;
    private User userObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userObj = (User) getArguments().getSerializable(getString(R.string.user));

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        servicesList = getResources().getStringArray(R.array.speciality_label);
        checkedItems = new boolean[servicesList.length];

        Button buttonInNeedOfService = view.findViewById(R.id.b_profile_type_sin);
        Button buttonServiceProvider = view.findViewById(R.id.b_profile_type_sp);

        buttonServiceProvider.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.user), (Serializable) userObj);
            ServiceProviderDetailsFragment serviceProviderDetailsFragment = new ServiceProviderDetailsFragment();
            serviceProviderDetailsFragment.setArguments(bundle);
            getParentFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.auth_fragment_container, serviceProviderDetailsFragment)
                    .commit();
        });

        buttonInNeedOfService.setOnClickListener(view2 -> {
            saveInNeedOfServices(view);
        });

    }

    private void saveInNeedOfServices(View view) {
        servicesSelected.clear();

        // initialise the alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // set the title for the alert dialog
        builder.setTitle("Choose Services");

        // now this is the function which sets the alert dialog for multiple item selection ready
        builder.setMultiChoiceItems(R.array.speciality_label, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton("Done", (dialog, which) -> {
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    servicesSelected.add(servicesList[i]);
                }
            }
            if (!servicesSelected.isEmpty()) {
                makeUserReceiverCollection();
            } else {
                Toast.makeText(getContext(), "Please select at least one service.", Toast.LENGTH_SHORT).show();
            }
        });

        // create the alert dialog with the
        // alert dialog builder instance
        Dialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void makeUserReceiverCollection() {

        ProgressBar progressBar = getView().findViewById(R.id.pb_progressbar_in_profile_fragment);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firebaseFirestore.collection(getString(R.string.service_receiver))
                .document(firebaseAuth.getCurrentUser().getUid());
        documentReference.set(new ServiceReceiver(documentReference.getId(), servicesSelected, userObj))
                .addOnCompleteListener(task2 -> {
                    progressBar.setVisibility(View.GONE);
                    if (task2.isSuccessful()) {
                        // Update type of user

                        // getting the document users by its id because its unique always
                        DocumentReference currentUser = firebaseFirestore.collection(getString(R.string.user)).document(firebaseAuth.getCurrentUser().getUid());

                        // Update user profile
                        currentUser.update("profile", getString(R.string.service_receiver)).addOnSuccessListener(success -> {
                            Log.d(TAG, "profile successfully updated");
                        }).addOnFailureListener(failure -> {
                            Log.d(TAG, "profile updating failed");
                        });

                        Toast.makeText(getContext(), "Service Receiver details saved successfully.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), task2.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}

