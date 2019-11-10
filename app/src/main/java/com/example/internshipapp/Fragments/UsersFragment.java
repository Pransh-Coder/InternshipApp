package com.example.internshipapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.internshipapp.Adapter.UserAdapter;
import com.example.internshipapp.R;
import com.example.internshipapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    RecyclerView.LayoutManager layoutManager;
    private List<User> userList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

        System.out.println("Fragment created");
        recyclerView = view.findViewById(R.id.recycler_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //userList=new ArrayList<>();

        setAdapter();
        readUsers();


        return view;
    }

    private void setAdapter() {
        userAdapter = new UserAdapter(getContext(),userList);
        recyclerView.setAdapter(userAdapter);
    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        Log.e("database", reference.toString());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("snapshot",dataSnapshot.toString());
                if (dataSnapshot.exists()){
                    String phone = "" , name = "";
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    {

                        User mUser = new User(childSnapshot.child("id").getValue().toString(),childSnapshot.child("username").getValue().toString(),childSnapshot.child("imageURL").getValue().toString());
                        userList.add(mUser);
                        Log.e("user",(childSnapshot.child("id").getValue().toString()));
                        userAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

