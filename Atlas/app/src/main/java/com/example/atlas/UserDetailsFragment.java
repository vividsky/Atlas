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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {

    public static final String TAG = UserDetailsFragment.class.getSimpleName();

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

        mSave = (Button) view.findViewById(R.id.bv_save);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mName.getText().toString();
                String address = mAddress.getText().toString();
                String alternateContact = mAlternateContact.getText().toString();
                final int[] gender = new int[1];
                final int[] type = new int[1];

                mGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rb_gender_male:
                                gender[0] = 0;
                                break;
                            case R.id.rb_gender_female:
                                // do operations specific to this selection
                                gender[0] = 1;
                                break;
                            default:
                        }
                    }
                });

                mServices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rb_service_provider:
                                type[0] = 0;
                                break;
                            case R.id.rb_require_services:
                                // do operations specific to this selection
                                type[0] = 1;
                                break;
                            default:
                        }
                    }
                });

                mName.setError(null);
                mAddress.setError(null);
                mAlternateContact.setError(null);

                if (TextUtils.isEmpty(userName)) {
                    mName.setError("Name is required.");
                    return;
                }
                if (!Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$").matcher(userName).matches()) {
                    mName.setError("Name is required");
                }


                if (TextUtils.isEmpty(address)) {
                    mAddress.setError("Address is required.");
                    return;
                }

                if (!TextUtils.isEmpty(alternateContact) && !Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$").matcher(alternateContact).matches()) {
                    mAlternateContact.setError("Please enter a valid Contact detail.");
                    return;
                }

                Log.d(TAG, userName + "-" + gender[0] + "-" + address + "-" + alternateContact + "-" + type[0]);
                Toast.makeText(getActivity(), "Successfully Saved", Toast.LENGTH_SHORT).show();

//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                UserId = mAuth.getCurrentUser().getUid();
//                FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
//                Task<Void> user = mFirestore.collection("Users").document(UserId).update("name",userName);
//                // user.get().addOnCompleteListener();

            }
        });

    }
}
