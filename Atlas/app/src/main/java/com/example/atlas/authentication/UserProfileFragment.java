package com.example.atlas.authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.atlas.R;
import com.example.atlas.ServiceProviderDetailsFragment;

public class UserProfileFragment extends Fragment {

    Button buttonServiceProvider;
    Button buttonInNeedOfService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonInNeedOfService = view.findViewById(R.id.b_profile_type_sin);
        buttonServiceProvider = view.findViewById(R.id.b_profile_type_sp);

        buttonInNeedOfService.setOnClickListener(view1 -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.auth_fragment_container, new ServiceProviderDetailsFragment())
                    .commit();
        });

    }
}