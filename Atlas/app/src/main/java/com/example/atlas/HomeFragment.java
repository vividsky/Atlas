package com.example.atlas;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.atlas.Models.ServiceProvider;
import com.example.atlas.Models.ServiceReceiver;
import com.example.atlas.Models.User;
import com.example.atlas.adapters.ServiceProviderAdapter;
import com.example.atlas.adapters.ServiceReceiverAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

// implements LoaderManager.LoaderCallbacks<List<ServiceProvider>>
public class HomeFragment extends Fragment {

    public static final String TAG = MainActivity.class.getSimpleName();

    static RecyclerView recyclerView;
    static FirebaseAuth firebaseAuth;
    static FirebaseFirestore firebaseFirestore;
    static ServiceProviderAdapter serviceProviderAdapter;
    static ServiceReceiverAdapter serviceReceiverAdapter;
    static ArrayList<ServiceProvider> serviceProvidersArrayList;
    static ArrayList<ServiceReceiver> serviceReceiversArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        serviceProviderAdapter = new ServiceProviderAdapter(new ArrayList<>());
//        recyclerView.setAdapter(serviceProviderAdapter);
//        getLoaderManager().initLoader(0, null, this).forceLoad();

        recyclerView = view.findViewById(R.id.main_activity_recycler_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        serviceProvidersArrayList = new ArrayList<>();
        serviceReceiversArrayList = new ArrayList<>();


        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    User user;
                    if(task.isSuccessful()) {
                        user = task.getResult().toObject(User.class);
                        if (user != null) {
                            String profile = user.getProfile();
                            if (profile.equals(getString(R.string.service_receiver))) {
                                firebaseFirestore.collection("ServiceProviders").get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                for (QueryDocumentSnapshot sp : task1.getResult()) {
                                                    ServiceProvider xx = sp.toObject(ServiceProvider.class);
                                                    serviceProvidersArrayList.add(xx);
                                                }
                                                serviceProviderAdapter = new ServiceProviderAdapter(serviceProvidersArrayList);
                                                recyclerView.setHasFixedSize(true);
                                                recyclerView.setAdapter(serviceProviderAdapter);
                                            } else {
                                                Log.i(TAG, task.getException().getMessage());
                                            }
                                        });

                            } else if (profile.equals(getString(R.string.service_provider))) {
                                firebaseFirestore.collection("ServiceReceivers").get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                for (QueryDocumentSnapshot sp : task1.getResult()) {
                                                    ServiceReceiver xx = sp.toObject(ServiceReceiver.class);
                                                    serviceReceiversArrayList.add(xx);
                                                }
                                                serviceReceiverAdapter = new ServiceReceiverAdapter(serviceReceiversArrayList);
                                                recyclerView.setHasFixedSize(true);
                                                recyclerView.setAdapter(serviceReceiverAdapter);
                                            } else {
                                                Log.i(TAG, task.getException().getMessage());
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "Some error has occured", Toast.LENGTH_LONG);
                            }
                        }
                    }
                });

    }

//    @NonNull
//    @Override
//    public Loader<List<ServiceProvider>> onCreateLoader(int id, @Nullable Bundle args) {
//        return new ServiceProviderLoader(getContext());
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<List<ServiceProvider>> loader, List<ServiceProvider> data) {
//        serviceProviderAdapter = new ServiceProviderAdapter(serviceProvidersArrayList);
//
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<List<ServiceProvider>> loader) {
//    }
//    private static class ServiceProviderLoader extends AsyncTaskLoader<List<ServiceProvider>> {
//
//        public ServiceProviderLoader(@NonNull Context context) {
//            super(context);
//        }
//
//        @Nullable
//        @Override
//        public List<ServiceProvider> loadInBackground() {
//
//            firebaseAuth = FirebaseAuth.getInstance();
//            firebaseFirestore = FirebaseFirestore.getInstance();
//
//            serviceProvidersArrayList = new ArrayList<>();
//
//            firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        User user;
//                        if(task.isSuccessful()) {
//                            user = task.getResult().toObject(User.class);
//                            if (user != null) {
//                                String profile = user.getProfile();
//                                if (profile.equals("Service Receiver")) {
//                                    firebaseFirestore.collection("ServiceProviders").get()
//                                            .addOnCompleteListener(task1 -> {
//                                                if (task1.isSuccessful()) {
//                                                    for (QueryDocumentSnapshot sp : task1.getResult()) {
//                                                        ServiceProvider xx = sp.toObject(ServiceProvider.class);
//                                                        serviceProvidersArrayList.add(xx);
//                                                    }
//                                                } else {
//                                                    Log.i(TAG, task.getException().getMessage());
//                                                }
//                                            });
//
//                                } else if (profile.equals("Service Provider")) {
//                                    firebaseFirestore.collection("ServiceReceivers").get()
//                                            .addOnCompleteListener(task1 -> {
//                                                if (task1.isSuccessful()) {
//                                                    for (QueryDocumentSnapshot sp : task1.getResult()) {
//                                                        ServiceReceiver xx = sp.toObject(ServiceReceiver.class);
//                                                        serviceReceiversArrayList.add(xx);
//                                                    }
//                                                } else {
//                                                    Log.i(TAG, task.getException().getMessage());
//                                                }
//                                            });
//                                } else {
//                                    Toast.makeText(getContext(), "Some error has occured", Toast.LENGTH_LONG);
//                                }
//                            }
//                        }
//                    });
//            return serviceProvidersArrayList;
//        }
//
//    }
}