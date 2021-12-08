package com.example.atlas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicReference;
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

    private String UserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mName = (EditText) view.findViewById(R.id.et_name);
        mAddress = (EditText) view.findViewById(R.id.et_address);
        mAlternateContact = (EditText) view.findViewById(R.id.et_contact_alternate);

        mGender = (RadioGroup) view.findViewById(R.id.rg_gender);
        mServices = (RadioGroup) view.findViewById(R.id.rg_services);

        String[] gender = new String[1];
        String[] type = new String[1];

        mGender.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "in Switch");
            switch (checkedId) {
                case R.id.rb_gender_male:
                    Log.d(TAG, "in case male");
                    gender[0] = GENDER_MALE;
                    break;
                case R.id.rb_gender_female:
                    // do operations specific to this selection
                    Log.d(TAG, "in case female");
                    gender[0] = GENDER_FEMALE;
                    break;
                default:
            }
        });

        mServices.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "in Switch2");
            switch (checkedId) {
                case R.id.rb_service_provider:
                    Log.d(TAG, "in case SP");
                    type[0] = SERVICE_PROVIDER;
                    // TODO: go to service provider fragment and the data of this fragment should not be lost
                    break;
                case R.id.rb_require_services:
                    // do operations specific to this selection
                    Log.d(TAG, "in case need");
                    //TODO: dialog box to ask for needs
                    type[0] = IN_NEED_OF_SERVICES;
                    break;
                default:
            }
        });

        mSave = (Button) view.findViewById(R.id.bv_save);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mName.getText().toString();
                String address = mAddress.getText().toString();
                String alternateContact = mAlternateContact.getText().toString();

                mName.setError(null);
                mAddress.setError(null);
                mAlternateContact.setError(null);

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

                if (!TextUtils.isEmpty(alternateContact) && !Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$").matcher(alternateContact).matches()) {
                    mAlternateContact.setError("Please enter a valid Contact detail.");
                    return;
                }

                Log.d(TAG, name + "-" + gender[0] + "-" + address + "-" + alternateContact + "-" + type[0]);
                Toast.makeText(getActivity(), "Successfully Saved", Toast.LENGTH_SHORT).show();

//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                UserId = mAuth.getCurrentUser().getUid();
//                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
//                Task<Void> user = mFirestore.collection("Users").document(UserId).update("name",userName);
//                user.get().addOnCompleteListener();

            }
        });

    }
}
