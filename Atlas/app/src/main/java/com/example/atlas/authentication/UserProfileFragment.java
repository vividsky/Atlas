package com.example.atlas.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.atlas.MainActivity;
import com.example.atlas.Models.ServiceProviderModel;
import com.example.atlas.Models.ServiceReceiverModel;
import com.example.atlas.Models.UsersModel;
import com.example.atlas.R;
import com.example.atlas.ServiceProviderDetailsFragment;
import com.example.atlas.UserDetailsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment {

    public static final String TAG = UserProfileFragment.class.getSimpleName();

    private ArrayList<String> servicesSelected = new ArrayList<>();
    private final String[] servicesList = new String[]{"Cook", "Driver", "Babysitter", "Tutor", "Daily wager", "Maid"};
    private final boolean[] checkedItems = new boolean[servicesList.length];

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonInNeedOfService = (Button) view.findViewById(R.id.b_profile_type_sin);
        Button buttonServiceProvider = (Button) view.findViewById(R.id.b_profile_type_sp);

        buttonServiceProvider.setOnClickListener(view1 -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.auth_fragment_container, new ServiceProviderDetailsFragment())
                    .commit();
        });

        buttonInNeedOfService.setOnClickListener(view2 -> {
            saveInNeedOfServices(view);
        });

    }

    private void saveInNeedOfServices(View view) {
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
            if(!servicesSelected.isEmpty()) {
                makeUserReceiverCollection();
            } else {
                Toast.makeText(getContext(), "Please select at least one service.", Toast.LENGTH_SHORT).show();
            }
        });

        // create the builder
        builder.create();

        // create the alert dialog with the
        // alert dialog builder instance
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void makeUserReceiverCollection() {

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
                    firebaseFirestore.collection("ServiceReceivers")
                            .document(firebaseAuth.getCurrentUser().getUid())
                            .set(new ServiceReceiverModel(servicesSelected, user))
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    Toast.makeText(getContext(), "Service Receiver details saved successfully.", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    if(intent.resolveActivity(getContext().getPackageManager()) != null) {
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(getContext(), task2.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                });

    }
}

